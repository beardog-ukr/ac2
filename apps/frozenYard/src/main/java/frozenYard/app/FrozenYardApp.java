package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;
//
//import java.util.ArrayList;
//
import java.io.File;
//
import desertCyborg.CourtsArchiveReader;

/**
 * Main class of the FrozenYard app.
 */
public class FrozenYardApp {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  static final String dbFileNameOption = "db";
  static final String jsonFileNameOption = "json";
  static final String logLevelShCO = "l";
  static final String logLevelLCO = "loglevel";

  String errorMessage = "";

  protected void printHelpMessage() {
    System.out.println("Application accepts following options:");
    System.out.println("-h or --help              Show this message and exit");
    System.out.println("-l or --loglevel [level]  Change loggilg level. " +
                       "Possible values are \"DEBUG\", \"INFO\", \"WARN\"" +
                       "and \"ERROR\". Default is \"WARN\". ");
    System.out.println("--" + dbFileNameOption
                            + "    Sqlite database file name; mandatory");
    System.out.println("--" + jsonFileNameOption
            + "    JSON file name; mandatory");
  }

  String dbFileName = "";
  String jsonFileName = "";

  protected boolean proccessArgs(String[] args) {
    CottonFalcon cf = new CottonFalcon();
    cf.addOption("h", "help", false);
    cf.addLongOption(dbFileNameOption, true);
    cf.addLongOption(jsonFileNameOption, true);

    cf.addOption(logLevelShCO, logLevelLCO, true);


    boolean cfpr = cf.process(args);
    if (!cfpr) {
      System.out.println("Error: "+ cf.getErrorMessage());
      System.out.println("");
      printHelpMessage();
      return  false;
    }

    if (cf.gotShortOption("h")||cf.gotLongOption("help")) {
      logger.debug("need to show help message"); //
      printHelpMessage();
      return false;
    }

    //--
    if (cf.gotShortOption(logLevelShCO)||cf.gotLongOption(logLevelLCO)) {
      String desiredLogLevel = cf.getOptionParameter(logLevelShCO, logLevelLCO);
      if (desiredLogLevel.equals("DEBUG")) {
        ((ch.qos.logback.classic.Logger)logger).setLevel(ch.qos.logback.classic.Level.DEBUG);
      } else if (desiredLogLevel.equals("INFO")) {
        ((ch.qos.logback.classic.Logger)logger).setLevel(ch.qos.logback.classic.Level.INFO);
      } else if (desiredLogLevel.equals("WARN")) {
        ((ch.qos.logback.classic.Logger)logger).setLevel(ch.qos.logback.classic.Level.WARN);
      } else if (desiredLogLevel.equals("ERROR")) {
        ((ch.qos.logback.classic.Logger)logger).setLevel(ch.qos.logback.classic.Level.ERROR);
      }
      else {
        logger.error("Error: unknown logging level \"{}\"", desiredLogLevel);
        return false ;
      }
      //logger.warn("Log level changed to {}.", desiredLogLevel);
    }

    if (cf.gotLongOption(dbFileNameOption)) {
      dbFileName = cf.getLongOptionParameter(dbFileNameOption);
    }
    else {
      logger.error("Error: please specify db name.");
      return false;
    }

    if (cf.gotLongOption(jsonFileNameOption)) {
      jsonFileName = cf.getLongOptionParameter(jsonFileNameOption);
    }
    else {
      logger.error("Error: please specify db name.");
      return false;
    }

    //finally
    return true;
  }

  /**
   * Performs all required actions.
   * @param args   command line arguments, as in "main".
   * @return true on success, false on some error.
   */
  public boolean doIt(String[] args) {

    if (!proccessArgs(args)) {
      return false;
    }

    if (!performBasicCheck(dbFileName)) {
      logger.error(errorMessage) ;
      return false;
    }

    if (!performBasicCheck(jsonFileName)) {
      logger.error(errorMessage) ;
      return false;
    }

    //jsonFileName = "not.real.another.foobar.json" ;
    if (!performProcessedFilesCheck()) {
      logger.error(errorMessage);
      return false;
    }



//    CourtsArchiveReader car = new CourtsArchiveReader();
//    boolean readResult = car.readFile(jsonFileName); //
//    if (!readResult) {
//       logger.error("failed to read file : " + car.getErrorMessage());
//       return false;
//    }
//    logger.debug(String.format("Got \"%d\" items in json file", car.getItems().size()));


    // String courtId = CourtIdExtractor.extract(jsonFileName);
    // if (courtId.isEmpty()) {
    //   logger.error("Failed to extract court if from json filename {}", jsonFileName);
    //   return false;
    // }
    //


    //ArrayList<CaseItem> items = car.getItems();
    //logger.debug( String.format("Got %d items", items.size()) );

    logger.debug("Done something");

    return true;
  }

  boolean performBasicCheck(String dbFileName) {
    File fl = new File(dbFileName);
    if (!fl.exists()) {
      errorMessage = String.format("File \"%s\" does not exist", dbFileName);
      return false;
    }
    else  {
      logger.debug("File {} exists", dbFileName);
    }

    if (!fl.canRead()) {
      errorMessage = String.format("Can\'t read file \"%s\".", dbFileName);
      return false;
    }

    if (!fl.canWrite()) {
      errorMessage = String.format("Can\'t write to file \"%s\".", dbFileName);
      return false;
    }

    //finally, normally
    return true;
  }

  boolean performProcessedFilesCheck() {
    FilesTableProcessor filestp = new FilesTableProcessor(dbFileName);
    if (filestp.readFilesInfo()){
      if (filestp.wasProcessed(jsonFileName)) {
        errorMessage=String.format("File %s already was processed for %s",
                                   jsonFileName, dbFileName);
        return false;
      }
    }
    else {
      errorMessage="Failed to read processed files list from " + dbFileName;
      errorMessage+= " (" + filestp.getErrorMessage() + ")";
      return false;//
    }

    //finally, normally
    return true;
  }

}
