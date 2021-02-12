package com.redpilllinpro.graphqlbbldemo.service.author;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;

public class AuthorServiceVeritcle extends AbstractVerticle {

  public void start(Promise<Void> future) {
    try {
      // Create the service instance
      AuthorService authorService = AuthorService.create(vertx);

      // Register the service proxy on event bus
      ServiceBinder serviceBinder = new ServiceBinder(vertx);
      serviceBinder.setAddress(AuthorService.ADDRESS).register(AuthorService.class, authorService);

      System.out.printf("%s: AuthorService reporting for duty.\n", this.toString());
      // Service is deployed and available on the event bus.
      future.complete();
    } catch (Exception e) {
      e.printStackTrace();
      future.fail(e);
    }

  }

}
