package com.davis.utilities.result.compare.api;

import com.davis.utilities.result.compare.entities.AlgoResult;
import com.davis.utilities.result.compare.entities.LegacyResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class CompareOperation {
  private String compareId;
  private UUID uuid;
  private String imageName;
  private boolean containsTruth;
  private String truthValue;
  private Boolean isLegacyProcessed;
  private Boolean isInceptionProcessed;
  private List<AlgoResult> algoResults = new ArrayList<>();

  private List<LegacyResult> legacyResults = new ArrayList<>();

  public List<AlgoResult> getAlgoResults() {
    return algoResults;
  }

  public List<LegacyResult> getLegacyResults() {
    return legacyResults;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public boolean isContainsTruth() {
    return containsTruth;
  }

  public void setContainsTruth(boolean containsTruth) {
    this.containsTruth = containsTruth;
  }

  public String getTruthValue() {
    return truthValue;
  }

  public void setTruthValue(String truthValue) {
    this.truthValue = truthValue;
  }

  public Boolean getLegacyProcessed() {
    return isLegacyProcessed;
  }

  public void setLegacyProcessed(Boolean legacyProcessed) {
    isLegacyProcessed = legacyProcessed;
  }

  public Boolean getInceptionProcessed() {
    return isInceptionProcessed;
  }

  public void setInceptionProcessed(Boolean inceptionProcessed) {
    isInceptionProcessed = inceptionProcessed;
  }

  public String getCompareId() {
    return compareId;
  }

  public void setCompareId(String compareId) {
    this.compareId = compareId;
  }
}
