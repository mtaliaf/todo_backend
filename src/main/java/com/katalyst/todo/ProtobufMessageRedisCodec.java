package com.katalyst.todo;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.lambdaworks.redis.codec.RedisCodec;
import com.lambdaworks.redis.codec.Utf8StringCodec;

public class ProtobufMessageRedisCodec<T extends Message>
    implements RedisCodec<String, T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufMessageRedisCodec.class);

  private static Utf8StringCodec stringCodex = new Utf8StringCodec();
  private final T defaultInstance;

  public ProtobufMessageRedisCodec(T defaultInstance) {
    this.defaultInstance = defaultInstance;
  }

  @Override
  public String decodeKey(ByteBuffer bytes) {
    return stringCodex.decodeKey(bytes);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T decodeValue(ByteBuffer bytes) {
    byte[] byteArray = new byte[bytes.remaining()];
    bytes.duplicate().get(byteArray);
    try {
      // this cast should always be safe
      return (T) defaultInstance.newBuilderForType()
          .mergeFrom(byteArray)
          .build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException("Unable to parse protobuf", e);
    }
  }

  @Override
  public ByteBuffer encodeKey(String key) {
    return stringCodex.encodeKey(key);
  }

  @Override
  public ByteBuffer encodeValue(T value) {
    return ByteBuffer.wrap(value.toByteArray());
  }


}
