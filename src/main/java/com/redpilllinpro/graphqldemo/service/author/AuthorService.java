package com.redpilllinpro.graphqldemo.service.author;

import com.redpilllinpro.graphqldemo.service.author.impl.AuthorServiceImpl;
import com.redpilllinpro.graphqldemo.service.author.model.Author;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;


public interface AuthorService {

  static AuthorService create(Vertx vertx) {
    return new AuthorServiceImpl(vertx);
  }

  public AuthorService getAuthorByID(String id, Handler<AsyncResult<Author>> resultHandler);

  AuthorService addAuthor(Author author, Handler<AsyncResult<Author>> resultHandler);

}
