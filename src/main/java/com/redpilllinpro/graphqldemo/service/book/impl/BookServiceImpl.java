package com.redpilllinpro.graphqldemo.service.book.impl;

import com.redpilllinpro.graphqldemo.service.book.BookService;
import com.redpilllinpro.graphqldemo.service.book.model.Book;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookServiceImpl implements BookService {

  private Map<String, Book> booksData;

  public BookServiceImpl(Vertx vertx) {
    this.booksData = new HashMap<>();
    booksData.put("book-0", new Book( "book-0", "Harry Potter and the snek", 323, "author-0"));
    booksData.put("book-1", new Book( "book-1", "Harry Potter and the Philosopher's Stone", 223, "author-0"));
    booksData.put("book-2", new Book( "book-2", "Moby Dick", 635, "author-1"));
    booksData.put("book-3", new Book( "book-3", "Neuromancer", 276, "author-2"));
    booksData.put("book-4", new Book( "book-4", "Count Zero", 225, "author-2"));
    booksData.put("book-5", new Book( "book-5", "Mona Lisa Overdrive", 420, "author-2"));
  }

  @Override
  public BookService getBookById(String id, Handler<AsyncResult<Book>> resultHandler) {
    System.out.printf("%s: Getting book %%n", this.toString(), id);
    resultHandler.handle(Future.succeededFuture(booksData.get(id)));
    return this;
  }

  @Override
  public BookService getBooks(Handler<AsyncResult<List<Book>>> resultHandler) {
    System.out.println(this.toString() + ": Getting books");
    resultHandler.handle(Future.succeededFuture(new ArrayList<>(booksData.values())));
    return this;
  }

  @Override
  public BookService addBook(Book book, Handler<AsyncResult<Book>> resultHandler) {
    String id = String.format("book-%d", booksData.size());
    book.setId(id);
    booksData.put(book.getId(), book);
    resultHandler.handle(Future.succeededFuture(booksData.get(book.getId())));
    return this;
  }
}
