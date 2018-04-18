package com.davis.utilities.result.compare.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 9/27/17.
 */
@Entity
public class AlgoResult {

  @Column
  @JsonProperty("correct")
  public Boolean correct;

  @ElementCollection
  @JsonProperty("runs")
  public Set<Run> runs = new HashSet<Run>();

  @Column
  @JsonProperty("image")
  public String imagePath;

  @Column public String imageName;
  @Column public String imageClass;
  @Column public UUID uuid;
  @Column private String modelKey;
  @ElementCollection private List<String> algoTop5 = new ArrayList<String>();
  @Column private Boolean algoCorrectTop1;
  @Column private Boolean algoCorrectTop5;
  @Column private Double algoCorrectScore;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "result_id")
  private Result result;

  @Column private boolean inceptionProcessed;

  public AlgoResult(
      Boolean correct,
      Set<Run> runs,
      String imagePath,
      String imageName,
      String imageClass,
      UUID uuid,
      Result result,
      boolean inceptionProcessed) {
    this.correct = correct;
    this.runs = runs;
    this.imagePath = imagePath;
    this.imageName = imageName;
    this.imageClass = imageClass;
    this.uuid = uuid;
    this.result = result;
    this.inceptionProcessed = inceptionProcessed;
  }

  public AlgoResult() {}

  public AlgoResult(
      Boolean correct,
      Set<Run> runs,
      String imagePath,
      String imageName,
      String imageClass,
      UUID uuid,
      List<String> algoTop5,
      Boolean algoCorrectTop1,
      Boolean algoCorrectTop5,
      Double algoCorrectScore,
      Result result,
      boolean inceptionProcessed) {
    this.correct = correct;
    this.runs = runs;
    this.imagePath = imagePath;
    this.imageName = imageName;
    this.imageClass = imageClass;
    this.uuid = uuid;
    this.algoTop5 = algoTop5;
    this.algoCorrectTop1 = algoCorrectTop1;
    this.algoCorrectTop5 = algoCorrectTop5;
    this.algoCorrectScore = algoCorrectScore;
    this.result = result;
    this.inceptionProcessed = inceptionProcessed;
  }

  public AlgoResult(
      Boolean correct,
      Set<Run> runs,
      String imagePath,
      String imageName,
      String imageClass,
      UUID uuid,
      String modelKey,
      List<String> algoTop5,
      Boolean algoCorrectTop1,
      Boolean algoCorrectTop5,
      Double algoCorrectScore,
      Result result,
      boolean inceptionProcessed) {
    this.correct = correct;
    this.runs = runs;
    this.imagePath = imagePath;
    this.imageName = imageName;
    this.imageClass = imageClass;
    this.uuid = uuid;
    this.modelKey = modelKey;
    this.algoTop5 = algoTop5;
    this.algoCorrectTop1 = algoCorrectTop1;
    this.algoCorrectTop5 = algoCorrectTop5;
    this.algoCorrectScore = algoCorrectScore;
    this.result = result;
    this.inceptionProcessed = inceptionProcessed;
  }

  public String getModelKey() {

    return modelKey;
  }

  public void setModelKey(String modelKey) {
    this.modelKey = modelKey;
  }

  public Set<Run> getRuns() {

    return runs;
  }

  public void setRuns(Set<Run> runs) {
    this.runs = runs;
  }

  @JsonProperty("correct")
  public Boolean getCorrect() {
    return correct;
  }

  @JsonProperty("correct")
  public void setCorrect(Boolean correct) {
    this.correct = correct;
  }

  @JsonProperty("image")
  public String getImagePath() {
    return imagePath;
  }

  @JsonProperty("image")
  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getImageClass() {
    return imageClass;
  }

  public void setImageClass(String imageClass) {
    this.imageClass = imageClass;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public boolean isInceptionProcessed() {
    return inceptionProcessed;
  }

  public void setInceptionProcessed(boolean inceptionProcessed) {
    this.inceptionProcessed = inceptionProcessed;
  }

  public List<String> getAlgoTop5() {

    return algoTop5;
  }

  public void setAlgoTop5(List<String> algoTop5) {
    this.algoTop5 = algoTop5;
  }

  public Boolean getAlgoCorrectTop1() {
    return algoCorrectTop1;
  }

  public void setAlgoCorrectTop1(Boolean algoCorrectTop1) {
    this.algoCorrectTop1 = algoCorrectTop1;
  }

  public Boolean getAlgoCorrectTop5() {
    return algoCorrectTop5;
  }

  public void setAlgoCorrectTop5(Boolean algoCorrectTop5) {
    this.algoCorrectTop5 = algoCorrectTop5;
  }

  public Double getAlgoCorrectScore() {
    return algoCorrectScore;
  }

  public void setAlgoCorrectScore(Double algoCorrectScore) {
    this.algoCorrectScore = algoCorrectScore;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }
}
