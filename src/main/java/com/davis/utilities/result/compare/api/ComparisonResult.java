package com.davis.utilities.result.compare.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class ComparisonResult {
  private String truthValue;
  private String imageName;
  private String inceptionCorrectClass;
  private List<String> inceptionTop5;
  private boolean inceptionCorrectTop1;
  private boolean inceptionCorrectTop5;
  private boolean isInceptionProcessed;
  private boolean isLegacyProcessed;
  private Double inceptionCorrectScore;
  private boolean legacyCorrect;
  private Map<String, String> legacyCorrectLabels = new HashMap<>();
  private Map<String, Double> legacyCorrectAlgorithims = new HashMap<>();
  private Map<String, Double> legacyIncorrectAlgorithims = new HashMap<>();

  public boolean isInceptionProcessed() {
    return isInceptionProcessed;
  }

  public void setInceptionProcessed(boolean inceptionProcessed) {
    isInceptionProcessed = inceptionProcessed;
  }

  public boolean isLegacyProcessed() {
    return isLegacyProcessed;
  }

  public void setLegacyProcessed(boolean legacyProcessed) {
    isLegacyProcessed = legacyProcessed;
  }

  public Map<String, Double> getLegacyCorrectAlgorithims() {
    return legacyCorrectAlgorithims;
  }

  public void setLegacyCorrectAlgorithims(Map<String, Double> legacyCorrectAlgorithims) {
    this.legacyCorrectAlgorithims = legacyCorrectAlgorithims;
  }

  public Map<String, Double> getLegacyIncorrectAlgorithims() {
    return legacyIncorrectAlgorithims;
  }

  public void setLegacyIncorrectAlgorithims(Map<String, Double> legacyIncorrectAlgorithims) {
    this.legacyIncorrectAlgorithims = legacyIncorrectAlgorithims;
  }

  public boolean isInceptionCorrectTop1() {
    return inceptionCorrectTop1;
  }

  public void setInceptionCorrectTop1(boolean inceptionCorrectTop1) {
    this.inceptionCorrectTop1 = inceptionCorrectTop1;
  }

  public boolean isInceptionCorrectTop5() {
    return inceptionCorrectTop5;
  }

  public void setInceptionCorrectTop5(boolean inceptionCorrectTop5) {
    this.inceptionCorrectTop5 = inceptionCorrectTop5;
  }

  public Double getInceptionCorrectScore() {
    return inceptionCorrectScore;
  }

  public void setInceptionCorrectScore(Double inceptionCorrectScore) {
    this.inceptionCorrectScore = inceptionCorrectScore;
  }

  public boolean isLegacyCorrect() {
    return legacyCorrect;
  }

  public void setLegacyCorrect(boolean legacyCorrect) {
    this.legacyCorrect = legacyCorrect;
  }

  public String getInceptionCorrectClass() {
    return inceptionCorrectClass;
  }

  public void setInceptionCorrectClass(String inceptionTop1) {
    this.inceptionCorrectClass = inceptionTop1;
  }

  public List<String> getInceptionTop5() {
    return inceptionTop5;
  }

  public void setInceptionTop5(List<String> inceptionTop5) {
    this.inceptionTop5 = inceptionTop5;
  }

  public Map<String, String> getLegacyCorrectLabels() {
    return legacyCorrectLabels;
  }

  public void setLegacyCorrectLabels(Map<String, String> legacyCorrectLabels) {
    this.legacyCorrectLabels = legacyCorrectLabels;
  }

  public String getTruthValue() {
    return truthValue;
  }

  public void setTruthValue(String truthValue) {
    this.truthValue = truthValue;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }
}
