package com.sonarsource.app.json;

import java.util.List;

public class Action {

  private String key;
  private List<Parameter> params;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public List<Parameter> getParams() {
    return params;
  }

  public void setParams(List<Parameter> params) {
    this.params = params;
  }

}
