package friedBox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;
//
import desertCyborg.CourtsArchiveReader;
import desertCyborg.CaseItem;
//
import java.util.ArrayList;
//
import friedBox.component.SqliteUpdater;

/**
 * Main class of the FriedBox app.
 */
public class FriedBoxApp {

  private static final Logger logger = LoggerFactory.getLogger("friedBox.app");

  static final String dbFileNameOption = "db";
  static final String jsonFileNameOption = "json";

  protected void printHelpMessage() {
    System.out.println("Application accepts following options:");
    System.out.println("-h or --help              Show this message and exit");
    System.out.println("-l or --loglevel [level]  Change loggilg level. " +
                       "Possible values are \"DEBUG\", \"INFO\", \"WARN\"" +
                       "and \"ERROR\". Default is \"WARN\". ");
    System.out.println("--" + dbFileNameOption
                            + "    Sqlite database file name; mandatory");
    System.out.println("--" + jsonFileNameOption
                            + "     JSON file name; mandatory");
  }

  String dbFileName = "";
  String jsonFileName = "";

  protected boolean proccessArgs(String[] args) {
    CottonFalcon cf = new CottonFalcon();
    cf.addOption("h", "help", false);
    cf.addLongOption("db", true);
    cf.addLongOption("json", true);

    final String logLevelShCO = "l";
    final String logLevelLCO = "loglevel";
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
      logger.warn("Log level changed to {}.", desiredLogLevel);
    }

    if (cf.gotLongOption(dbFileNameOption)) {
      dbFileName = cf.getLongOptionParameter(dbFileNameOption);
    }
    else {
      System.out.println("Error: please specify db name.");
      return false;
    }

    if (cf.gotLongOption(jsonFileNameOption)) {
      jsonFileName = cf.getLongOptionParameter(jsonFileNameOption);
    }
    else {
      System.out.println("Error: please specify json name.");
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

    String courtId = CourtIdExtractor.extract(jsonFileName);
    if (courtId.isEmpty()) {
      logger.error("Failed to extract court if from json filename");
      return false;
    }

    CourtsArchiveReader car = new CourtsArchiveReader();
    boolean readResult = car.readFile(jsonFileName); //
    if (!readResult) {
      logger.error("failed to read file : " + car.getErrorMessage());
      return false;
    }

    ArrayList<CaseItem> items = car.getItems();
    logger.debug( String.format("Got %d items", items.size()) );

    SqliteUpdater sup = new SqliteUpdater();
    sup.setCourtId(courtId);

    if (!sup.connectToDB(dbFileName)) {
      logger.error(sup.getErrorMessage());
      return false;
    }

    for (CaseItem ci:items) {
      if (!sup.addOneItem(ci)) {
        logger.error(sup.getErrorMessage());
        break;
      }
    }

    sup.finish();

    return true;
  }

}
