package com.redpilllinpro.graphqlbbldemo.service.author.impl;

import com.redpilllinpro.graphqlbbldemo.service.author.AuthorService;
import com.redpilllinpro.graphqlbbldemo.service.author.model.Author;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.HashMap;
import java.util.Map;

public class AuthorServiceImpl implements AuthorService {

  private Map<String, Author> authorData;

  public AuthorServiceImpl(Vertx vertx) {
    authorData = new HashMap<>();
    authorData.put("author-1", new Author("author-1", "Joanne", "Rowling", "quote-1"));
    authorData.put("author-2", new Author("author-2", "Herman", "Melville", "quote-2"));
    authorData.put("author-3", new Author("author-3", "William", "Gibson", "quote-3"));
  }

  @Override
  public AuthorService getAuthorByID(String id, Handler<AsyncResult<Author>> resultHandler) {
    System.out.printf("%s: Getting author %s%n", this.toString(), id);
    resultHandler.handle(Future.succeededFuture(authorData.get(id)));
    return this;
  }
}
