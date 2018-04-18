package com.davis.utilities.result.compare.api;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/12/17.
 */
public class Prediction {

  private String predictedClass;
  private String actualClass;
  private String fileName;
  private Boolean isCorrect;

  public Prediction() {}

  public Prediction(String predictedClass, String actualClass, String fileName, Boolean isCorrect) {
    this.predictedClass = predictedClass;
    this.actualClass = actualClass;
    this.fileName = fileName;
    this.isCorrect = isCorrect;
  }

  public Boolean getCorrect() {
    return isCorrect;
  }

  public void setCorrect(Boolean correct) {
    isCorrect = correct;
  }

  public String getPredictedClass() {
    return predictedClass;
  }

  public void setPredictedClass(String predictedClass) {
    this.predictedClass = predictedClass;
  }

  public String getActualClass() {
    return actualClass;
  }

  public void setActualClass(String actualClass) {
    this.actualClass = actualClass;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
