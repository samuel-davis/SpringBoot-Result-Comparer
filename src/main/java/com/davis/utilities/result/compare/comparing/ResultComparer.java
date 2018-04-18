package com.davis.utilities.result.compare.comparing;

import com.davis.utilities.result.compare.api.CompareOperation;
import com.davis.utilities.result.compare.api.ComparisonResult;
import com.davis.utilities.result.compare.entities.AlgoResult;
import com.davis.utilities.result.compare.api.InceptionTops;
import com.davis.utilities.result.compare.entities.Run;
import com.davis.utilities.result.compare.api.TruthValue;
import com.davis.utilities.result.compare.entities.LegacyResult;
import com.davis.utilities.result.compare.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static com.davis.utilities.result.compare.utils.Utils.getClassName;
import static com.davis.utilities.result.compare.utils.Utils.getFilename;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 9/27/17.
 */
public class ResultComparer {
  private static final Logger log = LoggerFactory.getLogger(ResultComparer.class.getName());
  private Map<UUID, String> truthMap = new HashMap<>();
  private List<LegacyResult> legacyResults;
  private List<AlgoResult> algoResults;
  private List<TruthValue> truthValues;
  private Map<UUID, CompareOperation> compareOperationMap = new HashMap<>();
  private Map<UUID, List<LegacyResult>> legacyMap = new HashMap<>();
  private Map<UUID, List<AlgoResult>> inceptionMap = new HashMap<>();
  private List<CompareOperation> compareOperations = new ArrayList<>();

  public ResultComparer(
      List<LegacyResult> legacyResults,
      List<AlgoResult> algoResults,
      List<TruthValue> truthValues) {
    this.legacyResults = legacyResults;
    this.algoResults = algoResults;
    this.truthValues = truthValues;
  }

  public List<ComparisonResult> compare() {
    List<ComparisonResult> results = new ArrayList<>();
    populateTruthMap();
    populateLegacyMap();
    populateInceptionMap();
    //inceptionMatrix.increaseValue();


    populateCompareOperationMap(false);
    for (CompareOperation compareOperation : compareOperations) {
      results.add(doComparison(compareOperation));
    }

    List<ComparisonResult> inceptionCorrectTop1List = getInceptionCorrectTop1(results);

    List<ComparisonResult> inceptionCorrectTop5List = getInceptionCorrectTop5(results);
    List<ComparisonResult> legacyCorrectList = getLegacyCorrect(results);
    List<ComparisonResult> legacyIncorrectList = getLegacyIncorrect(results);

    Map<String, Integer> legacyIncorrectByClass = getLegacyIncorrectsByClass(legacyIncorrectList);
    Map<String, Integer> legacyNonProcessed = getLegacyNonProcessed(legacyIncorrectList);
    Map<String, Integer> legacyIncorrectAndProcessed =
        getLegacyIncorrectsByClassAndProcessed(legacyIncorrectList);
    float totalImages = compareOperations.size();
    float totalProcessedByLegacy = totalImages - addAllValuesFromMap(legacyNonProcessed);
    float totalNonProcessedByLegacy = addAllValuesFromMap(legacyNonProcessed);

    List<String> legacyIncorrectAll = new ArrayList<>();
    List<String> legacyIncorrectAndProcessedList = new ArrayList<>();
    List<String> legacyNonProcessedList = new ArrayList<>();

    for (String key : legacyIncorrectAndProcessed.keySet()) {
      BigDecimal num =
          new BigDecimal((legacyIncorrectAndProcessed.get(key) / totalProcessedByLegacy) * 100);
      String numWithNoExponents = num.toPlainString();
      legacyIncorrectAndProcessedList.add(key + "  ::  " + numWithNoExponents);
    }

    for (String key : legacyIncorrectByClass.keySet()) {
      BigDecimal num = new BigDecimal((legacyIncorrectByClass.get(key) / totalImages) * 100);
      String numWithNoExponents = num.toPlainString();
      legacyIncorrectAll.add(key + "  ::  " + numWithNoExponents);
    }
    for (String key : legacyNonProcessed.keySet()) {
      BigDecimal num = new BigDecimal((legacyNonProcessed.get(key) / totalNonProcessedByLegacy) * 100);
      String numWithNoExponents = num.toPlainString();
      legacyNonProcessedList.add(key + "  ::  " + numWithNoExponents);


    }


    log.info("Total number of inception correct at Top 1 {}", inceptionCorrectTop1List.size());
    log.info(
        "Total number of inception correct at Top 5 {}",
        inceptionCorrectTop5List.size() - inceptionCorrectTop1List.size());
    log.info("Total number of inception correct total {}", inceptionCorrectTop5List.size());
    log.info("Total number of Legacy Correct {}", legacyCorrectList.size());

    return results;
  }




