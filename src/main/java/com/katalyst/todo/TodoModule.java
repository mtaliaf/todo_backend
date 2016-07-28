package com.katalyst.todo;

import com.google.inject.AbstractModule;

public class TodoModule extends AbstractModule {
  @Override
  public void configure() {
    bind(HelloResource.class);
    bind(TodoResource.class);
    bind(Greeter.class).to(DefaultGreeter.class);
  }
}
