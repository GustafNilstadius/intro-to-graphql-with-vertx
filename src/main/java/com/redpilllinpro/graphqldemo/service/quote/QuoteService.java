package com.redpilllinpro.graphqldemo.service.quote;

import com.redpilllinpro.graphqldemo.service.quote.impl.QuoteServiceImpl;
import com.redpilllinpro.graphqldemo.service.quote.model.Quote;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public interface QuoteService {

  static QuoteService create(Vertx vertx) {
    return new QuoteServiceImpl(vertx);
  }

  public QuoteService getQuote(String id, Handler<AsyncResult<Quote>> resultHandler);
}
