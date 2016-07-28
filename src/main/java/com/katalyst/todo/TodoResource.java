package com.katalyst.todo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.base.Joiner;

@Path("api/todos")
public class TodoResource {
  private final Greeter greeter;
  private static List<String> todos = new ArrayList<>();

  @Inject
  public TodoResource(final Greeter greeter) {
    this.greeter = greeter;
  }

  @GET
  public String getTodos() {
    return Joiner.on(", ").join(todos);
  }

  @POST
  @Consumes("text/plain")
  public Response addTodo(String todo) {
    todos.add(todo);
    return Response.status(201).location(URI.create("blah")).build();
  }

  @DELETE
  @Path("{todo_id}")
  public Response deleteTodos(@PathParam("todo_id") final String name) {
    return Response.status(201).build();
  }
}