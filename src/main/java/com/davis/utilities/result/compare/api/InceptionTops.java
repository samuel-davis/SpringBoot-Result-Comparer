package com.davis.utilities.result.compare.api;

import java.util.ArrayList;
import java.util.List;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class InceptionTops {

  private String top1Label;
  private Double top1Score;
  private String top2Label;
  private Double top2Score;
  private String top3Label;
  private Double top3Score;
  private String top4Label;
  private Double top4Score;
  private String top5Label;
  private Double top5Score;

  public List<String> getTop5Labels(){
    List<String> list = new ArrayList<>();
    list.add(getTop1Label());
    list.add(getTop2Label());
    list.add(getTop3Label());
    list.add(getTop4Label());
    list.add(getTop5Label());
    return list;
  }


  public String getTop1Label() {
    return top1Label;
  }

  public void setTop1Label(String top1Label) {
    this.top1Label = top1Label;
  }

  public Double getTop1Score() {
    return top1Score;
  }

  public void setTop1Score(Double top1Score) {
    this.top1Score = top1Score;
  }

  public String getTop2Label() {
    return top2Label;
  }

  public void setTop2Label(String top2Label) {
    this.top2Label = top2Label;
  }

  public Double getTop2Score() {
    return top2Score;
  }

  public void setTop2Score(Double top2Score) {
    this.top2Score = top2Score;
  }

  public String getTop3Label() {
    return top3Label;
  }

  public void setTop3Label(String top3Label) {
    this.top3Label = top3Label;
  }

  public Double getTop3Score() {
    return top3Score;
  }

  public void setTop3Score(Double top3Score) {
    this.top3Score = top3Score;
  }

  public String getTop4Label() {
    return top4Label;
  }

  public void setTop4Label(String top4Label) {
    this.top4Label = top4Label;
  }

  public Double getTop4Score() {
    return top4Score;
  }

  public void setTop4Score(Double top4Score) {
    this.top4Score = top4Score;
  }

  public String getTop5Label() {
    return top5Label;
  }

  public void setTop5Label(String top5Label) {
    this.top5Label = top5Label;
  }

  public Double getTop5Score() {
    return top5Score;
  }

  public void setTop5Score(Double top5Score) {
    this.top5Score = top5Score;
  }
}
