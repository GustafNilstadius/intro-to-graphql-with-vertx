package com.redpilllinpro.graphqlbbldemo.service.book;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;

public class BookServiceVerticle extends AbstractVerticle {
  public void start(Promise<Void> future) throws Exception {
    // Create the service instance
    BookService bookService = BookService.create(vertx);

    // Register the service proxy on event bus
    ServiceBinder serviceBinder = new ServiceBinder(vertx);
    serviceBinder.setAddress(BookService.ADDRESS).register(BookService.class, bookService);


    System.out.printf("%s: BookService reporting for duty.\n", this.toString());
    // Service is deployed and available on the event bus.
    future.complete();
  }
}
