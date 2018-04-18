package com.davis.utilities.result.compare.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 9/27/17.
 */
@Entity
public class LegacyResult {
  @Column private String fileLocation;
  @Column private String triageValue;
  @Column private String detectionType;
  @Column private String detectionSubType;
  @Column private String confidence;
  @Column private String boundingBox;
  @Column private String detector;
  @Column private String detectorScore;
  @Column private String detectorConfidence;
  @Column private String detectorBoundingBox;
  @Column private String imageName;
  @Column private String imageClass;
  @Column private String uuid;
  @Column private Boolean legacyProcessed;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "result_id")
  private Result result;

  public LegacyResult() {}

  public LegacyResult(
      String fileLocation,
      String detectionType,
      String detectionSubType,
      String confidence,
      String boundingBox,
      String detector,
      String detectorScore,
      String detectorConfidence,
      String detectorBoundingBox) {
    setFileLocation(fileLocation);
    setDetectionType(detectionType);
    setDetectionSubType(detectionSubType);
    setConfidence(confidence);
    setBoundingBox(boundingBox);
    setDetector(detector);
    setDetectorScore(detectorScore);
    setDetectorConfidence(detectorConfidence);
    setDetectorBoundingBox(detectorBoundingBox);
  }

  public String getFileLocation() {
    return fileLocation;
  }

  public void setFileLocation(String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public String getDetectionType() {
    return detectionType;
  }

  public void setDetectionType(String detectionType) {
    this.detectionType = detectionType;
  }

  public String getDetectionSubType() {
    return detectionSubType;
  }

  public void setDetectionSubType(String detectionSubType) {
    this.detectionSubType = detectionSubType;
  }

  public String getConfidence() {
    return confidence;
  }

  public void setConfidence(String confidence) {
    this.confidence = confidence;
  }

  public String getBoundingBox() {
    return boundingBox;
  }

  public void setBoundingBox(String boundingBox) {
    this.boundingBox = boundingBox;
  }

  public String getDetector() {
    return detector;
  }

  public void setDetector(String detector) {
    this.detector = detector;
  }

  public String getDetectorScore() {
    return detectorScore;
  }

  public void setDetectorScore(String detectorScore) {
    this.detectorScore = detectorScore;
  }

  public String getDetectorConfidence() {
    return detectorConfidence;
  }

  public void setDetectorConfidence(String detectorConfidence) {
    this.detectorConfidence = detectorConfidence;
  }

  public String getDetectorBoundingBox() {
    return detectorBoundingBox;
  }

  public void setDetectorBoundingBox(String detectorBoundingBox) {
    this.detectorBoundingBox = detectorBoundingBox;
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

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public boolean isLegacyProcessed() {
    return legacyProcessed;
  }

  public void setLegacyProcessed(boolean legacyProcessed) {
    this.legacyProcessed = legacyProcessed;
  }

  public String getTriageValue() {
    return triageValue;
  }

  public void setTriageValue(String triageValue) {
    this.triageValue = triageValue;
  }
}
