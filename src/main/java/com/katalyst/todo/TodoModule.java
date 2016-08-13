package com.katalyst.todo;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.katalyst.toto.resources.Resourses.Todo;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.sync.RedisCommands;

public class TodoModule extends AbstractModule {
  @Override
  public void configure() {
    bind(TodoResource.class);
  }

  @Provides
  @Singleton
  RedisCommands<String, String> provideStringRedisClient() {
    RedisURI redisUri= RedisURI.Builder.redis("redis").withPort(6379).build();
    return RedisClient.create(redisUri).connect().sync();
  }

  @Provides
  @Singleton
  RedisCommands<String, Todo> provideTodoRedisClient() {
    RedisURI redisUri= RedisURI.Builder.redis("redis").withPort(6379).build();
    return RedisClient
        .create(redisUri)
        .connect(new ProtobufMessageRedisCodec<Todo>(Todo.getDefaultInstance()))
        .sync();
  }
}
