package com.davis.utilities.result.compare.comparing;

import aliceinnets.python.PythonScriptUtil;
import aliceinnets.python.jyplot.JyPlot;
import aliceinnets.util.OneLiners;
import com.davis.utilities.result.compare.api.CompareOperation;
import com.davis.utilities.result.compare.api.ComparisonResult;
import com.davis.utilities.result.compare.entities.AlgoResult;
import com.davis.utilities.result.compare.api.InceptionTops;
import com.davis.utilities.result.compare.entities.Run;
import com.davis.utilities.result.compare.api.TruthValue;
import com.davis.utilities.result.compare.entities.LegacyResult;
import com.davis.utilities.result.compare.confusion.ConfusionMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.davis.utilities.result.compare.utils.Utils.getClassName;
import static com.davis.utilities.result.compare.utils.Utils.getFilename;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 9/27/17.
 */
public class ConfusionComparer {
  private static final Logger log = LoggerFactory.getLogger(ConfusionComparer.class.getName());
  //private static final long GRAPH_TIME = System.nanoTime();
  private static final String GRAPH_TIME = "results";
  private Map<UUID, String> truthMap = new HashMap<>();
  private Map<String, UUID> truthByName = new HashMap<>();
  private List<LegacyResult> legacyResults;
  private List<AlgoResult> algoResults;
  private List<TruthValue> truthValues;
  private Map<UUID, CompareOperation> compareOperationMap = new HashMap<>();
  private Map<UUID, List<LegacyResult>> legacyMap = new HashMap<>();
  private Map<UUID, List<AlgoResult>> inceptionMap = new HashMap<>();
  private List<CompareOperation> compareOperations = new ArrayList<>();
  private Map<String, Integer> legacyPredicts = new HashMap<>();
  private boolean isFileOpen = false;
  private List<String> resultList = new ArrayList<>();

  public ConfusionComparer(
      List<LegacyResult> legacyResults,
      List<AlgoResult> algoResults,
      List<TruthValue> truthValues) {
    this.legacyResults = legacyResults;
    this.algoResults = algoResults;
    this.truthValues = truthValues;
  }

  private static double[] getRandomWalk(int numPoints) {

    double[] y = new double[numPoints];
    y[0] = 0;
    for (int i = 1; i < y.length; i++) {
      y[i] = y[i - 1] + Math.random() - .5;
    }
    return y;
  }

  public List<ComparisonResult> compare() {

    List<ComparisonResult> results = new ArrayList<>();
    populateTruthMap();
    populateLegacyMap();
    populateInceptionMap();
    ConfusionMatrix inceptionMatrixTop1 = createInceptionConfusionMatrixTop1();
    addMatrixVariablesToResults("Inception Top 1", inceptionMatrixTop1);

    ConfusionMatrix inceptionMatrixTop5 = createInceptionConfusionMatrixTop5();
    addMatrixVariablesToResults("Inception Top 5", inceptionMatrixTop5);

    log.info("Inception Top 1 Accuracy = {}", inceptionMatrixTop1.getAccuracy());
    log.info("Inception Top 5 Accuracy = {}", inceptionMatrixTop5.getAccuracy());

    //inceptionMatrix.increaseValue();

    Map<String, ConfusionMatrix> legacyConfusions = createConfusionMatricesForDetectors();
    ConfusionMatrix legacyProcessedMatrix = combineLegacyMatrices(legacyConfusions);
    legacyConfusions.put("All LEGACY Without Dropped Results", legacyProcessedMatrix);
    Map<String, ConfusionMatrix> legacyConfusionsWithNonProcessed =
        createConfusionMatricesForDetectors();
    addNonProcessedToLegacyMap(legacyConfusionsWithNonProcessed);
    ConfusionMatrix mainLegacyConfusionWithNonProcessed =
        combineLegacyMatrices(legacyConfusionsWithNonProcessed);
    legacyConfusionsWithNonProcessed.put(
        "All LEGACY With Dropped Results", mainLegacyConfusionWithNonProcessed);
    writeResultsForLegacyMap(legacyConfusionsWithNonProcessed, true);
    writeResultsForLegacyMap(legacyConfusions, false);
    populateCompareOperationMap(false);
    writeResultList("build/" + GRAPH_TIME + "/results.txt", resultList);
    matrixImageCreate(
        inceptionMatrixTop5,
        "Inception Top 5",
        "/home/sam/projects/dev-personal/ml-uilities/Result-Comparer/build/"
            + GRAPH_TIME
            + "/inceptionTop5Matrix.png");
    matrixImageCreate(
        inceptionMatrixTop1,
        "Inception Top 1",
        "/home/sam/projects/dev-personal/ml-uilities/Result-Comparer/build/"
            + GRAPH_TIME
            + "/inceptionTop1Matrix.png");
    createGraphImagesForLegacyMap(GRAPH_TIME, true, legacyConfusionsWithNonProcessed);
    createGraphImagesForLegacyMap(GRAPH_TIME, true, legacyConfusions);
    for (CompareOperation compareOperation : compareOperations) {
      results.add(doComparison(compareOperation));
    }

    return results;
  }

