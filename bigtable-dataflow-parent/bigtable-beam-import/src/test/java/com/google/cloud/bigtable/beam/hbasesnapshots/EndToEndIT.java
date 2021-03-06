/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.beam.hbasesnapshots;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.api.services.storage.model.Objects;
import com.google.bigtable.repackaged.com.google.gson.Gson;
import com.google.cloud.bigtable.beam.hbasesnapshots.ImportJobFromHbaseSnapshot.ImportOptions;
import com.google.cloud.bigtable.beam.sequencefiles.HBaseResultToMutationFn;
import com.google.cloud.bigtable.beam.validation.HadoopHashTableSource.RangeHash;
import com.google.cloud.bigtable.beam.validation.SyncTableJob;
import com.google.cloud.bigtable.beam.validation.SyncTableJob.SyncTableOptions;
import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.PipelineResult.State;
import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.extensions.gcp.util.GcsUtil;
import org.apache.beam.sdk.extensions.gcp.util.gcsfs.GcsPath;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * End to end integration test for pipeline that import HBase snapshot data into Cloud Bigtable and
 * validates the imported data with SyncTable.
 * Prepare test data with gsutil(https://cloud.google.com/storage/docs/quickstart-gsutil):
 * gsutil -m cp -r <PATH_TO_REPO>/bigtable-dataflow-parent/bigtable-beam-import/src/test/integration-test \
 *  gs://<test_bucket>/
 *
 * Setup GCP credential: https://cloud.google.com/docs/authentication
 *  Ensure your credential have access to Bigtable and Dataflow
 *
 * Run with:
 * mvn integration-test -PhbasesnapshotsIntegrationTest \
 * -Dgoogle.bigtable.project.id=<project_id> \
 * -Dgoogle.bigtable.instance.id=<instance_id> \
 * -Dgoogle.dataflow.stagingLocation=gs://<test_bucket>/staging \
 * -Dcloud.test.data.folder=gs://<test_bucket>/integration-test/
 */
public class EndToEndIT {

  private static Logger LOG = LoggerFactory.getLogger(HBaseResultToMutationFn.class);
  private static final String TEST_SNAPSHOT_NAME = "test-snapshot";
  // Location of test data hosted on Google Cloud Storage, for on-cloud dataflow tests.
  private static final String CLOUD_TEST_DATA_FOLDER = "cloud.test.data.folder";
  private static final String DATAFLOW_REGION = "region";

  // Column family name used in all test bigtables.
  private static final String CF = "cf";

  // Full path of the Cloud Storage folder where dataflow jars are uploaded to.
  private static final String GOOGLE_DATAFLOW_STAGING_LOCATION = "google.dataflow.stagingLocation";

  private Connection connection;
  private String projectId;
  private String instanceId;
  private String tableId;
  private String region;

  private GcsUtil gcsUtil;
  private String dataflowStagingLocation;
  private String workDir;
  private byte[][] keySplits;

  // Snapshot data setup
  private String hbaseSnapshotDir;
  private String hashDir;
  private String syncTableOutputDir;

  @Before
  public void setup() throws Exception {
    projectId = getTestProperty(BigtableOptionsFactory.PROJECT_ID_KEY);
    instanceId = getTestProperty(BigtableOptionsFactory.INSTANCE_ID_KEY);
    dataflowStagingLocation = getTestProperty(GOOGLE_DATAFLOW_STAGING_LOCATION);
    region = getTestProperty(DATAFLOW_REGION);
    String cloudTestDataFolder = getTestProperty(CLOUD_TEST_DATA_FOLDER);
    if (!cloudTestDataFolder.endsWith(File.separator)) {
      cloudTestDataFolder = cloudTestDataFolder + File.separator;
    }

    hbaseSnapshotDir = cloudTestDataFolder + "data/";
    UUID test_uuid = UUID.randomUUID();
    hashDir = cloudTestDataFolder + "hashtable/";

    syncTableOutputDir = dataflowStagingLocation;
    if (!syncTableOutputDir.endsWith(File.separator)) {
      syncTableOutputDir = syncTableOutputDir + File.separator;
    }
    syncTableOutputDir = syncTableOutputDir + "sync-table-output/" + test_uuid + "/";

    // Cloud Storage config
    GcpOptions gcpOptions = PipelineOptionsFactory.create().as(GcpOptions.class);
    gcpOptions.setProject(projectId);
    gcsUtil = new GcsUtil.GcsUtilFactory().create(gcpOptions);

    // Bigtable config
    connection = BigtableConfiguration.connect(projectId, instanceId);
    tableId = "test_" + UUID.randomUUID().toString();

    LOG.info("Setting up integration tests");

    String[] keys = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    keySplits = new byte[keys.length][];
    for (int i = 0; i < keys.length; i++) {
      keySplits[i] = keys[i].getBytes();
    }

    // Create table in Bigtable
    TableName tableName = TableName.valueOf(tableId);
    HTableDescriptor descriptor = new HTableDescriptor(tableName);
    descriptor.addFamily(new HColumnDescriptor(CF));
    connection.getAdmin().createTable(descriptor, SnapshotTestingUtils.getSplitKeys());
  }

  private static String getTestProperty(String name) {
    return checkNotNull(System.getProperty(name), "Required property missing: " + name);
  }

  @After
  public void teardown() throws IOException {
    final List<GcsPath> paths = gcsUtil.expand(GcsPath.fromUri(syncTableOutputDir + "/*"));

    if (!paths.isEmpty()) {
      final List<String> pathStrs = new ArrayList<>();

      for (GcsPath path : paths) {
        pathStrs.add(path.toString());
      }
      // TODO: cleanup fails when tests time out. Add a orphan cleaner in the setup()
      // https://github.com/googleapis/java-bigtable/blob/35588d89b9b243eb691a29d3aff16b9f5a08fbb8/google-cloud-bigtable/src/test/java/com/google/cloud/bigtable/test_helpers/env/AbstractTestEnv.java#L108-L119
      this.gcsUtil.remove(pathStrs);
    }

    connection.close();

    // delete test table
    BigtableConfiguration.connect(projectId, instanceId)
        .getAdmin()
        .deleteTable(TableName.valueOf(tableId));
  }

  private SyncTableOptions createSyncTableOptions() {
    DataflowPipelineOptions syncTableOpts =
        PipelineOptionsFactory.as(DataflowPipelineOptions.class);
    syncTableOpts.setRunner(DataflowRunner.class);
    syncTableOpts.setGcpTempLocation(dataflowStagingLocation);
    syncTableOpts.setNumWorkers(1);
    syncTableOpts.setProject(projectId);
    syncTableOpts.setRegion(region);

    SyncTableOptions syncOpts = syncTableOpts.as(SyncTableOptions.class);
    // Setup Bigtable params
    syncOpts.setBigtableProject(StaticValueProvider.of(projectId));
    syncOpts.setBigtableInstanceId(StaticValueProvider.of(instanceId));
    syncOpts.setBigtableTableId(StaticValueProvider.of(tableId));
    syncOpts.setBigtableAppProfileId(null);

    // Setup Hashes
    syncOpts.setHashTableOutputDir(StaticValueProvider.of(hashDir));
    syncOpts.setOutputPrefix(StaticValueProvider.of(syncTableOutputDir));
    return syncOpts;
  }

  private ImportOptions createImportOptions() {
    DataflowPipelineOptions importPipelineOpts =
        PipelineOptionsFactory.as(DataflowPipelineOptions.class);
    importPipelineOpts.setRunner(DataflowRunner.class);
    importPipelineOpts.setGcpTempLocation(dataflowStagingLocation);
    importPipelineOpts.setNumWorkers(1);
    importPipelineOpts.setProject(projectId);
    importPipelineOpts.setRegion(region);

    ImportOptions importOpts = importPipelineOpts.as(ImportOptions.class);

    // setup Bigtable options
    importOpts.setBigtableProject(StaticValueProvider.of(projectId));
    importOpts.setBigtableInstanceId(StaticValueProvider.of(instanceId));
    importOpts.setBigtableTableId(StaticValueProvider.of(tableId));

    // setup HBase snapshot info
    importOpts.setHbaseSnapshotSourceDir(hbaseSnapshotDir);
    importOpts.setSnapshotName(TEST_SNAPSHOT_NAME);
    return importOpts;
  }

  private Map<String, Long> getCountMap(PipelineResult result) {
    MetricQueryResults metrics = result.metrics().allMetrics();
    return StreamSupport.stream(metrics.getCounters().spliterator(), false)
        .collect(Collectors.toMap((m) -> m.getName().getName(), (m) -> m.getAttempted()));
  }

  /**
   * Reads the output of SyncTable job and returns a list of mismatched RangeHashes.
   *
   * @throws IOException
   */
  private List<RangeHash> readMismatchesFromOutputFiles() throws IOException {
    Gson gson = new Gson();
    // Find output files
    List<GcsPath> outputFiles = gcsUtil.expand(GcsPath.fromUri(syncTableOutputDir + "*"));
    List<RangeHash> rangeHashes = new ArrayList<>();

    // Read each file line by line and create a RangeHash from it.
    for (GcsPath outputFile : outputFiles) {
      int size = (int) gcsUtil.fileSize(outputFile);
      byte[] fileContents = new byte[size];
      gcsUtil.open(outputFile).read(ByteBuffer.wrap(fileContents));
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContents)));
      String serializedRangeHash;
      while ((serializedRangeHash = reader.readLine()) != null) {
        try {
          rangeHashes.add(gson.fromJson(serializedRangeHash.trim(), RangeHash.class));
        } catch (Exception e) {
          LOG.error("Failed to parse JSON: [" + serializedRangeHash + "]", e);
          throw e;
        }
      }
    }
    return rangeHashes;
  }

  // Asserts that all the rowKeys belong in mismatches.
  // Throws AssertionException
  private void validateRowInRangeHashes(List<byte[]> rowKeys, Iterable<RangeHash> mismatches) {
    for (byte[] mismatchedRowKey : rowKeys) {
      Assert.assertTrue(containsRow(mismatchedRowKey, mismatches));
    }
  }

  // Returns true if the rowKey belongs in one of the ranges contained in rangeHashes.
  private boolean containsRow(byte[] rowKey, Iterable<RangeHash> rangeHashes) {
    for (RangeHash mismatchedRange : rangeHashes) {
      // TODO: There maybe a better Range.belongs() utility function somewhere?
      // Empty start/end key means that there is no start/end key.
      if ((mismatchedRange.startInclusive.equals(HConstants.EMPTY_BYTE_ARRAY)
              || mismatchedRange.startInclusive.compareTo(rowKey) <= 0)
          && (mismatchedRange.stopExclusive.equals(HConstants.EMPTY_BYTE_ARRAY)
              || mismatchedRange.stopExclusive.compareTo(rowKey) > 0)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void testHBaseSnapshotImport() throws Exception {

    // Start import
    ImportOptions importOpts = createImportOptions();

    // run pipeline
    State state = ImportJobFromHbaseSnapshot.buildPipeline(importOpts).run().waitUntilFinish();
    Assert.assertEquals(State.DONE, state);

    // check that the .restore dir used for temp files has been removed
    Objects objects =
        gcsUtil.listObjects(
            GcsPath.fromUri(hbaseSnapshotDir).getBucket(),
            CleanupHBaseSnapshotRestoreFilesFn.getListPrefix(
                HBaseSnapshotInputConfigBuilder.RESTORE_DIR),
            null);
    Assert.assertNull(objects.getItems());

    SyncTableOptions syncOpts = createSyncTableOptions();

    PipelineResult result = SyncTableJob.buildPipeline(syncOpts).run();
    state = result.waitUntilFinish();
    Assert.assertEquals(State.DONE, state);

    // Read the output files and validate that there are no mismatches.
    Assert.assertEquals(0, readMismatchesFromOutputFiles().size());

    // Validate the counters.
    Map<String, Long> counters = getCountMap(result);
    Assert.assertEquals(counters.get("ranges_matched"), (Long) 101L);
    Assert.assertEquals(counters.get("ranges_not_matched"), (Long) 0L);
  }

  /**
   * Introduces multiple corruptions in imported table and validates that sync-table can detect
   * them.
   */
  @Test
  public void testHBaseSnapshotImportWithCorruptions() throws Exception {
    // Import snapshot
    ImportOptions importOpts = createImportOptions();
    State state = ImportJobFromHbaseSnapshot.buildPipeline(importOpts).run().waitUntilFinish();
    Assert.assertEquals(State.DONE, state);

    // Rows where corruptions will be added.
    byte[] mismatchRowAtStart = "000".getBytes();
    byte[] mismatchRowInMiddle = "24".getBytes();
    byte[] mismatchRowDeleted = "64".getBytes();
    byte[] mismatchRowAtTheEnd = "999".getBytes();

    // Introduce corruptions to the data in Bigtable. Delete data from Bigtable to simulate Bigtable
    // missing data. Add data to Bigtable to simulate extra data in Bigtable. It is easier to update
    // Bigtable than change the snapshots.
    Table table = connection.getTable(TableName.valueOf(tableId));
    Cell cellInMiddle = table.get(new Get(mismatchRowInMiddle)).rawCells()[0];
    List<Put> puts =
        Arrays.asList(
            // Add a row at the start
            new Put(mismatchRowAtStart)
                .addColumn(CF.getBytes(), "random_col".getBytes(), 1L, "value000".getBytes())
                .addColumn(CF.getBytes(), "random_col".getBytes(), 2L, "value001".getBytes()),
            // change a cell in middle
            new Put(cellInMiddle.getRowArray())
                .addColumn(
                    cellInMiddle.getFamilyArray(),
                    cellInMiddle.getQualifierArray(),
                    cellInMiddle.getTimestamp(),
                    "corrupted_val".getBytes()),
            // add a new row in the end
            new Put(mismatchRowAtTheEnd)
                .addColumn(CF.getBytes(), "random_col".getBytes(), 100L, "value999".getBytes()));

    table.put(puts);
    // Delete a random row in the middle. We should see 4 ranges mismatch as table is split on
    // 1,2...9. All the updates are happening on a different split.
    table.delete(new Delete(mismatchRowDeleted));

    // Run SyncTable job and expect 4 mismatches.
    SyncTableOptions syncOpts = createSyncTableOptions();
    PipelineResult result = SyncTableJob.buildPipeline(syncOpts).run();
    state = result.waitUntilFinish();
    Assert.assertEquals(State.DONE, state);

    List<RangeHash> syncTableOutputMismatches = readMismatchesFromOutputFiles();
    Assert.assertEquals(4, syncTableOutputMismatches.size());
    validateRowInRangeHashes(
        Arrays.asList(
            mismatchRowAtStart, mismatchRowAtTheEnd, mismatchRowDeleted, mismatchRowInMiddle),
        syncTableOutputMismatches);

    // Assert that the output collection is the right one.
    Map<String, Long> counters = getCountMap(result);
    Assert.assertEquals(counters.get("ranges_matched"), (Long) 97L);
    Assert.assertEquals(counters.get("ranges_not_matched"), (Long) 4L);
  }
}
