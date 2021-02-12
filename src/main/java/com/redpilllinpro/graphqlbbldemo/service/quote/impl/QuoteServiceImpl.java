package com.redpilllinpro.graphqlbbldemo.service.quote.impl;

import com.redpilllinpro.graphqlbbldemo.service.quote.QuoteService;
import com.redpilllinpro.graphqlbbldemo.service.quote.model.Quote;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;

public class QuoteServiceImpl implements QuoteService {
  private WebClient client;

  public QuoteServiceImpl(Vertx vertx) {
    this.client = WebClient.create(vertx);;
  }

  @Override
  public QuoteService getQuote(String id, Handler<AsyncResult<Quote>> resultHandler) {
    System.out.println(this.toString() + ": Getting quote");
    client
      .get(443, "api.chucknorris.io", "/jokes/random").ssl(true).send()
      .onSuccess(response -> {
        if (response.statusCode() == 200) {
          resultHandler.handle(Future.succeededFuture(new Quote(response.bodyAsJsonObject())));
        } else {
          resultHandler.handle(Future.failedFuture(new Exception("Error getting quote.")));
        }
      }).onFailure(err -> {
       err.printStackTrace();
       resultHandler.handle(Future.failedFuture(err));
    });
    return this;
  }
}