  private void writeResultsForLegacyMap(Map<String, ConfusionMatrix> legacyConfusions, boolean isWithDropped) {
    for (Map.Entry<String, ConfusionMatrix> entry : legacyConfusions.entrySet()) {
      if(isWithDropped){
        addMatrixVariablesToResults(entry.getKey() +" With Dropped Results", entry.getValue());
      }else{
        addMatrixVariablesToResults(entry.getKey()+" Without Dropped Results", entry.getValue());
      }

    }
  }

  private void addNonProcessedToLegacyMap(Map<String, ConfusionMatrix> legacyConfusions) {
    for (Map.Entry<String, ConfusionMatrix> entry : legacyConfusions.entrySet()) {
      log.info(
          "Matrix for detector {} only processed {} files never processed",
          entry.getKey(),
          entry.getValue().getTotalSum());
      int added = addNonProcessedToMatrix(entry.getValue());
      log.info("Matrix for detector {} had {} files never processed", entry.getKey(), added);
    }
  }

  private void createGraphImagesForLegacyMap(
      String outputPath, boolean isDroppedIncluded, Map<String, ConfusionMatrix> legacyConfusions) {
    if (isDroppedIncluded) {
      for (Map.Entry<String, ConfusionMatrix> legacyDetectorMatrix : legacyConfusions.entrySet()) {
        matrixImageCreate(
            legacyDetectorMatrix.getValue(),
            legacyDetectorMatrix.getKey() + "With Dropped Results",
            "build/"
                + outputPath
                + "/"
                + legacyDetectorMatrix.getKey().replaceAll(":", "")
                + "-with-dropped-results"
                + ".png");
      }
    } else {
      for (Map.Entry<String, ConfusionMatrix> legacyDetectorMatrix : legacyConfusions.entrySet()) {
        matrixImageCreate(
            legacyDetectorMatrix.getValue(),
            legacyDetectorMatrix.getKey() + "Without Dropped Results",
            "/home/sam/projects/dev-personal/ml-uilities/Result-Comparer/build/"
                + GRAPH_TIME
                + "/"
                + legacyDetectorMatrix.getKey().replaceAll(":", "")
                + ".png");
      }
    }
  }

  private void addMatrixVariablesToResults(String matrixName, ConfusionMatrix cm) {
    resultList.add("----------|  " + "Start" + "|  ----------");
    resultList.add("----------|  " + matrixName + "|  ----------");
    resultList.add("Average Accuracy = [ " + String.valueOf(cm.getAccuracy()) + " ]");
    resultList.add("Average Precision = [ " + String.valueOf(cm.getAvgPrecision()) + " ]");
    resultList.add("Average Macro F Measure = [ " + String.valueOf(cm.getMacroFMeasure()) + " ]");
    resultList.add("Average Micro F Measure = [ " + String.valueOf(cm.getMicroFMeasure()) + " ]");
    resultList.add("Cohen's Kappa = [ " + String.valueOf(cm.getCohensKappa()) + " ]");
    resultList.add("Total Sum of Predictions = [ " + String.valueOf(cm.getTotalSum()) + " ]");
    resultList.add("---------| By Class |-----------");
    resultList.add(
        String.format("|%-30s|%-30s|%-30s|%-30s|", "Label", "Recall", "Precision", "F-Measure"));
    resultList.add(
        "-----------------------------------------------------"
            + "-----------------------------------------------------------------");
    for (String label : cm.getRecallForLabels().keySet()) {
      String printString =
          String.format(
              "|%-30s|%-30s|%-30s|%-30s|",
              label,
              String.valueOf(cm.getRecallForLabels().get(label)),
              String.valueOf(cm.getPrecisionForLabels().get(label)),
              String.valueOf(cm.getFMeasureForLabels().get(label)));
      resultList.add(printString);
    }
    resultList.add(
            "-----------------------------------------------------"
                    + "-----------------------------------------------------------------");
    resultList.add("----------|  " + matrixName + "|  ----------");
    resultList.add("----------|  " + "End" + "|  ----------");
    resultList.add("\n");
    resultList.add("\n");
  }

