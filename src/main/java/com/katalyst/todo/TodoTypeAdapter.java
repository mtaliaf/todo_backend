package com.katalyst.todo;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.katalyst.toto.resources.Resourses.Todo;

public class TodoTypeAdapter
    implements JsonSerializer<Todo>, JsonDeserializer<Todo> {

  @Override
  public Todo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    try {
      Todo.Builder todo = Todo.newBuilder();
      JsonFormat.parser().merge(json.getAsJsonPrimitive().getAsString(), todo);
      return todo.build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public JsonElement serialize(Todo src, Type typeOfSrc, JsonSerializationContext context) {
    try {
      return new JsonPrimitive(JsonFormat.printer().omittingInsignificantWhitespace().print(src));
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

}
