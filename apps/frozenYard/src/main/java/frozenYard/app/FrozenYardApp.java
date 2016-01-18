package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;
//
//import java.util.ArrayList;
//

/**
 * Main class of the FrozenYard app.
 */
public class FrozenYardApp {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  static final String dbFileNameOption = "db";
  //static final String jsonFileNameOption = "json";
  static final String logLevelShCO = "l";
  static final String logLevelLCO = "loglevel";


  protected void printHelpMessage() {
    System.out.println("Application accepts following options:");
    System.out.println("-h or --help              Show this message and exit");
    System.out.println("-l or --loglevel [level]  Change loggilg level. " +
                       "Possible values are \"DEBUG\", \"INFO\", \"WARN\"" +
                       "and \"ERROR\". Default is \"WARN\". ");
    System.out.println("--" + dbFileNameOption
                            + "    Sqlite database file name; mandatory");
  }

  String dbFileName = "";

  protected boolean proccessArgs(String[] args) {
    CottonFalcon cf = new CottonFalcon();
    cf.addOption("h", "help", false);
    cf.addLongOption("db", true);

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
        System.out.println("Error: unknown logging level.");
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

    // String courtId = CourtIdExtractor.extract(jsonFileName);
    // if (courtId.isEmpty()) {
    //   logger.error("Failed to extract court if from json filename {}", jsonFileName);
    //   return false;
    // }
    //
    // CourtsArchiveReader car = new CourtsArchiveReader();
    // boolean readResult = car.readFile(jsonFileName); //
    // if (!readResult) {
    //   logger.error("failed to read file : " + car.getErrorMessage());
    //   return false;
    // }

    //ArrayList<CaseItem> items = car.getItems();
    //logger.debug( String.format("Got %d items", items.size()) );

    logger.debug("Done something");

    return true;
  }

}
