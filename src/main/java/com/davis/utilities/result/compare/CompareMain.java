package com.davis.utilities.result.compare;

import com.davis.utilities.result.compare.parsing.AlgoPopulator;
import com.davis.utilities.result.compare.parsing.TruthPopulator;
import com.davis.utilities.result.compare.parsing.LegacyPopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/** The type Main. */
@SpringBootApplication
@EnableAutoConfiguration
public class CompareMain {
  private static final Logger log = LoggerFactory.getLogger(CompareMain.class.getName().toString());
  @Autowired TruthPopulator truthPopulator;
  @Autowired AlgoPopulator algoPopulator;
  @Autowired
  LegacyPopulator legacyPopulator;
  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {
    SpringApplication.run(CompareMain.class, args);
  }

  @Bean
  CommandLineRunner runner() {
    truthPopulator.populate(
        "src/main/resources/truth/truth_numerics_Mon_Nov_06_13:34:31_EST_2017.csv");
    algoPopulator.populate(
        "/home/sam/projects/dev-personal/ml-uilities/SpringBoot-Result-Comparer/src/main/resources/old-techngs/inception-nextgen-all.json",
        "inception-nextgen-all");
    legacyPopulator.populate(
        "/home/sam/projects/dev-personal/ml-uilities/SpringBoot-Result-Comparer/src/main/resources/legacy/legacy_NoThreshold_20171030.csv");
    log.info("Database Population Complete");
    return args -> {};
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  /*public static void main2(String[] args) throws IOException {
      AlgoParser inceptionParser = new AlgoParser();
      //Todo(Sam) Make this a command line param
      List<AlgoResult> algoResults =
          inceptionParser.getInceptionResults(
              "/home/sam/projects/dev-personal/ml-uilities/Result-Comparer/src/main/resources/mobile-nextgen.json");
      log.info("Have {} number of inception results.", algoResults.size());
      LegacyParser legacyParser = new LegacyParser();
      //Todo(Sam) Make this a command line param
      List<LegacyResult> legacyResults =
          legacyParser.getLegacyResults(
              "/home/sam/projects/dev-personal/ml-uilities/Result-Comparer/src/main/resources/legacy/legacy_NoThreshold_20171030.csv");
      log.info("Have {} number of Legacy results.", legacyResults.size());
      //TruthParser truthParser = new TruthParser(algoResults);
      //List<TruthValue> truthValues = truthParser.collectTruth();
      //ConfusionComparer confusionComparer =
      //   new ConfusionComparer(legacyResults, algoResults, truthValues);
      //List<ComparisonResult> compareResultsConfusion = confusionComparer.compare();
      //ResultComparer resultComparer = new ResultComparer(legacyResults, algoResults, truthValues);
      //List<ComparisonResult> comparisonResults = resultComparer.compare();

    }
  */
  /*public static void writeResultToFile(Map<String, String> nodeModules, String outputPath)
      throws IOException {
    String outputFileName = Paths.get(outputPath).toAbsolutePath().toString();
    File file = new File(outputFileName);
    if (file.exists()) {
      file.delete();
    }
    file.mkdirs();
    if (file.isDirectory()) {
      file.delete();
    }

    try (Writer writer = new FileWriter(outputFileName)) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(nodeModules, writer);
    }
  }*/

}
