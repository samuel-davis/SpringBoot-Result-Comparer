package com.davis.utilities.result.compare.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Run {
    @Column
    @JsonProperty("score")
    public Double score;
    @Column
    @JsonProperty("correct")
    public Boolean correct;
    @Column
    @JsonProperty("label")
    public String label;
    @Column
    @JsonProperty("score")
    public Double getScore() {
      return score;
    }
    @Column
    @JsonProperty("score")
    public void setScore(Double score) {
      this.score = score;
    }
    @Column
    @JsonProperty("correct")
    public Boolean getCorrect() {
      return correct;
    }
    @Column
    @JsonProperty("correct")
    public void setCorrect(Boolean correct) {
      this.correct = correct;
    }
    @Column
    @JsonProperty("label")
    public String getLabel() {
      return label;
    }
    @Column
    @JsonProperty("label")
    public void setLabel(String label) {
      this.label = label;
    }
  }