  private Integer addAllValuesFromMap(Map<String, Integer> map) {

    Integer result = 0;
    for (Integer o : map.values()) {
      result = result + o;
    }
    return result;
  }

  private Map<String, Integer> getLegacyIncorrectsByClass(List<ComparisonResult> legacyIncorrectList) {
    Map<String, Integer> legacyIncorrectClassCounts = new TreeMap<>();

    for (ComparisonResult rrr : legacyIncorrectList) {
      if (legacyIncorrectClassCounts.get(rrr.getTruthValue()) != null) {
        int newCount = legacyIncorrectClassCounts.get(rrr.getTruthValue()) + 1;
        legacyIncorrectClassCounts.put(rrr.getTruthValue(), newCount);
      } else {
        legacyIncorrectClassCounts.put(rrr.getTruthValue(), 1);
      }
    }
    return Utils.sortByValue(legacyIncorrectClassCounts);
  }

  private Map<String, Integer> getLegacyIncorrectsByClassAndProcessed(
      List<ComparisonResult> legacyIncorrectList) {
    Map<String, Integer> legacyIncorrectClassCounts = new TreeMap<>();

    for (ComparisonResult rrr : legacyIncorrectList) {
      if (rrr.isLegacyProcessed()) {
        if (legacyIncorrectClassCounts.get(rrr.getTruthValue()) != null) {
          int newCount = legacyIncorrectClassCounts.get(rrr.getTruthValue()) + 1;
          legacyIncorrectClassCounts.put(rrr.getTruthValue(), newCount);
        } else {
          legacyIncorrectClassCounts.put(rrr.getTruthValue(), 1);
        }
      }
    }
    return Utils.sortByValue(legacyIncorrectClassCounts);
  }

  private Map<String, Integer> getLegacyNonProcessed(List<ComparisonResult> legacyIncorrectList) {
    Map<String, Integer> legacyIncorrectClassCounts = new TreeMap<>();

    for (ComparisonResult rrr : legacyIncorrectList) {
      if (!rrr.isLegacyProcessed()) {
        if (legacyIncorrectClassCounts.get(rrr.getTruthValue()) != null) {
          int newCount = legacyIncorrectClassCounts.get(rrr.getTruthValue()) + 1;
          legacyIncorrectClassCounts.put(rrr.getTruthValue(), newCount);
        } else {
          legacyIncorrectClassCounts.put(rrr.getTruthValue(), 1);
        }
      }
    }
    return Utils.sortByValue(legacyIncorrectClassCounts);
  }

  private List<ComparisonResult> getLegacyCorrect(List<ComparisonResult> results) {
    List<ComparisonResult> legacyCorrectList = new ArrayList<>();

    int legacyCorrect = 0;
    for (ComparisonResult r : results) {
      if (r.isLegacyCorrect()) {
        legacyCorrectList.add(r);
        legacyCorrect = legacyCorrect + 1;
      }
    }
    log.info("Total number of legacy correct {}", legacyCorrect);
    return legacyCorrectList;
  }

  private List<ComparisonResult> getLegacyIncorrect(List<ComparisonResult> results) {
    List<ComparisonResult> legacyIncorrectList = new ArrayList<>();

    int legacyIncorrect = 0;
    for (ComparisonResult r : results) {
      if (!r.isLegacyCorrect() || !r.isLegacyProcessed()) {
        legacyIncorrectList.add(r);
        legacyIncorrect = legacyIncorrect + 1;
      }
    }
    log.info("Total number of legacy incorrect {}", legacyIncorrect);

    return legacyIncorrectList;
  }

  private List<ComparisonResult> getInceptionCorrectTop1(List<ComparisonResult> results) {
    List<ComparisonResult> inceptionCorrectTop1List = new ArrayList<>();

    int inceptionCorrectTop1 = 0;
    for (ComparisonResult r : results) {
      if (r.isInceptionCorrectTop1()) {
        inceptionCorrectTop1List.add(r);
        inceptionCorrectTop1 = inceptionCorrectTop1 + 1;
      }
    }
    log.info("Total number of inception correct at Top 1 {}", inceptionCorrectTop1);
    return inceptionCorrectTop1List;
  }

