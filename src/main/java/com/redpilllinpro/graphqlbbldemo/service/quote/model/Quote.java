package com.redpilllinpro.graphqlbbldemo.service.quote.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Quote {
  private String id;
  private String value;

  public Quote(String id, String value) {
    this.id = id;
    this.value = value;
  }

  public Quote(JsonObject jsonObject) {
    this.id = jsonObject.getString("id");
    this.value = jsonObject.getString("value");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public JsonObject toJson() {
    return new JsonObject()
      .put("id", this.id)
      .put("value", this.value);
  }
}
