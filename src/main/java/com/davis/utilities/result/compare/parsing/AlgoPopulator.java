package com.davis.utilities.result.compare.parsing;

import com.davis.utilities.result.compare.dao.ResultRepository;
import com.davis.utilities.result.compare.entities.AlgoResult;
import com.davis.utilities.result.compare.entities.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 11/6/17.
 */
@Component
public class AlgoPopulator {
  private static final Logger log = LoggerFactory.getLogger(AlgoPopulator.class.getName());
  @Autowired private ResultRepository resultRepository;

  public void populate(String modelResultJsonPath, String modelKey) {
    long startTime = System.currentTimeMillis();
    log.info("Beginning to populate database with Algorithm results. ");
    //List<Result> resultList = resultRepository.findAll();
    //Map<String, Result> resultMap = new HashMap<>();
    //for(Result r : resultList){
    //  resultMap.put(r.getNumericName(), r);
    //}
    AlgoParser parser = new AlgoParser();
    List<AlgoResult> results =
        parser.getInceptionResults(
            modelResultJsonPath, modelKey);

    for (AlgoResult r : results) {

      String numericName = StringUtils.substringAfterLast(r.getImagePath(), "/");
      //  if(!resultMap.containsKey(numericName)){
      //    log.error("{} was not located in resultMap. ", numericName);
      //    continue;
      //  }
      Result result = resultRepository.findFirstByNumericName(numericName);
      result.getAlgoResults().add(r);
      result.setAlgoProcessed(true);
      resultRepository.save(result);
    }

    //List<Result> saveList = new ArrayList<>();
    //saveList.addAll(resultMap.values());
    //resultRepository.save(saveList);
    resultRepository.flush();
    log.info(
        "Algorithim results:: Add to database complete. Total time required in milliseconds {}",
        (System.currentTimeMillis() - startTime));
  }
}