  private List<ComparisonResult> getInceptionIncorrectTop1(List<ComparisonResult> results) {
    List<ComparisonResult> incorrectTop1List = new ArrayList<>();

    int incorrectTop1 = 0;
    for (ComparisonResult r : results) {
      if (!r.isInceptionCorrectTop1() || !r.isInceptionProcessed()) {
        incorrectTop1List.add(r);
        incorrectTop1 = incorrectTop1 + 1;
      }
    }
    log.info("Total number of inception incorrect at Top 1 {}", incorrectTop1);

    return incorrectTop1List;
  }

  private List<ComparisonResult> getInceptionIncorrectTop5(List<ComparisonResult> results) {
    List<ComparisonResult> incorrectTop5List = new ArrayList<>();

    int incorrectTop5 = 0;
    for (ComparisonResult r : results) {
      if (!r.isInceptionCorrectTop5() || !r.isInceptionProcessed()) {
        incorrectTop5List.add(r);
        incorrectTop5 = incorrectTop5 + 1;
      }
    }
    log.info("Total number of inception incorrect at Top 5 {}", incorrectTop5);
    return incorrectTop5List;
  }

  private List<ComparisonResult> getInceptionCorrectTop5(List<ComparisonResult> results) {
    List<ComparisonResult> correctTop5List = new ArrayList<>();

    int correctTop5 = 0;
    for (ComparisonResult r : results) {
      if (r.isInceptionCorrectTop5()) {
        correctTop5List.add(r);
        correctTop5 = correctTop5 + 1;
      }
    }
    log.info("Total number of inception correct at Top 5 {}", correctTop5);

    return correctTop5List;
  }

  private ComparisonResult doComparison(CompareOperation compareOperation) {

    ComparisonResult comparisonResult = new ComparisonResult();
    comparisonResult.setTruthValue(compareOperation.getTruthValue());
    comparisonResult.setImageName(compareOperation.getImageName());
    comparisonResult.setLegacyProcessed(compareOperation.getLegacyProcessed());
    comparisonResult.setInceptionProcessed(compareOperation.getInceptionProcessed());
    String truth = compareOperation.getTruthValue();
    InceptionTops inceptionTops = getInceptionTops(compareOperation.getAlgoResults());
    parseInceptionTopsForCorrect(inceptionTops, truth, comparisonResult);
    parseLegacyResultsForCorrect(compareOperation.getLegacyResults(), truth, comparisonResult);

    return comparisonResult;
  }

  private void parseLegacyResultsForCorrect(
      List<LegacyResult> legacyResults, String truth, ComparisonResult comparisonResult) {
    String detectionType;
    String detectiongSubtype;
    Double detectorScore;

    for (LegacyResult legacyResult : legacyResults) {
      detectionType = legacyResult.getDetectionType();
      detectiongSubtype = legacyResult.getDetectionSubType();
      try {
        detectorScore = Double.parseDouble(legacyResult.getDetectorScore());

      } catch (Exception e) {
        detectorScore = 0.0;
      }

      boolean primCorrect = false;

      boolean subcorrect = false;

      subcorrect = testLegacyResultForCorrect(detectiongSubtype, truth);
      if (subcorrect) {
        comparisonResult.getLegacyCorrectAlgorithims().put(legacyResult.getDetector(), detectorScore);
        comparisonResult.getLegacyCorrectLabels().put(legacyResult.getDetector(), detectiongSubtype);
        comparisonResult.setLegacyCorrect(true);
      }
      if (!subcorrect) {
        primCorrect = testLegacyResultForCorrect(detectionType, truth);
        if (primCorrect) {
          comparisonResult.getLegacyCorrectAlgorithims().put(legacyResult.getDetector(), detectorScore);
          comparisonResult.getLegacyCorrectLabels().put(legacyResult.getDetector(), detectionType);
          comparisonResult.setLegacyCorrect(true);
        }
      }
      if (!subcorrect && !primCorrect) {
        comparisonResult.getLegacyIncorrectAlgorithims().put(legacyResult.getDetector(), detectorScore);
      }
    }
  }

