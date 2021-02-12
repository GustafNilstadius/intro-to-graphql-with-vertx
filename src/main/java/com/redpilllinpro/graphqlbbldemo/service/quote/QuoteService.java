package com.redpilllinpro.graphqlbbldemo.service.quote;

import com.redpilllinpro.graphqlbbldemo.service.quote.impl.QuoteServiceImpl;
import com.redpilllinpro.graphqlbbldemo.service.quote.model.Quote;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

@ProxyGen
public interface QuoteService {

  static final String ADDRESS = "quote.service";

  // A couple of factory methods to create an instance and a proxy
  @ProxyIgnore
  static QuoteService create(Vertx vertx) {
    return new QuoteServiceImpl(vertx);
  }

  @ProxyIgnore
  static QuoteService createProxy(Vertx vertx) {
    return new QuoteServiceVertxEBProxy(vertx, ADDRESS);
  }

  @Fluent
  public QuoteService getQuote(String id, Handler<AsyncResult<Quote>> resultHandler);
}