  private String tableString(String... strings) {
    String fromatString = "%s15";

    for (String s : strings) {
      fromatString = fromatString + "%15";
    }
    return String.format(fromatString, strings);
  }

  private void writeResultList(String resultFilePath, List<String> resultList) {
    File results = new File(resultFilePath);
    if (results.exists()) {
      results.delete();
    }
    if (!results.getParentFile().exists()) {
      results.getParentFile().mkdirs();
    }
    try (FileWriter fw = new FileWriter(results, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw)) {
      for (String s : resultList) {
        out.println(s);
      }
    } catch (Exception e) {
      log.error("Error occurred while writing results to output file {}", e);
    }
  }

  private Integer addNonProcessedToMatrix(ConfusionMatrix confusionMatrix) {
    int numberAdded = 0;
    String truth;
    for (Map.Entry<String, UUID> entry : truthByName.entrySet()) {
      if (!confusionMatrix.getResultsMap().containsKey(entry.getKey())) {
        truth = truthMap.get(entry.getValue());
        numberAdded++;
        //Means we never had a result for this one.
        //Which means LEGACY determined it was below threshold and doesnt matter.
        //But if we truthed it and found something that does, its wrong.
        confusionMatrix.increaseValue(truth, getWrongAnswer(truth), 1, entry.getKey());
      }
    }
    return numberAdded;
  }

  private String getWrongAnswer(String truth) {
    String result = null;
    for (String answers : truthMap.values()) {
      if (!answers.equalsIgnoreCase(truth)) {
        result = answers;
        break;
      }
    }
    return result;
  }

  /** Returns all the images that were deemed below threshold by LEGACY. * */
  private List<String> getLegacyNonProcessed(List<ComparisonResult> legacyIncorrectList) {
    List<String> nonProcessedList = new ArrayList<>();
    for (ComparisonResult rrr : legacyIncorrectList) {
      if (!rrr.isLegacyProcessed()) {
        nonProcessedList.add(rrr.getImageName());
      }
    }
    return nonProcessedList;
  }

  private void attemptToPrintMatrix(ConfusionMatrix confusionMatrix) {
    List<List<String>> matrix = confusionMatrix.prepareToString();
    StringBuilder sb = null;
    sb = new StringBuilder();
    for (List<String> strings : matrix) {

      for (String s : strings) {
        sb.append(s + "\t");
      }
      log.info(sb.toString());
      sb.append("\n");
    }
    log.info(sb.toString());
  }

