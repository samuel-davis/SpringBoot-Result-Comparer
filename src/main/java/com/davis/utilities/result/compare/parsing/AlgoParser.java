package com.davis.utilities.result.compare.parsing;

import com.davis.utilities.result.compare.entities.AlgoResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 9/27/17.
 */
public class AlgoParser {
    private static final Logger log = LoggerFactory.getLogger(AlgoParser.class.getName());
  private ObjectMapper mapper;

  public AlgoParser() {
    mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
  }

  public List<AlgoResult> getInceptionResults(String filePath, String modelKey) {
    List<AlgoResult> results = null;
    /*String pathOfJson =
        Main.class
            .getClassLoader()
            .getResource("NS_50000-TB_100-LR_0.01.pb-results-validation.json")
            .getFile()
            .toString();*/
    File libResponse = Paths.get(filePath).toAbsolutePath().toFile().getAbsoluteFile();
    AlgoResult[] inceptionArray = null;
    try {
      inceptionArray = mapper.readValue(libResponse, AlgoResult[].class);
      results = new ArrayList<>(Arrays.asList(inceptionArray));
      for(AlgoResult r : results){
        r.setModelKey(modelKey);
      }
      log.info("There is {} Inception Results ", results.size());
    } catch (IOException e) {
      log.error("Could not parse inception results json. {}", e);
    }

    return results;
  }
}
