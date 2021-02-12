package com.redpilllinpro.graphqlbbldemo.service.quote;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;

public class QuoteServiceVerticle extends AbstractVerticle {

  public void start(Promise<Void> future) throws Exception {
    // Create the service instance
    QuoteService quoteService = QuoteService.create(vertx);

    // Register the service proxy on event bus
    ServiceBinder serviceBinder = new ServiceBinder(vertx);
    serviceBinder.setAddress(QuoteService.ADDRESS).register(QuoteService.class, quoteService);


    System.out.printf("%s: QuoteService reporting for duty.\n", this.toString());
    // Service is deployed and available on the event bus.
    future.complete();
  }

}