  public void matrixImageCreate(
      ConfusionMatrix confusionMatrix, String matrixTitle, String outputPath) {
    File file = new File(outputPath);
    File pFile = file.getParentFile();

    if (!pFile.exists()) {
      pFile.mkdirs();
    }
    List<List<String>> matrix = confusionMatrix.prepareToString();
    OneLiners.rmdirs(PythonScriptUtil.DEFAULT_PATH);
    List<String> labels = new ArrayList<>();
    for (int x = 1; x < matrix.get(0).size(); x++) {
      labels.add("'" + matrix.get(0).get(x) + "'");
    }
    JyPlot plt = new JyPlot();
    plt.write("from matplotlib import cm");
    plt.write("plt.figure(figsize=(12,12))");
    plt.title(matrixTitle + " Confusion Matrix");
    plt.write("plt.axes().set_axisbelow(True)");
    plt.grid();
    //plt.axes("set_axisbelow=True");
    plt.ylabel("True Labels");
    plt.xlabel("Actual Predictions");
    int[] ticks = new int[labels.size() + 1];
    for (int x = 0; x < labels.size(); x++) {
      ticks[x] = x;
    }
    ticks[labels.size()] = labels.size();
    //plt.imshow(ticks, "interpolation='nearest'", "cmap='Blues'");
    plt.xticks(ticks, labels, "rotation = 90");
    plt.yticks(ticks, labels);
    plt.yticks("fontname='Times New Roman'");
    plt.xticks("fontname='Times New Roman'");
    for (int x = 0; x < labels.size(); x++) {
      for (List<String> cList : matrix) {
        if (cList.get(0).equalsIgnoreCase(labels.get(x).replaceAll("'", ""))) {
          for (int y = 1; y < cList.size(); y++) {
            plt.text(x, y - 1, cList.get(y), "ha='left', va='bottom'", "color='black'");
            //plt.text(x+1,y-1,cList.get(y),"ha='right', va='bottom'","color='black'");
          }
        }
      }
    }
    //plt.text(0,0,50,"horizontalalignment='center'","color='black'");
    //plt.text(3,4,50,"horizontalalignment='center'","color='black'");

    //plt.write("fig_size = plt.rcParams['figure.figsize']");
    //plt.write("fig_size[0] = 24");
    //plt.write("fig_size[1] = 24");
    //plt.write("plt.rcParams['figure.figsize'] = fig_size");

    plt.tight_layout();
    plt.savefig("r'" + outputPath + ".pdf'", "bbox_inches='tight'");

    //plt.show();
    plt.exec();
    //plt.imshow(matrix, "interpolation='nearest'", "cmap = plt.cm.Blues");
  }

  private Map<String, List<LegacyResult>> createDetectorResultMap() {
    Map<String, List<LegacyResult>> detectorResultMap = new HashMap<>();
    for (LegacyResult legacyResult : legacyResults) {
      if (truthMap.containsKey(legacyResult.getUuid())) {
        detectorResultMap.computeIfAbsent(legacyResult.getDetector(), k -> new ArrayList<>());
        detectorResultMap.get(legacyResult.getDetector()).add(legacyResult);
      }
    }
    return detectorResultMap;
  }

  private Map<String, ConfusionMatrix> createConfusionMatricesForDetectors() {
    Map<String, List<LegacyResult>> detectorMap = createDetectorResultMap();
    Map<String, ConfusionMatrix> confusionMatrixMap = new HashMap<>();
    for (Map.Entry<String, List<LegacyResult>> entry : detectorMap.entrySet()) {
      confusionMatrixMap.put(entry.getKey(), createLegacyConfusionMatrixForDetector(entry.getValue()));
    }

    return confusionMatrixMap;
  }

  private ConfusionMatrix combineLegacyMatrices(Map<String, ConfusionMatrix> confusionMatrices) {
    List<ConfusionMatrix> matrices = new ArrayList<>();

    for (ConfusionMatrix matrix : confusionMatrices.values()) {
      matrices.add(matrix);
    }
    ConfusionMatrix[] confusionMatrices1 = matrices.toArray(new ConfusionMatrix[0]);

    return ConfusionMatrix.createCumulativeMatrix(confusionMatrices1);
  }

  private ConfusionMatrix createLegacyConfusionMatrixMain() {
    Map<String, Boolean> answeredMap = new HashMap<>();
    ConfusionMatrix legacyMatrix = new ConfusionMatrix();
    String correctClass = null;
    int counter = 0;
    for (CompareOperation op : compareOperationMap.values()) {
      correctClass = truthMap.get(op.getUuid());

      if (op.getLegacyResults().isEmpty()) {
        for (String s : truthMap.values()) {
          if (!s.equalsIgnoreCase(correctClass)) {
            legacyMatrix.increaseValue(correctClass, s, 1, op.getImageName());
            break;
          }
        }
      } else {
        for (LegacyResult result : op.getLegacyResults()) {
          counter++;
          boolean wasCorrect = incrementMatrixForLegacyResult(legacyMatrix, result, answeredMap);
          if (wasCorrect) {
            break;
          }
        }
      }
    }
    return legacyMatrix;
  }

