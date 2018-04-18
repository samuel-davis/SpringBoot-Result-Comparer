package com.davis.utilities.result.compare.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class TruthValue {

  public String fileName;
  public String className;

  public List<String> objectsPresent = new ArrayList<>();



  public TruthValue(String fileName, String className){
      this.fileName = fileName;
      this.className = className;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void addToObjects(String object){
    objectsPresent.add(object);
  }

  public List<String> getObjectsPresent() {
    return objectsPresent;
  }

  public void setObjectsPresent(List<String> objectsPresent) {
    this.objectsPresent = objectsPresent;
  }
}
