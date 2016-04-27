// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/bigtable/v2/data.proto

package com.google.bigtable.v2;

/**
 * Protobuf type {@code google.bigtable.v2.ReadModifyWriteRule}
 *
 * <pre>
 * Specifies an atomic read/modify/write operation on the latest value of the
 * specified column.
 * </pre>
 */
public  final class ReadModifyWriteRule extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:google.bigtable.v2.ReadModifyWriteRule)
    ReadModifyWriteRuleOrBuilder {
  // Use ReadModifyWriteRule.newBuilder() to construct.
  private ReadModifyWriteRule(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private ReadModifyWriteRule() {
    familyName_ = "";
    columnQualifier_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private ReadModifyWriteRule(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            String s = input.readStringRequireUtf8();

            familyName_ = s;
            break;
          }
          case 18: {

            columnQualifier_ = input.readBytes();
            break;
          }
          case 26: {
            ruleCase_ = 3;
            rule_ = input.readBytes();
            break;
          }
          case 32: {
            ruleCase_ = 4;
            rule_ = input.readInt64();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw new RuntimeException(e.setUnfinishedMessage(this));
    } catch (java.io.IOException e) {
      throw new RuntimeException(
          new com.google.protobuf.InvalidProtocolBufferException(
              e.getMessage()).setUnfinishedMessage(this));
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.bigtable.v2.DataProto.internal_static_google_bigtable_v2_ReadModifyWriteRule_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.bigtable.v2.DataProto.internal_static_google_bigtable_v2_ReadModifyWriteRule_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.bigtable.v2.ReadModifyWriteRule.class, com.google.bigtable.v2.ReadModifyWriteRule.Builder.class);
  }

  private int ruleCase_ = 0;
  private java.lang.Object rule_;
  public enum RuleCase
      implements com.google.protobuf.Internal.EnumLite {
    APPEND_VALUE(3),
    INCREMENT_AMOUNT(4),
    RULE_NOT_SET(0);
    private int value = 0;
    private RuleCase(int value) {
      this.value = value;
    }
    public static RuleCase valueOf(int value) {
      switch (value) {
        case 3: return APPEND_VALUE;
        case 4: return INCREMENT_AMOUNT;
        case 0: return RULE_NOT_SET;
        default: throw new java.lang.IllegalArgumentException(
          "Value is undefined for this oneof enum.");
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public RuleCase
  getRuleCase() {
    return RuleCase.valueOf(
        ruleCase_);
  }

  public static final int FAMILY_NAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object familyName_;
  /**
   * <code>optional string family_name = 1;</code>
   *
   * <pre>
   * The name of the family to which the read/modify/write should be applied.
   * Must match [-_.a-zA-Z0-9]+
   * </pre>
   */
  public java.lang.String getFamilyName() {
    java.lang.Object ref = familyName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      familyName_ = s;
      return s;
    }
  }
  /**
   * <code>optional string family_name = 1;</code>
   *
   * <pre>
   * The name of the family to which the read/modify/write should be applied.
   * Must match [-_.a-zA-Z0-9]+
   * </pre>
   */
  public com.google.protobuf.ByteString
      getFamilyNameBytes() {
    java.lang.Object ref = familyName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      familyName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int COLUMN_QUALIFIER_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString columnQualifier_;
  /**
   * <code>optional bytes column_qualifier = 2;</code>
   *
   * <pre>
   * The qualifier of the column to which the read/modify/write should be
   * applied.
   * Can be any byte string, including the empty string.
   * </pre>
   */
  public com.google.protobuf.ByteString getColumnQualifier() {
    return columnQualifier_;
  }

  public static final int APPEND_VALUE_FIELD_NUMBER = 3;
  /**
   * <code>optional bytes append_value = 3;</code>
   *
   * <pre>
   * Rule specifying that "append_value" be appended to the existing value.
   * If the targeted cell is unset, it will be treated as containing the
   * empty string.
   * </pre>
   */
  public com.google.protobuf.ByteString getAppendValue() {
    if (ruleCase_ == 3) {
      return (com.google.protobuf.ByteString) rule_;
    }
    return com.google.protobuf.ByteString.EMPTY;
  }

  public static final int INCREMENT_AMOUNT_FIELD_NUMBER = 4;
  /**
   * <code>optional int64 increment_amount = 4;</code>
   *
   * <pre>
   * Rule specifying that "increment_amount" be added to the existing value.
   * If the targeted cell is unset, it will be treated as containing a zero.
   * Otherwise, the targeted cell must contain an 8-byte value (interpreted
   * as a 64-bit big-endian signed integer), or the entire request will fail.
   * </pre>
   */
  public long getIncrementAmount() {
    if (ruleCase_ == 4) {
      return (java.lang.Long) rule_;
    }
    return 0L;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getFamilyNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessage.writeString(output, 1, familyName_);
    }
    if (!columnQualifier_.isEmpty()) {
      output.writeBytes(2, columnQualifier_);
    }
    if (ruleCase_ == 3) {
      output.writeBytes(
          3, (com.google.protobuf.ByteString)((com.google.protobuf.ByteString) rule_));
    }
    if (ruleCase_ == 4) {
      output.writeInt64(
          4, (long)((java.lang.Long) rule_));
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getFamilyNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessage.computeStringSize(1, familyName_);
    }
    if (!columnQualifier_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, columnQualifier_);
    }
    if (ruleCase_ == 3) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(
            3, (com.google.protobuf.ByteString)((com.google.protobuf.ByteString) rule_));
    }
    if (ruleCase_ == 4) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(
            4, (long)((java.lang.Long) rule_));
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.bigtable.v2.ReadModifyWriteRule parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.bigtable.v2.ReadModifyWriteRule prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessage.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code google.bigtable.v2.ReadModifyWriteRule}
   *
   * <pre>
   * Specifies an atomic read/modify/write operation on the latest value of the
   * specified column.
   * </pre>
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.bigtable.v2.ReadModifyWriteRule)
      com.google.bigtable.v2.ReadModifyWriteRuleOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.bigtable.v2.DataProto.internal_static_google_bigtable_v2_ReadModifyWriteRule_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.bigtable.v2.DataProto.internal_static_google_bigtable_v2_ReadModifyWriteRule_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.bigtable.v2.ReadModifyWriteRule.class, com.google.bigtable.v2.ReadModifyWriteRule.Builder.class);
    }

    // Construct using com.google.bigtable.v2.ReadModifyWriteRule.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      familyName_ = "";

      columnQualifier_ = com.google.protobuf.ByteString.EMPTY;

      ruleCase_ = 0;
      rule_ = null;
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.bigtable.v2.DataProto.internal_static_google_bigtable_v2_ReadModifyWriteRule_descriptor;
    }

    public com.google.bigtable.v2.ReadModifyWriteRule getDefaultInstanceForType() {
      return com.google.bigtable.v2.ReadModifyWriteRule.getDefaultInstance();
    }

    public com.google.bigtable.v2.ReadModifyWriteRule build() {
      com.google.bigtable.v2.ReadModifyWriteRule result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.bigtable.v2.ReadModifyWriteRule buildPartial() {
      com.google.bigtable.v2.ReadModifyWriteRule result = new com.google.bigtable.v2.ReadModifyWriteRule(this);
      result.familyName_ = familyName_;
      result.columnQualifier_ = columnQualifier_;
      if (ruleCase_ == 3) {
        result.rule_ = rule_;
      }
      if (ruleCase_ == 4) {
        result.rule_ = rule_;
      }
      result.ruleCase_ = ruleCase_;
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.bigtable.v2.ReadModifyWriteRule) {
        return mergeFrom((com.google.bigtable.v2.ReadModifyWriteRule)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.bigtable.v2.ReadModifyWriteRule other) {
      if (other == com.google.bigtable.v2.ReadModifyWriteRule.getDefaultInstance()) return this;
      if (!other.getFamilyName().isEmpty()) {
        familyName_ = other.familyName_;
        onChanged();
      }
      if (other.getColumnQualifier() != com.google.protobuf.ByteString.EMPTY) {
        setColumnQualifier(other.getColumnQualifier());
      }
      switch (other.getRuleCase()) {
        case APPEND_VALUE: {
          setAppendValue(other.getAppendValue());
          break;
        }
        case INCREMENT_AMOUNT: {
          setIncrementAmount(other.getIncrementAmount());
          break;
        }
        case RULE_NOT_SET: {
          break;
        }
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.google.bigtable.v2.ReadModifyWriteRule parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.bigtable.v2.ReadModifyWriteRule) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int ruleCase_ = 0;
    private java.lang.Object rule_;
    public RuleCase
        getRuleCase() {
      return RuleCase.valueOf(
          ruleCase_);
    }

    public Builder clearRule() {
      ruleCase_ = 0;
      rule_ = null;
      onChanged();
      return this;
    }


    private java.lang.Object familyName_ = "";
    /**
     * <code>optional string family_name = 1;</code>
     *
     * <pre>
     * The name of the family to which the read/modify/write should be applied.
     * Must match [-_.a-zA-Z0-9]+
     * </pre>
     */
    public java.lang.String getFamilyName() {
      java.lang.Object ref = familyName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        familyName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string family_name = 1;</code>
     *
     * <pre>
     * The name of the family to which the read/modify/write should be applied.
     * Must match [-_.a-zA-Z0-9]+
     * </pre>
     */
    public com.google.protobuf.ByteString
        getFamilyNameBytes() {
      java.lang.Object ref = familyName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        familyName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string family_name = 1;</code>
     *
     * <pre>
     * The name of the family to which the read/modify/write should be applied.
     * Must match [-_.a-zA-Z0-9]+
     * </pre>
     */
    public Builder setFamilyName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      familyName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string family_name = 1;</code>
     *
     * <pre>
     * The name of the family to which the read/modify/write should be applied.
     * Must match [-_.a-zA-Z0-9]+
     * </pre>
     */
    public Builder clearFamilyName() {
      
      familyName_ = getDefaultInstance().getFamilyName();
      onChanged();
      return this;
    }
    /**
     * <code>optional string family_name = 1;</code>
     *
     * <pre>
     * The name of the family to which the read/modify/write should be applied.
     * Must match [-_.a-zA-Z0-9]+
     * </pre>
     */
    public Builder setFamilyNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      familyName_ = value;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString columnQualifier_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>optional bytes column_qualifier = 2;</code>
     *
     * <pre>
     * The qualifier of the column to which the read/modify/write should be
     * applied.
     * Can be any byte string, including the empty string.
     * </pre>
     */
    public com.google.protobuf.ByteString getColumnQualifier() {
      return columnQualifier_;
    }
    /**
     * <code>optional bytes column_qualifier = 2;</code>
     *
     * <pre>
     * The qualifier of the column to which the read/modify/write should be
     * applied.
     * Can be any byte string, including the empty string.
     * </pre>
     */
    public Builder setColumnQualifier(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      columnQualifier_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bytes column_qualifier = 2;</code>
     *
     * <pre>
     * The qualifier of the column to which the read/modify/write should be
     * applied.
     * Can be any byte string, including the empty string.
     * </pre>
     */
    public Builder clearColumnQualifier() {
      
      columnQualifier_ = getDefaultInstance().getColumnQualifier();
      onChanged();
      return this;
    }

    /**
     * <code>optional bytes append_value = 3;</code>
     *
     * <pre>
     * Rule specifying that "append_value" be appended to the existing value.
     * If the targeted cell is unset, it will be treated as containing the
     * empty string.
     * </pre>
     */
    public com.google.protobuf.ByteString getAppendValue() {
      if (ruleCase_ == 3) {
        return (com.google.protobuf.ByteString) rule_;
      }
      return com.google.protobuf.ByteString.EMPTY;
    }
    /**
     * <code>optional bytes append_value = 3;</code>
     *
     * <pre>
     * Rule specifying that "append_value" be appended to the existing value.
     * If the targeted cell is unset, it will be treated as containing the
     * empty string.
     * </pre>
     */
    public Builder setAppendValue(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ruleCase_ = 3;
      rule_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional bytes append_value = 3;</code>
     *
     * <pre>
     * Rule specifying that "append_value" be appended to the existing value.
     * If the targeted cell is unset, it will be treated as containing the
     * empty string.
     * </pre>
     */
    public Builder clearAppendValue() {
      if (ruleCase_ == 3) {
        ruleCase_ = 0;
        rule_ = null;
        onChanged();
      }
      return this;
    }

    /**
     * <code>optional int64 increment_amount = 4;</code>
     *
     * <pre>
     * Rule specifying that "increment_amount" be added to the existing value.
     * If the targeted cell is unset, it will be treated as containing a zero.
     * Otherwise, the targeted cell must contain an 8-byte value (interpreted
     * as a 64-bit big-endian signed integer), or the entire request will fail.
     * </pre>
     */
    public long getIncrementAmount() {
      if (ruleCase_ == 4) {
        return (java.lang.Long) rule_;
      }
      return 0L;
    }
    /**
     * <code>optional int64 increment_amount = 4;</code>
     *
     * <pre>
     * Rule specifying that "increment_amount" be added to the existing value.
     * If the targeted cell is unset, it will be treated as containing a zero.
     * Otherwise, the targeted cell must contain an 8-byte value (interpreted
     * as a 64-bit big-endian signed integer), or the entire request will fail.
     * </pre>
     */
    public Builder setIncrementAmount(long value) {
      ruleCase_ = 4;
      rule_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional int64 increment_amount = 4;</code>
     *
     * <pre>
     * Rule specifying that "increment_amount" be added to the existing value.
     * If the targeted cell is unset, it will be treated as containing a zero.
     * Otherwise, the targeted cell must contain an 8-byte value (interpreted
     * as a 64-bit big-endian signed integer), or the entire request will fail.
     * </pre>
     */
    public Builder clearIncrementAmount() {
      if (ruleCase_ == 4) {
        ruleCase_ = 0;
        rule_ = null;
        onChanged();
      }
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:google.bigtable.v2.ReadModifyWriteRule)
  }

  // @@protoc_insertion_point(class_scope:google.bigtable.v2.ReadModifyWriteRule)
  private static final com.google.bigtable.v2.ReadModifyWriteRule DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.bigtable.v2.ReadModifyWriteRule();
  }

  public static com.google.bigtable.v2.ReadModifyWriteRule getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ReadModifyWriteRule>
      PARSER = new com.google.protobuf.AbstractParser<ReadModifyWriteRule>() {
    public ReadModifyWriteRule parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new ReadModifyWriteRule(input, extensionRegistry);
      } catch (RuntimeException e) {
        if (e.getCause() instanceof
            com.google.protobuf.InvalidProtocolBufferException) {
          throw (com.google.protobuf.InvalidProtocolBufferException)
              e.getCause();
        }
        throw e;
      }
    }
  };

  public static com.google.protobuf.Parser<ReadModifyWriteRule> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ReadModifyWriteRule> getParserForType() {
    return PARSER;
  }

  public com.google.bigtable.v2.ReadModifyWriteRule getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
