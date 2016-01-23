package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;

public class CmdArgsProcessing {
  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  static final String dbFileNameOption = "db";
  static final String jsonFileNameLCO = "json";
  static final String logLevelShCO = "l";
  static final String logLevelLCO = "loglevel";

  static final String fileListLCO = "list";


  public static void printHelpMessage() {
    System.out.println("Application accepts following options:");
    System.out.println("-h or --help              Show this message and exit");
    System.out.println("-l or --loglevel [level]  Change loggilg level. " +
            "Possible values are \"DEBUG\", \"INFO\", \"WARN\"" +
            "and \"ERROR\". Default is \"WARN\". ");
    System.out.println("--" + dbFileNameOption
            + "    Sqlite database file name; mandatory");
    System.out.println("--" + jsonFileNameLCO + "    JSON file name; ");
    System.out.println("--" + fileListLCO + "    JSON file name; ");
  }

  public static CmdArgsResults proccessArgs(String[] args) {
    CottonFalcon cf = new CottonFalcon();
    cf.addOption("h", "help", false);
    cf.addLongOption(dbFileNameOption, true);
    cf.addLongOption(jsonFileNameLCO, true);
    cf.addLongOption(fileListLCO, true);
    cf.addOption(logLevelShCO, logLevelLCO, true);

    CmdArgsResults result = new CmdArgsResults();

    boolean cfpr = cf.process(args);
    if (!cfpr) {
      result.error = true;
      result.errorMessage =  cf.getErrorMessage();
      return result;
    }

    if (cf.gotShortOption("h")||cf.gotLongOption("help")) {
      logger.debug("need to show help message"); //
      result.showHelp = true;
      return result;
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
        result.setError(String.format("Error: unknown logging level \"%s\"",
                        desiredLogLevel) );
        return result ;
      }
      //logger.warn("Log level changed to {}.", desiredLogLevel);
    }

    if (cf.gotLongOption(dbFileNameOption)) {
      result.dbFileName = cf.getLongOptionParameter(dbFileNameOption);
    }
    else {
      result.setError("Error: please specify db name.");
      return  result;
    }

    if (cf.gotLongOption(jsonFileNameLCO)) {
      result.jsonFileName = cf.getLongOptionParameter(jsonFileNameLCO);
    }

    if (cf.gotLongOption(fileListLCO)) {
      result.fileListFileName = cf.getLongOptionParameter(fileListLCO);
    }

    if (result.jsonFileName.isEmpty() && result.fileListFileName.isEmpty()) {
      result.setError( String.format("Please specify either list of files" +
                       "(\"--%s\" option) or one json file (\"--%s\" option)",
                       fileListLCO, jsonFileNameLCO) );
      return result;
    }

//    if ((!jsonFileName.isEmpty()) && (!fileListFileName.isEmpty())) {
//      errorMessage = String.format("Impossible to specify both \"--%s\"" +
//                     " and \"--%s\" options", fileListLCO, jsonFileNameLCO);
//      return false;
//    }

    //finally
    return result;
  }
}