  private boolean testLegacyResultForCorrect(String legacyPredict, String truth) {

    boolean correct = false;
    if (legacyPredict != null) {
      if (legacyPredict.equalsIgnoreCase("face")) {
        if (truth.contains("person")) {
          correct = true;
        }
      }
      if (legacyPredict.equalsIgnoreCase("person") ) {
        if (truth.contains("person")) {
          correct = true;
        }
      }
      else if (legacyPredict.equalsIgnoreCase("vehicle")) {
        if (truth.contains("vehicle")) {
          correct = true;
        }
      }
      else if (legacyPredict.equalsIgnoreCase("bus")) {
        if (truth.contains("vehicle")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("sedan")) {
        if (truth.contains("vehicle")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("weapon")) {
        if (truth.contains("weapon")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("rpg")) {
        if (truth.contains("weapon")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("rpg_plus_launcher")) {
        if (truth.contains("weapon")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("building")) {
        if (truth.contains("building")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("motorcycle")) {
        if (truth.contains("vehicle")) {
          correct = true;
        }
      }
    }

    return correct;
  }

  private void parseInceptionTopsForCorrect(
      InceptionTops inceptionTops, String truth, ComparisonResult comparisonResult) {
    List<String> top5Strings = new ArrayList<>();
    top5Strings.add(inceptionTops.getTop1Label());
    top5Strings.add(inceptionTops.getTop2Label());
    top5Strings.add(inceptionTops.getTop3Label());
    top5Strings.add(inceptionTops.getTop4Label());
    top5Strings.add(inceptionTops.getTop5Label());

    if (inceptionTops.getTop1Label().equalsIgnoreCase(truth)) {
      comparisonResult.setInceptionCorrectTop1(true);
      comparisonResult.setInceptionCorrectTop5(true);
      comparisonResult.setInceptionCorrectClass(inceptionTops.getTop1Label());
      comparisonResult.setInceptionCorrectScore(inceptionTops.getTop1Score());
      comparisonResult.setInceptionTop5(top5Strings);

    } else if (inceptionTops.getTop2Label().equalsIgnoreCase(truth)) {
      comparisonResult.setInceptionCorrectTop1(false);
      comparisonResult.setInceptionCorrectTop5(true);
      comparisonResult.setInceptionCorrectClass(inceptionTops.getTop2Label());
      comparisonResult.setInceptionCorrectScore(inceptionTops.getTop2Score());
      comparisonResult.setInceptionTop5(top5Strings);
    } else if (inceptionTops.getTop3Label().equalsIgnoreCase(truth)) {
      comparisonResult.setInceptionCorrectTop1(false);
      comparisonResult.setInceptionCorrectTop5(true);
      comparisonResult.setInceptionCorrectClass(inceptionTops.getTop3Label());
      comparisonResult.setInceptionCorrectScore(inceptionTops.getTop3Score());
      comparisonResult.setInceptionTop5(top5Strings);
    } else if (inceptionTops.getTop4Label().equalsIgnoreCase(truth)) {
      comparisonResult.setInceptionCorrectTop1(false);
      comparisonResult.setInceptionCorrectTop5(true);
      comparisonResult.setInceptionCorrectClass(inceptionTops.getTop4Label());
      comparisonResult.setInceptionCorrectScore(inceptionTops.getTop4Score());
      comparisonResult.setInceptionTop5(top5Strings);
    } else if (inceptionTops.getTop5Label().equalsIgnoreCase(truth)) {
      comparisonResult.setInceptionCorrectTop5(true);
      comparisonResult.setInceptionCorrectClass(inceptionTops.getTop5Label());
      comparisonResult.setInceptionCorrectScore(inceptionTops.getTop5Score());
      comparisonResult.setInceptionTop5(top5Strings);
    } else {
      comparisonResult.setInceptionCorrectTop1(false);
      comparisonResult.setInceptionCorrectTop5(false);
      comparisonResult.setInceptionCorrectClass(null);
      comparisonResult.setInceptionCorrectScore(null);
      comparisonResult.setInceptionTop5(top5Strings);
    }
  }

  private InceptionTops getInceptionTops(AlgoResult results) {

    List<Run> runs = new ArrayList<>();
    for(Run r : results.getRuns()){
      runs.add(r);
    }
       Collections.sort(
            runs,
            new Comparator<Run>() {
              @Override
              public int compare(Run o1, Run o2) {
                if (o1.getScore() < o2.getScore()) {
                  return 1;
                } else {
                  return -1;
                }
              }
            });
    InceptionTops inceptionTops = new InceptionTops();
    inceptionTops.setTop1Label(runs.get(0).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop2Label(runs.get(1).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop3Label(runs.get(2).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop4Label(runs.get(3).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop5Label(runs.get(4).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop1Score(runs.get(0).getScore());
    inceptionTops.setTop2Score(runs.get(1).getScore());
    inceptionTops.setTop3Score(runs.get(2).getScore());
    inceptionTops.setTop4Score(runs.get(3).getScore());
    inceptionTops.setTop5Score(runs.get(4).getScore());

    return inceptionTops;
  }

  private InceptionTops getInceptionTops(List<AlgoResult> results) {

    List<Run> runs = new ArrayList<>();
    for (AlgoResult result : results) {
      for (Run r : result.getRuns()) {
        runs.add(r);
      }
    }

    Collections.sort(
        runs,
        new Comparator<Run>() {
          @Override
          public int compare(Run o1, Run o2) {
            if (o1.getScore() < o2.getScore()) {
              return 1;
            } else {
              return -1;
            }
          }
        });
    InceptionTops inceptionTops = new InceptionTops();
    inceptionTops.setTop1Label(runs.get(0).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop2Label(runs.get(1).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop3Label(runs.get(2).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop4Label(runs.get(3).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop5Label(runs.get(4).getLabel().replaceAll(" ", "-"));
    inceptionTops.setTop1Score(runs.get(0).getScore());
    inceptionTops.setTop2Score(runs.get(1).getScore());
    inceptionTops.setTop3Score(runs.get(2).getScore());
    inceptionTops.setTop4Score(runs.get(3).getScore());
    inceptionTops.setTop5Score(runs.get(4).getScore());

    return inceptionTops;
  }

  private void populateTruthMap() {
    for (TruthValue truthValue : truthValues) {
      UUID uuid =
          UUID.nameUUIDFromBytes((truthValue.getFileName()).getBytes());
      if(truthMap.containsKey(uuid)){
        log.info("Uh  OH Spagetthiios");
      }
      truthMap.put(uuid, truthValue.getClassName());
    }
  }

  private void populateCompareOperationMap(boolean useSingleProcessedResults) {

    for (AlgoResult algoResult : algoResults) {
      if (compareOperationMap.get(algoResult.getUuid()) != null) {
        compareOperationMap
            .get(algoResult.getUuid())
            .getAlgoResults()
            .add(algoResult);
        compareOperationMap.get(algoResult.getUuid()).setContainsTruth(true);
        compareOperationMap
            .get(algoResult.getUuid())
            .setTruthValue(truthMap.get(algoResult.getUuid()));
        compareOperationMap.get(algoResult.getUuid()).setContainsTruth(true);
      } else {
        CompareOperation compareOperation = new CompareOperation();
        compareOperation.setUuid(algoResult.getUuid());
        compareOperation.setImageName(
            algoResult.getImageName());
        compareOperation.getAlgoResults().add(algoResult);
        compareOperation.setTruthValue(truthMap.get(algoResult.getUuid()));
        compareOperation.setContainsTruth(true);
        compareOperationMap.put(algoResult.getUuid(), compareOperation);
      }
    }

    for (LegacyResult legacyResult : legacyResults) {
      if (compareOperationMap.get(legacyResult.getUuid()) != null) {
        //Legacy processed image in the test set
        compareOperationMap.get(legacyResult.getUuid()).getLegacyResults().add(legacyResult);
        compareOperationMap.get(legacyResult.getUuid()).setLegacyProcessed(true);
      } else {
        //Image either wasnt in the test set
        if (useSingleProcessedResults) {
          log.trace(
              "Adding a compare operation for a LEGACY result tha thas no corresponding inception result.");
          CompareOperation compareOperation = new CompareOperation();
          compareOperation.setUuid(UUID.fromString(legacyResult.getUuid()));


          compareOperation.setImageName(legacyResult.getImageName());
          compareOperation.getLegacyResults().add(legacyResult);
          compareOperation.setContainsTruth(false);
          compareOperationMap.put(UUID.fromString(legacyResult.getUuid()), compareOperation);
        }
      }
    }
    if (useSingleProcessedResults) {
      List<UUID> uuidsWithSingleProcessing = new ArrayList<>();
      for (CompareOperation operation : compareOperationMap.values()) {
        if (operation.getLegacyResults().isEmpty() || operation.getAlgoResults().isEmpty()) {
          log.trace(
              "UUID of {} contains {} number of inception results and {} number of legacy results",
              operation.getImageName(),
              operation.getAlgoResults().size(),
              operation.getLegacyResults().size());
          log.trace(
              "Removing UUID of {} due to only have results from a single framework. I={} V={}",
              operation.getUuid(),
              operation.getAlgoResults().size(),
              operation.getLegacyResults().size());
          uuidsWithSingleProcessing.add(operation.getUuid());
        }
      }

      for (UUID removeUuid : uuidsWithSingleProcessing) {
        if (compareOperationMap.get(removeUuid).getLegacyResults().isEmpty()) {
          LegacyResult legacyResult = new LegacyResult();
          legacyResult.setLegacyProcessed(false);
          compareOperationMap.get(removeUuid).getLegacyResults().add(legacyResult);
        }
        if (compareOperationMap.get(removeUuid).getAlgoResults().isEmpty()) {
          AlgoResult algoResult = new AlgoResult();
          algoResult.setInceptionProcessed(false);
          compareOperationMap.get(removeUuid).getAlgoResults().add(algoResult);
        }
      }
    }

    for (CompareOperation compareOperation : compareOperationMap.values()) {
      if (compareOperation.isContainsTruth()) {

        if (!compareOperation.getAlgoResults().isEmpty()) {
          compareOperation.setInceptionProcessed(true);
        } else {
          compareOperation.setInceptionProcessed(false);
        }
        if (compareOperation.getLegacyResults().isEmpty()) {
          compareOperation.setLegacyProcessed(false);
        } else {
          compareOperation.setLegacyProcessed(true);
        }

        compareOperations.add(compareOperation);
      }
    }

    log.info(
        "Will only be able to perform comparisons on {} number of images",
        compareOperations.size());
  }

  private void populateInceptionMap() {
    for (AlgoResult result : algoResults) {
      String fileName = getFilename(result.getImagePath());
      String className = getClassName(result.getImagePath());
      result.setImageName(fileName);
      result.setImageClass(className);
      UUID uuid = UUID.nameUUIDFromBytes((fileName).getBytes());
      result.setUuid(uuid);
      if (inceptionMap.get(uuid) != null) {
        inceptionMap.get(uuid).add(result);
      } else {
        List<AlgoResult> results = new ArrayList<>();
        results.add(result);
        inceptionMap.put(uuid, results);
      }
    }
  }

  private void populateLegacyMap() {

    //List<LegacyResult> evaluatedResults = new ArrayList<>();
    for (LegacyResult result : legacyResults) {
      String fileName = getFilename(result.getFileLocation());
      String className = getClassName(result.getFileLocation());
      result.setImageName(fileName);
      result.setImageClass(className);
      UUID uuid = UUID.nameUUIDFromBytes((fileName).getBytes());
      result.setUuid(uuid.toString());
      if (legacyMap.get(uuid) != null) {
        legacyMap.get(uuid).add(result);
      } else {
        List<LegacyResult> results = new ArrayList<>();
        results.add(result);
        legacyMap.put(uuid, results);
      }
      /*
      if (truthMap.get(className + fileName) != null) {
        evaluatedResults.add(result);
      }*/
    }
    // this.legacyResults = evaluatedResults;
  }

  class DarknetResult {

    public String className;
    public Integer probablity;

    public Double x;
    public Double y;
    public Double width;
    public Double height;

    public DarknetResult(){

    }
    public DarknetResult(
        String className, Integer probablity, Double x, Double y, Double width, Double height) {
      this.className = className;
      this.probablity = probablity;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

    public String getClassName() {
      return className;
    }

    public void setClassName(String className) {
      this.className = className;
    }

    public Integer getProbablity() {
      return probablity;
    }

    public void setProbablity(Integer probablity) {
      this.probablity = probablity;
    }

    public Double getX() {
      return x;
    }

    public void setX(Double x) {
      this.x = x;
    }

    public Double getY() {
      return y;
    }

    public void setY(Double y) {
      this.y = y;
    }

    public Double getWidth() {
      return width;
    }

    public void setWidth(Double width) {
      this.width = width;
    }

    public Double getHeight() {
      return height;
    }

    public void setHeight(Double height) {
      this.height = height;
    }
  }


  class DarknetResults{
    List<DarknetResult> darknetResults = new ArrayList<>();

    public void addToResults(DarknetResult result){
      darknetResults.add(result);
    }
    public List<DarknetResult> getDarknetResults() {
      return darknetResults;
    }
  }
}
