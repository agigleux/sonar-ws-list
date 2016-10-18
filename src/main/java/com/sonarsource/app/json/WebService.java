package com.sonarsource.app.json;

import java.util.List;

public class WebService {

  private String path;
  private List<Action> actions;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

}
