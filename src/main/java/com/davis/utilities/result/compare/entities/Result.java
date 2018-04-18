package com.davis.utilities.result.compare.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 11/6/17.
 */
@Entity
public class Result {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ElementCollection private List<String> newTruths = new ArrayList<String>();
  @ElementCollection private List<String> allTruths = new ArrayList<String>();

  @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<AlgoResult> algoResults = new HashSet<AlgoResult>();

  @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<LegacyResult> legacyResults = new HashSet<LegacyResult>();

  @Column private String numericName;

  @Column private Boolean algoProcessed = false;
  @Column private Boolean legacyProcessed = false;
  @Column private String originalTruth;
  @Column private String comboTruth;

  public Result() {}

  public Result(
      List<String> newTruths,
      List<String> allTruths,
      Set<AlgoResult> algoResults,
      Set<LegacyResult> legacyResults,
      String numericName,
      Boolean algoProcessed,
      Boolean legacyProcessed,
      String originalTruth,
      String comboTruth) {
    this.newTruths = newTruths;
    this.allTruths = allTruths;
    this.algoResults = algoResults;
    this.legacyResults = legacyResults;
    this.numericName = numericName;
    this.algoProcessed = algoProcessed;
    this.legacyProcessed = legacyProcessed;
    this.originalTruth = originalTruth;
    this.comboTruth = comboTruth;
  }

  public List<String> getAllTruths() {
    return allTruths;
  }

  public void setAllTruths(List<String> allTruths) {

    this.allTruths = allTruths;
  }

  public void addToLegacyResults(LegacyResult result) {
    this.legacyResults.add(result);
  }

  public Set<AlgoResult> getAlgoResults() {
    return algoResults;
  }

  public void setAlgoResults(Set<AlgoResult> algoResults) {
    this.algoResults = algoResults;
  }

  public void addToAlgoResults(AlgoResult result) {
    this.algoResults.add(result);
  }

  public void addToAllTruths(String truth) {
    this.allTruths.add(truth);
  }

  public void addToNewTruths(String truth) {
    this.newTruths.add(truth);
  }

  public String getOriginalTruth() {
    return originalTruth;
  }

  public void setOriginalTruth(String originalTruth) {
    this.originalTruth = originalTruth;
  }

  public String getComboTruth() {
    return comboTruth;
  }

  public void setComboTruth(String comboTruth) {
    this.comboTruth = comboTruth;
  }

  public Set<LegacyResult> getLegacyResults() {
    return legacyResults;
  }

  public void setLegacyResults(Set<LegacyResult> legacyResults) {
    this.legacyResults = legacyResults;
  }

  public Boolean getAlgoProcessed() {
    return algoProcessed;
  }

  public Boolean getLegacyProcessed() {
    return legacyProcessed;
  }

  public boolean isAlgoProcessed() {
    return algoProcessed;
  }

  public void setAlgoProcessed(Boolean algoProcessed) {
    this.algoProcessed = algoProcessed;
  }

  public boolean isLegacyProcessed() {
    return legacyProcessed;
  }

  public void setLegacyProcessed(Boolean legacyProcessed) {
    this.legacyProcessed = legacyProcessed;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<String> getNewTruths() {
    return newTruths;
  }

  public void setNewTruths(List<String> newTruths) {
    this.newTruths = newTruths;
  }

  public String getNumericName() {
    return numericName;
  }

  public void setNumericName(String numericName) {
    this.numericName = numericName;
  }
}
