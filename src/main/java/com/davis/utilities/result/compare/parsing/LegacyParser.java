package com.davis.utilities.result.compare.parsing;

import com.davis.utilities.result.compare.entities.LegacyResult;
import com.davis.utilities.result.compare.entities.LegacyResult;
import com.davis.utilities.result.compare.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 9/27/17.
 */
public class LegacyParser {
  private static final String COMMA_QUOTE = ",\"";
  private static final String COMMA = ",";
  private static final Logger log = LoggerFactory.getLogger(LegacyParser.class.getName());
  private Function<String, LegacyResult> mapToItem =
      (line) -> {
        //String fileLocation = StringUtils.substringBefore(line, COMMA_QUOTE);
        //String detectionType = StringUtils.substring(fileLocation, )
        String intialSplit[] = line.split(COMMA_QUOTE); // a CSV has comma separated lines
        LegacyResult item = new LegacyResult();

        //Having issues splitting here.
        if (intialSplit[2].contains("\\N")) {
          String detectionSplit[] = intialSplit[2].split(COMMA);
          item.setFileLocation(getTypeForValueOrNull(String.class, intialSplit[0]));
          item.setTriageValue(getTypeForValueOrNull(String.class, intialSplit[1]));
          item.setDetectionType(getTypeForValueOrNull(String.class, detectionSplit[0]));
          item.setDetectionSubType(getTypeForValueOrNull(String.class, detectionSplit[1]));
          item.setConfidence(getTypeForValueOrNull(String.class, intialSplit[3]));
          item.setBoundingBox(getTypeForValueOrNull(String.class, intialSplit[4]));
          item.setDetector(getTypeForValueOrNull(String.class, intialSplit[5]));
          item.setDetectorScore(getTypeForValueOrNull(String.class, intialSplit[6]));
          item.setDetectorConfidence(getTypeForValueOrNull(String.class, intialSplit[7]));
          item.setDetectorBoundingBox(getTypeForValueOrNull(String.class, intialSplit[8]));
        } else {
          item.setFileLocation(getTypeForValueOrNull(String.class, intialSplit[0]));
          item.setTriageValue(getTypeForValueOrNull(String.class, intialSplit[1]));
          item.setDetectionType(getTypeForValueOrNull(String.class, intialSplit[2]));
          item.setDetectionSubType(getTypeForValueOrNull(String.class, intialSplit[3]));
          item.setConfidence(getTypeForValueOrNull(String.class, intialSplit[4]));
          item.setBoundingBox(getTypeForValueOrNull(String.class, intialSplit[5]));
          item.setDetector(getTypeForValueOrNull(String.class, intialSplit[6]));
          item.setDetectorScore(getTypeForValueOrNull(String.class, intialSplit[7]));
          item.setDetectorConfidence(getTypeForValueOrNull(String.class, intialSplit[8]));
          item.setDetectorBoundingBox(getTypeForValueOrNull(String.class, intialSplit[9]));
        }
        if(item.getFileLocation() != null){
          item.setImageName(Utils.getFilename(item.getFileLocation()));
          item.setUuid(UUID.nameUUIDFromBytes(item.getImageName().getBytes()).toString());
        }

        return item;
      };

  private <T> T getTypeForValueOrNull(Class<T> type, String element) {
    T result = null;
    if (element != null && !element.trim().equalsIgnoreCase("")) {
      //Removing Quotes Characters
      if (element.indexOf("\"") == 0) {
        element = element.substring(1, element.length());
      }
      if (element.lastIndexOf("\"") == element.length() - 1) {
        element = element.substring(0, element.length() - 1);
      }
      result = type.cast(element);
    }
    return result;
  }

  public List<LegacyResult> getLegacyResults(String legacyResultCsv) {
    List<LegacyResult> inputList = new ArrayList<LegacyResult>();
    File inputF = Paths.get(legacyResultCsv).toAbsolutePath().toFile().getAbsoluteFile();
    try (InputStream inputFS = new FileInputStream(inputF)) {
      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
      // skip the header of the csv
      inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
      br.close();
    } catch (Exception e) {
      log.error("Exception encountered {}", e);
    }
    Map<String, Integer> nonDuplicatedMap = new HashMap<>();
    for (LegacyResult result : inputList) {
      if (nonDuplicatedMap.get(result.getFileLocation()) == null) {
        nonDuplicatedMap.put(result.getFileLocation(), 0);
      }
    }

    log.info("There is {} number of non duplicated images inside of the LEGACY results list.", nonDuplicatedMap.size());
    return inputList;
  }
}
