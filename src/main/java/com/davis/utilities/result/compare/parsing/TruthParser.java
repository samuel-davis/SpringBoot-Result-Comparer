package com.davis.utilities.result.compare.parsing;

import com.davis.utilities.result.compare.api.CsvTruthValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class TruthParser {
  private static final String COMMA = ",";
  private static final Logger log = LoggerFactory.getLogger(TruthParser.class.getName());

  private Map<String, CsvTruthValue> values = new TreeMap<>();



  private Function<String, CsvTruthValue> mapToItem =
          (line) -> {
            //String fileLocation = StringUtils.substringBefore(line, COMMA_QUOTE);
            //String detectionType = StringUtils.substring(fileLocation, )
            String intialSplit[] = line.split(COMMA); // a CSV has comma separated lines

            String numericName = getTypeForValueOrNull(String.class,intialSplit[0]);
            if(!values.containsKey(numericName)){
              values.put(numericName,new CsvTruthValue());
            }
            //Having issues splitting here.
            values.get(numericName).setNumericName(numericName);
            values.get(numericName).setSanitizedName(getTypeForValueOrNull(String.class,intialSplit[1]));
            values.get(numericName).setComboClassTruth(getTypeForValueOrNull(String.class,intialSplit[2]));
            values.get(numericName).setOriginalTruth(getTypeForValueOrNull(String.class,intialSplit[3]));
            values.get(numericName).addNewTruth(getTypeForValueOrNull(String.class,intialSplit[4]));
            values.get(numericName).setScene(getTypeForValueOrNull(String.class,intialSplit[5]));
            return values.get(numericName);
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

  public List<CsvTruthValue> getTruthValues(String truthCsv) {
    List<CsvTruthValue> inputList = new ArrayList<>();
    List<CsvTruthValue> truthValueList = new ArrayList<>();
    File inputF = Paths.get(truthCsv).toAbsolutePath().toFile().getAbsoluteFile();

    try (InputStream inputFS = new FileInputStream(inputF)) {
      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
      // skip the header of the csv
      inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
      br.close();
    } catch (Exception e) {
      log.error("Exception encountered {}", e);
    }
    for(CsvTruthValue value : values.values()){
      truthValueList.add(value);
    }

    return truthValueList;
  }
}
