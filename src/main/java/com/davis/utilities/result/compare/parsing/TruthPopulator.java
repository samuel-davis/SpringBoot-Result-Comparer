package com.davis.utilities.result.compare.parsing;

import com.davis.utilities.result.compare.api.CsvTruthValue;
import com.davis.utilities.result.compare.dao.ResultRepository;
import com.davis.utilities.result.compare.entities.Result;
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
public class TruthPopulator {

  private static final Logger log = LoggerFactory.getLogger(TruthPopulator.class.getName());
  @Autowired private ResultRepository resultRepository;

  public TruthPopulator() {}

  public void populate(String truthCsvFilePath) {
    TruthParser parser = new TruthParser();
    List<CsvTruthValue> values = parser.getTruthValues(truthCsvFilePath);
    if (resultRepository.count() < 1) {
      for (CsvTruthValue value : values) {
        Result result = new Result();
        result.setOriginalTruth(value.getOriginalTruth());
        result.setComboTruth(value.getComboClassTruth());
        result.setNewTruths(value.getNewTruths());
        result.setAllTruths(value.getAllTruths());
        result.setNumericName(value.getNumericName());
        resultRepository.save(result);
      }
    }

    log.info("Total repo size after truth population is {}", resultRepository.count());
  }
}
