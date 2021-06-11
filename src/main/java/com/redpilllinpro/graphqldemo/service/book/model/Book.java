package com.redpilllinpro.graphqldemo.service.book.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Book {
  private String id;
  private String name;
  private Integer pageCount;
  private String author;

  public Book(String id, String name, Integer pageCount, String author) {
    this.id = id;
    this.name = name;
    this.pageCount = pageCount;
    this.author = author;
  }

  public Book(JsonObject jsonObject) {
    this.id = jsonObject.getString("id", null);
    this.name = jsonObject.getString("name");
    this.pageCount = jsonObject.getInteger("pageCount");
    this.author = jsonObject.getString("author");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPageCount() {
    return pageCount;
  }

  public void setPageCount(Integer pageCount) {
    this.pageCount = pageCount;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public JsonObject toJson() {
    return new JsonObject()
      .put("id", this.id)
      .put("name", this.name)
      .put("pageCount", this.pageCount)
      .put("author", this.author);
  }
}
