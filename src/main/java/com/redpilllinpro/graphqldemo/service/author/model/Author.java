package com.redpilllinpro.graphqldemo.service.author.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Author {
  private String id;
  private String firstName;
  private String lastName;
  private String quote;

  public Author(String id, String firstName, String lastName, String quote) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.quote = quote;
  }

  public Author(JsonObject jsonObject) {
    this.id = jsonObject.getString("id");
    this.firstName = jsonObject.getString("firstName");
    this.lastName = jsonObject.getString("lastName");
    this.quote = jsonObject.getString("quote");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getQuote() {
    return quote;
  }

  public void setQuote(String quote) {
    this.quote = quote;
  }

  public JsonObject toJson() {
    return new JsonObject()
      .put("id", this.id)
      .put("firstName", this.firstName)
      .put("lastName", this.lastName)
      .put("quote", this.quote);
  }
}
