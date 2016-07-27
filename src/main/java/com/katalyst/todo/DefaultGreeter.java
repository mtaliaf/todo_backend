package com.katalyst.todo;

public class DefaultGreeter implements Greeter {
  @Override
  public String greet(final String name) {
    return "Hello " + name;
  }
}
