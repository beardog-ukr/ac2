package friedBox.app;

import friedBox.component.NativeUpdater;
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
import friedBox.component.NativeUpdater;

/**
 * Main class of the FriedBox app.
 */
public class FriedBoxApp {

  private static final Logger logger = LoggerFactory.getLogger("friedBox.app");

  static final String dbFileNameOption = "db";
  static final String jsonFileNameOption = "json";
  static final String logLevelShCO = "l";
  static final String logLevelLCO = "loglevel";
  static final String dbUpdaterShCO = "u";
  static final String dbUpdaterLCO = "updater";


  protected void printHelpMessage() {
    System.out.println("Application accepts following options:");
    System.out.println("-h or --help              Show this message and exit");
    System.out.println("-l or --loglevel [level]  Change loggilg level. " +
                       "Possible values are \"DEBUG\", \"INFO\", \"WARN\"" +
                       "and \"ERROR\". Default is \"WARN\". ");
    System.out.println("-" + dbUpdaterShCO + " or --" + dbUpdaterLCO + "[name]"+
                       "Set DB updater component. Possible values are \"jdbc\""+
                       "(default) and \"native\".");
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

    cf.addOption(logLevelShCO, logLevelLCO, true);

    cf.addOption(dbUpdaterShCO, dbUpdaterLCO, true);

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

    //--
    if (cf.gotShortOption(dbUpdaterShCO)||cf.gotLongOption(dbUpdaterLCO) ) {
      String dbcode = cf.getOptionParameter(dbUpdaterShCO, dbUpdaterLCO) ;
      if (dbcode.equals("jdbc")) {
        dbUpdater = new SqliteUpdater();
      }
      else if (dbcode.equals("native")) {
        dbUpdater = new NativeUpdater();
        logger.debug("Now will use sqlite4java updater (native)");
      } else {
        logger.error("Unknown dbupdater name \"{}\"");
        return false;
      }
    }
    else {
      logger.debug("Use default DB updater.");
      dbUpdater = new SqliteUpdater();
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

  DbUpdater dbUpdater = null;


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
      logger.error("Failed to extract court if from json filename {}", jsonFileName);
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

    dbUpdater.setCourtId(courtId);

    if (!dbUpdater.connectToDB(dbFileName)) {
      logger.error(dbUpdater.getErrorMessage());
      return false;
    }

    for (CaseItem ci:items) {
      if (!dbUpdater.addOneItem(ci)) {
        logger.error(dbUpdater.getErrorMessage());
        break;
      }
    }

    dbUpdater.finish();

    return true;
  }

}
