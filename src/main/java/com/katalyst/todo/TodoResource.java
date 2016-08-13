package com.katalyst.todo;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.Timestamps;
import com.katalyst.toto.resources.Resourses.Todo;
import com.lambdaworks.redis.api.sync.RedisCommands;

@Path("api/todos")
public class TodoResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(Todo.class);
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Todo.class, new TodoTypeAdapter())
      .create();

  private final RedisCommands<String, String> stringCommands;
  private final RedisCommands<String, Todo> todoCommands;

  @Inject
  private TodoResource(
      RedisCommands<String, String> stringCommands, RedisCommands<String, Todo> todoCommands) {
    this.stringCommands = stringCommands;
    this.todoCommands = todoCommands;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getTodos() {
    Set<String> usersTodosIds = stringCommands.smembers("1");
    LOGGER.info("USER IDS: " + Joiner.on(",").join(usersTodosIds));

    List<Todo> usersTodos = usersTodosIds.isEmpty()
        ? ImmutableList.<Todo>of()
        : todoCommands.mget(usersTodosIds.toArray(new String[usersTodosIds.size()]));
    LOGGER.info(Joiner.on(",").join(usersTodos));
    return GSON.toJson(usersTodos);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addTodo(String jsonTodo) throws InvalidProtocolBufferException {
    String id = UUID.randomUUID().toString();

    Todo.Builder todo = Todo.newBuilder();
    JsonFormat.parser().merge(jsonTodo, todo);
    todo.getMetadataBuilder()
        .setId(id)
        .setUserId("1")
        .setTimestamp(Timestamps.fromMillis(Instant.now().toEpochMilli()));

    LOGGER.info("1: " + Joiner.on(",").join(stringCommands.smembers("1")));
    stringCommands.sadd("1", id);
    LOGGER.info("1: " + Joiner.on(",").join(stringCommands.smembers("1")));

    LOGGER.info(id + ": " + todoCommands.exists(id));
    todoCommands.set(id, todo.build());
    LOGGER.info(id + ": " + todoCommands.exists(id));
    return Response.status(Response.Status.OK).location(URI.create(id)).build();
  }

  @DELETE
  @Path("{todo_id}")
  public Response deleteTodos(@PathParam("todo_id") final String id) {
    if (todoCommands.exists(new String[] {id}) != 0 &&
        stringCommands.sismember("1", id)) {
      LOGGER.info("DELETING: " + id);
      stringCommands.srem("1", id);
      todoCommands.del(id);
      LOGGER.info("In user todos: "+ stringCommands.sismember("1", id));
      LOGGER.info(id + ": " + todoCommands.exists(id));
      return Response.status(Response.Status.OK).build();
    } else {
      LOGGER.info("NOT FOUND: " + id);
      return Response.status(Response.Status.GONE).build();
    }
  }
}