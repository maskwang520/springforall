// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: model.proto

package com.alibaba.dubbo.performance.demo.agent.netty.model.proto;

public final class ProtoMessage {
  private ProtoMessage() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ProtoBufRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ProtoBufRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ProtoBufResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ProtoBufResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013model.proto\"x\n\017ProtoBufRequest\022\021\n\trequ" +
      "estId\030\001 \001(\005\022\016\n\006method\030\002 \001(\t\022\021\n\tinterface" +
      "\030\003 \001(\t\022\034\n\024parameterTypesString\030\004 \001(\t\022\021\n\t" +
      "parameter\030\005 \001(\t\"4\n\020ProtoBufResponse\022\021\n\tr" +
      "equestId\030\001 \001(\005\022\r\n\005value\030\002 \001(\tBL\n:com.ali" +
      "baba.dubbo.performance.demo.agent.netty." +
      "model.protoB\014ProtoMessageP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_ProtoBufRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ProtoBufRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ProtoBufRequest_descriptor,
        new java.lang.String[] { "RequestId", "Method", "Interface", "ParameterTypesString", "Parameter", });
    internal_static_ProtoBufResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_ProtoBufResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ProtoBufResponse_descriptor,
        new java.lang.String[] { "RequestId", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