  private boolean incrementMatrixForLegacyResult(
      ConfusionMatrix legacyMatrix, LegacyResult result, Map<String, Boolean> answeredMap) {
    String primary = null;
    String secondary = null;
    String correctClass = null;

    boolean legacyRight;
    correctClass = truthMap.get(result.getUuid());
    primary = getLegacyCorrectStringIfExists(result.getDetectionType(), correctClass);
    secondary = getLegacyCorrectStringIfExists(result.getDetectionSubType(), correctClass);
    if (answeredMap.containsKey(result.getUuid().toString())) {
      if (!answeredMap.get(result.getUuid().toString())) {
        if (secondary != null && correctClass.contains(secondary)) {
          legacyMatrix.increaseValue(correctClass, secondary, 1, result.getImageName());
          legacyRight = true;
        } else if (primary != null && correctClass.contains(primary)) {
          legacyMatrix.increaseValue(correctClass, primary, 1, result.getImageName());
          legacyRight = true;
        } else {
          legacyRight = false;
        }
      } else {
        if (secondary != null && correctClass.contains(secondary)) {
          legacyRight = true;
        } else if (primary != null && correctClass.contains(primary)) {
          legacyRight = true;
        } else {
          legacyRight = false;
        }
      }
    } else {
      if (secondary != null && correctClass.contains(secondary)) {
        legacyMatrix.increaseValue(correctClass, secondary, 1, result.getImageName());
        legacyRight = true;
      } else if (primary != null && correctClass.contains(primary)) {
        legacyMatrix.increaseValue(correctClass, primary, 1, result.getImageName());
        legacyRight = true;
      } else {
        legacyRight = false;
      }
    }

    if (!legacyRight) {
      if (!answeredMap.containsKey(result.getUuid().toString())) {
        answeredMap.put(result.getUuid().toString(), false);
        if (secondary != null) {
          legacyMatrix.increaseValue(correctClass, secondary, 1, result.getImageName());
        } else if (primary != null) {
          legacyMatrix.increaseValue(correctClass, primary, 1, result.getImageName());
        } else {
          for (String s : truthMap.values()) {
            if (!s.equalsIgnoreCase(correctClass)) {
              legacyMatrix.increaseValue(correctClass, s, 1, result.getImageName());
              break;
            }
          }
        }
      }
    }
    return legacyRight;
  }

  private ConfusionMatrix createLegacyConfusionMatrixForDetector(List<LegacyResult> results) {
    ConfusionMatrix legacyMatrix = new ConfusionMatrix();
    for (LegacyResult legacyResult : results) {
      String correctClass = truthMap.get(legacyResult.getUuid());
      String primary = null;
      String secondary = getLegacyCorrectStringIfExists(legacyResult.getDetectionSubType(), correctClass);
      if(secondary == null || secondary.equalsIgnoreCase("unknown")){
       primary = getLegacyCorrectStringIfExists(legacyResult.getDetectionType(), correctClass);
      }

      if (secondary != null && correctClass.contains(secondary)) {
        legacyMatrix.increaseValue(correctClass, secondary, 1, legacyResult.getImageName());
      } else if (primary != null && correctClass.contains(primary)) {
        legacyMatrix.increaseValue(correctClass, primary, 1, legacyResult.getImageName());
      } else {
        if (primary != null) {
          legacyMatrix.increaseValue(correctClass, primary, 1, legacyResult.getImageName());
        } else {
          for (String s : truthMap.values()) {
            if (!s.equalsIgnoreCase(correctClass)) {
              legacyMatrix.increaseValue(correctClass, s, 1, legacyResult.getImageName());
              break;
            }
          }
        }
      }
    }

    return legacyMatrix;
  }

