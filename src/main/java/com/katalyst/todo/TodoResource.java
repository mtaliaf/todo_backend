package com.katalyst.todo;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.Timestamps;
import com.katalyst.toto.resources.Resourses.Todo;

@Path("api/todos")
public class TodoResource {
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Todo.class, new TodoTypeAdapter())
      .create();

  private static Map<String, Todo> todos = new HashMap<>();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getTodos() {
    return GSON.toJson(todos.values());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addTodo(String jsonTodo) throws InvalidProtocolBufferException {
    String id = UUID.randomUUID().toString();

    Todo.Builder todo = Todo.newBuilder();
    JsonFormat.parser().merge(jsonTodo, todo);
    todo.getMetadataBuilder()
        .setId(id)
        .setTimestamp(Timestamps.fromMillis(Instant.now().toEpochMilli()));

    todos.put(id, todo.build());
    return Response.status(Response.Status.OK).location(URI.create(id)).build();
  }

  @DELETE
  @Path("{todo_id}")
  public Response deleteTodos(@PathParam("todo_id") final String id) {
    if (todos.containsKey(id)) {
      todos.remove(id);
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.GONE).build();
    }
  }

  private class MessageSerializer implements JsonSerializer<Message> {
    @Override
    public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
      try {
        return new JsonPrimitive(JsonFormat.printer().print(src));
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException(e);
      }
    }
  }
}