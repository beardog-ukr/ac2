package friedBox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;

/**
 * Main class of the FriedBox app.
 */
public class FriedBoxApp {

  private static final Logger logger = LoggerFactory.getLogger("friedBox.app");

  static final String dbFileNameOption = "db";
  static final String jsonFileNameOption = "json";

  protected void printHelpMessage() {
    System.out.println("Application accepts following options:");
    System.out.println("-h or --help    Show this message and exit");
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



    return true;
  }

}