  private ConfusionMatrix createInceptionConfusionMatrixTop1() {
    ConfusionMatrix cm = new ConfusionMatrix();
    for (AlgoResult algoResult : algoResults) {
      String correctClass = algoResult.getImageClass();
      InceptionTops tops = getInceptionTops(algoResult);
      cm.increaseValue(
          correctClass,
          tops.getTop1Label().replaceAll(" ", "-"),
          1,
          algoResult.getImageName());
    }
    return cm;
  }

  private ConfusionMatrix createInceptionConfusionMatrixTop5() {
    boolean correctInK = false;
    ConfusionMatrix cm = new ConfusionMatrix();
    for (AlgoResult algoResult : algoResults) {
      String correctClass = algoResult.getImageClass();
      InceptionTops tops = getInceptionTops(algoResult);
      for (String label : tops.getTop5Labels()) {
        String sani = label.replaceAll(" ", "-");
        if (sani.equalsIgnoreCase(correctClass)) {
          correctInK = true;
          break;
        }
      }
      if (correctInK) {
        cm.increaseValue(correctClass, correctClass, 1, algoResult.getImageName());
      } else {
        cm.increaseValue(
            correctClass,
            tops.getTop1Label().replaceAll(" ", "-"),
            1,
            algoResult.getImageName());
      }
    }
    return cm;
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

      //subcorrect = testLegacyResultForCorrect(detectiongSubtype, truth);

      subcorrect = (truth.contains(getLegacyCorrectString(detectiongSubtype, truth)));

      if (subcorrect) {
        comparisonResult.getLegacyCorrectAlgorithims().put(legacyResult.getDetector(), detectorScore);
        comparisonResult.getLegacyCorrectLabels().put(legacyResult.getDetector(), detectiongSubtype);
        comparisonResult.setLegacyCorrect(true);
      }
      if (!subcorrect) {
        primCorrect = (truth.contains(getLegacyCorrectString(detectionType, truth)));
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

  private String getLegacyCorrectString(String legacyPredict, String truth) {
    String approxLabel = null;
    if (legacyPredict != null) {
      if (legacyPredict.equalsIgnoreCase("face")) {
        if (truth.contains("person")) {
          approxLabel = truth;
        } else {
          approxLabel = "person";
        }
      }
      if (legacyPredict.equalsIgnoreCase("person")) {
        if (truth.contains("person")) {
          approxLabel = truth;
        } else {
          approxLabel = "person";
        }
      } else if (legacyPredict.equalsIgnoreCase("vehicle")) {
        if (truth.contains("vehicle")) {
          approxLabel = truth;
        } else {
          approxLabel = "vehicle";
        }
      } else if (legacyPredict.equalsIgnoreCase("bus")) {
        if (truth.contains("bus")) {
          approxLabel = truth;
        } else {
          approxLabel = "bus";
        }
      } else if (legacyPredict.equalsIgnoreCase("sedan")) {
        if (truth.contains("sedan")) {
          approxLabel = truth;
        } else {
          approxLabel = "sedan";
        }
      } else if (legacyPredict.equalsIgnoreCase("weapon")) {
        if (truth.contains("weapon")) {
          approxLabel = truth;
        } else {
          approxLabel = "weapon";
        }
      } else if (legacyPredict.equalsIgnoreCase("rpg")) {
        if (truth.contains("weapon")) {
          approxLabel = truth;
        } else {
          approxLabel = "weapon";
        }
      } else if (legacyPredict.equalsIgnoreCase("ak47")) {
        if (truth.contains("weapon")) {
          approxLabel = truth;
        } else {
          approxLabel = "weapon";
        }
      } else if (legacyPredict.equalsIgnoreCase("rpg_plus_launcher")) {
        if (truth.contains("weapon")) {
          approxLabel = truth;
        } else {
          approxLabel = "weapon";
        }
      } else if (legacyPredict.equalsIgnoreCase("building")) {
        if (truth.contains("building")) {
          approxLabel = truth;
        } else {
          approxLabel = "building";
        }
      } else if (legacyPredict.equalsIgnoreCase("motorcycle")) {
        if (truth.contains("vehicle")) {
          approxLabel = truth;
        } else {
          approxLabel = "vehicle";
        }
      } else if (legacyPredict.equalsIgnoreCase("scooter")) {
        if (truth.contains("vehicle")) {
          approxLabel = truth;
        } else {
          approxLabel = "vehicle";
        }
      } else if (legacyPredict.equalsIgnoreCase("\\N")) {
        if (truth.contains("unknown")) {
          approxLabel = truth;
        } else {
          approxLabel = "unknown";
        }
      }
    }
    return approxLabel;
  }

  private String getLegacyCorrectStringIfExists(String legacyPredict, String truth) {
    if (!legacyPredicts.containsKey(legacyPredict)) {
      legacyPredicts.put(legacyPredict, 1);
    } else {
      int x = legacyPredicts.get(legacyPredict);
      legacyPredicts.put(legacyPredict, x + 1);
    }
    String approxLabel = null;
    if (legacyPredict != null) {
      if (legacyPredict.equalsIgnoreCase("face")) {
        if (truth.contains("person")) {
          approxLabel = truth;
        } else {
          approxLabel = "person";
        }
      }
      if (legacyPredict.equalsIgnoreCase("person")) {
        if (truth.contains("person")) {
          approxLabel = truth;
        } else {
          approxLabel = "person";
        }
      } else if (legacyPredict.equalsIgnoreCase("vehicle")) {
        if (truth.contains("vehicle")) {
          approxLabel = truth;
        } else {
          approxLabel = "vehicle";
        }
      } else if (legacyPredict.equalsIgnoreCase("bus")) {
        if (truth.contains("bus")) {
          approxLabel = truth;
        } else {
          approxLabel = "bus";
        }
      } else if (legacyPredict.equalsIgnoreCase("sedan")) {
        if (truth.contains("sedan")) {
          approxLabel = truth;
        } else {
          approxLabel = "sedan";
        }
      } else if (legacyPredict.equalsIgnoreCase("weapon")) {
        if (truth.contains("weapon")) {
          approxLabel = truth;
        } else {
          approxLabel = "weapon";
        }
      } else if (legacyPredict.equalsIgnoreCase("rpg")) {
        if (truth.contains("rpg")) {
          approxLabel = truth;
        } else {
          approxLabel = "rpg";
        }
      } else if (legacyPredict.equalsIgnoreCase("ak47")) {
        if (truth.contains("other-weapon")) {
          approxLabel = truth;
        } else {
          approxLabel = "other-weapon";
        }
      } else if (legacyPredict.equalsIgnoreCase("rpg_plus_launcher")) {
        if (truth.contains("rpg-launcher")) {
          approxLabel = truth;
        } else {
          approxLabel = "rpg-launcher";
        }
      } else if (legacyPredict.equalsIgnoreCase("building")) {
        if (truth.contains("building")) {
          approxLabel = truth;
        } else {
          approxLabel = "building";
        }
      } else if (legacyPredict.equalsIgnoreCase("motorcycle")) {
        if (truth.contains("motorcycle")) {
          approxLabel = truth;
        } else {
          approxLabel = "motorcycle";
        }
      } else if (legacyPredict.equalsIgnoreCase("\\N")) {
        if (truth.contains("unknown")) {
          approxLabel = truth;
        } else {
          approxLabel = "unknown";
        }
      }
    }
    return approxLabel;
  }

  private boolean testLegacyResultForCorrect(String legacyPredict, String truth) {
    boolean correct = false;
    if (legacyPredict != null) {
      if (legacyPredict.equalsIgnoreCase("face")) {
        if (truth.contains("person")) {
          correct = true;
        }
      }
      if (legacyPredict.equalsIgnoreCase("person")) {
        if (truth.contains("person")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("vehicle")) {
        if (truth.contains("vehicle")) {
          correct = true;
        }
      } else if (legacyPredict.equalsIgnoreCase("bus")) {
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
      UUID uuid = UUID.nameUUIDFromBytes((truthValue.getFileName()).getBytes());
      if (truthMap.containsKey(uuid)) {
        log.info("Uh  OH Spagetthiios");
      }
      if (truthByName.containsKey(truthValue.getFileName())) {
        log.info("Uh  OH Spagetthiios");
      }
      truthByName.put(truthValue.getFileName(), uuid);
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
        compareOperation.setImageName(algoResult.getImageName());
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

}
