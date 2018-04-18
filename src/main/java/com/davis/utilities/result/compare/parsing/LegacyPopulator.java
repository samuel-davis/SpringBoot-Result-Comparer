package com.davis.utilities.result.compare.parsing;

import com.davis.utilities.result.compare.dao.ResultRepository;
import com.davis.utilities.result.compare.entities.Result;
import com.davis.utilities.result.compare.entities.LegacyResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 11/6/17.
 */
@Component
public class LegacyPopulator {
  private static final Logger log = LoggerFactory.getLogger(LegacyPopulator.class.getName());
  @Autowired private ResultRepository resultRepository;

  public LegacyPopulator() {}

  public void populate(String resultCsv) {
    long startTime = System.currentTimeMillis();
    log.info("Beginning to populate database with LEGACY results. ");
    Map<String, Result> resultMap = new HashMap<>();
    LegacyParser parser = new LegacyParser();
    List<LegacyResult> results = parser.getLegacyResults(resultCsv);

    for (LegacyResult legacyResult : results) {
      //smile
      String numericName = StringUtils.substringAfterLast(legacyResult.getFileLocation(), "/");

      if (!resultMap.containsKey(numericName)) {
        //log.error("{} was not located in resultMap. ", numericName);
        Result result = resultRepository.findFirstByNumericName(numericName);
        result.getLegacyResults().add(legacyResult);
        result.setLegacyProcessed(true);
        resultRepository.save(result);
        resultMap.put(numericName, result);
      } else {
        resultMap.get(numericName).getLegacyResults().add(legacyResult);
        resultMap.get(numericName).setLegacyProcessed(true);
        resultRepository.save(resultMap.get(numericName));
      }
    }
    resultRepository.flush();
    log.info(
            "LEGACY results:: Add to database complete. Total time required in milliseconds {}",
            (System.currentTimeMillis() - startTime));
  }
}
