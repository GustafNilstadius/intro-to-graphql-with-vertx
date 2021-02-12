package com.redpilllinpro.graphqlbbldemo.service.author;

import com.redpilllinpro.graphqlbbldemo.service.author.impl.AuthorServiceImpl;
import com.redpilllinpro.graphqlbbldemo.service.author.model.Author;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

@ProxyGen
public interface AuthorService {

  static final String ADDRESS = "author.service";

  // A couple of factory methods to create an instance and a proxy
  @ProxyIgnore
  static AuthorService create(Vertx vertx) {
    return new AuthorServiceImpl(vertx);
  }

  @ProxyIgnore
  static AuthorService createProxy(Vertx vertx) {
    return new AuthorServiceVertxEBProxy(vertx, ADDRESS);
  }

  @Fluent
  public AuthorService getAuthorByID(String id, Handler<AsyncResult<Author>> resultHandler);


}
