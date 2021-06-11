package com.redpilllinpro.graphqldemo.service.author.impl;

import com.redpilllinpro.graphqldemo.service.author.AuthorService;
import com.redpilllinpro.graphqldemo.service.author.model.Author;
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
    authorData.put("author-0", new Author("author-0", "Joanne", "Rowling", "quote-1"));
    authorData.put("author-1", new Author("author-1", "Herman", "Melville", "quote-2"));
    authorData.put("author-2", new Author("author-2", "William", "Gibson", "quote-3"));
  }

  @Override
  public AuthorService getAuthorByID(String id, Handler<AsyncResult<Author>> resultHandler) {
    System.out.printf("%s: Getting author %s%n", this.toString(), id);
    resultHandler.handle(Future.succeededFuture(authorData.get(id)));
    return this;
  }

  @Override
  public AuthorService addAuthor(Author author, Handler<AsyncResult<Author>> resultHandler) {
    String id = String.format("author-%d", authorData.size());
    author.setId(id);
    authorData.put(author.getId(), author);
    resultHandler.handle(Future.succeededFuture(authorData.get(author.getId())));
    return this;
  }


}
