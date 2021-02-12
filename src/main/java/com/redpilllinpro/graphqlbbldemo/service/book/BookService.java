package com.redpilllinpro.graphqlbbldemo.service.book;


import com.redpilllinpro.graphqlbbldemo.service.book.impl.BookServiceImpl;
import com.redpilllinpro.graphqlbbldemo.service.book.model.Book;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.List;

@ProxyGen
public interface BookService {
  static final String ADDRESS = "book.service";

  // A couple of factory methods to create an instance and a proxy
  @ProxyIgnore
  static BookService create(Vertx vertx) {
    return new BookServiceImpl(vertx);
  }

  @ProxyIgnore
  static BookService createProxy(Vertx vertx) {
    return new BookServiceVertxEBProxy(vertx, ADDRESS);
  }


  @Fluent
  public BookService getBookById(String id, Handler<AsyncResult<Book>> resultHandler);

  @Fluent
  public BookService getBooks(Handler<AsyncResult<List<Book>>> resultHandler);


}
