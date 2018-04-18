package com.davis.utilities.result.compare.api;


import java.util.ArrayList;
import java.util.List;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class CsvTruthValue {


  public String numericName;
  public String sanitizedName;
  public String comboClassTruth;
  public String originalTruth;
  public List<String> newTruths = new ArrayList<>();
  public Boolean isScene;



  public CsvTruthValue() {
  }


  public String getNumericName() {
    return numericName;
  }

  public void setNumericName(String numericName) {
    this.numericName = numericName;
  }

  public String getSanitizedName() {
    return sanitizedName;
  }

  public void setSanitizedName(String sanitizedName) {
    this.sanitizedName = sanitizedName;
  }

  public String getComboClassTruth() {
    return comboClassTruth;
  }

  public void setComboClassTruth(String comboClassTruth) {
    this.comboClassTruth = comboClassTruth;
  }

  public String getOriginalTruth() {
    return originalTruth;
  }

  public void setOriginalTruth(String originalTruth) {
    this.originalTruth = originalTruth;
  }


  public void addNewTruth(String truth){
    this.newTruths.add(truth);
  }




  public Boolean getScene() {
    return isScene;
  }
  public void setScene(String scene) {
    isScene = Boolean.valueOf(scene);
  }
  public void setScene(Boolean scene) {
    isScene = scene;
  }


  public List<String> getNewTruths() {
    return newTruths;
  }

  public void setNewTruths(List<String> newTruths) {
    this.newTruths = newTruths;
  }

  public List<String> getAllTruths(){
    List<String> truths = new ArrayList<>();
    if(originalTruth != null){
      truths.add(originalTruth);
    }
    if(comboClassTruth != null){
      truths.add(comboClassTruth);
    }
    if(newTruths != null){
      for(String s : newTruths){
        truths.add(s);
      }

    }
    return truths;
  }
}
