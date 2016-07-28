package com.katalyst.todo;

import javax.servlet.ServletContextEvent;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseListener implements javax.servlet.ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setServiceAccount(sce.getServletContext().getResourceAsStream("/WEB-INF/firebase.json"))
        .setDatabaseUrl("https://todo-backend-e8750.firebaseio.com/")
        .build();
    FirebaseApp.initializeApp(options);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // Do nothing
  }
}
