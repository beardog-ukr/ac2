package gleeFarm.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;
//
import java.util.List;
//import java.util.ArrayList;
//import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
//
import gleeFarm.component.CourtJsonTransformer;

/**
 * Main class of the GleeFarm app.
 */
public class GleeFarmApp {

  private static final Logger logger = LoggerFactory.getLogger("gleeFarm.app");

  static final String logLevelShCO = "l";
  static final String logLevelLCO = "loglevel";

  static final String fileListLCO = "list";
  static final String resultFolderLCO = "result";

  String errorMessage = "";

  protected void printHelpMessage() {
    System.out.println("Application accepts following options:");
    System.out.println("-h or --help              Show this message and exit");
    System.out.println("-l or --loglevel [level]  Change loggilg level. " +
                       "Possible values are \"DEBUG\", \"INFO\", \"WARN\"" +
                       "and \"ERROR\". Default is \"WARN\". ");
    System.out.println("--" + fileListLCO
                            + "    A text file containing list of files to be processed");
    System.out.println("--" + resultFolderLCO
            + "    A folder to store processed files");
  }

  String listFilename = "";
  String resultFolder = "";

  protected boolean processArgs(String[] args) {
    CottonFalcon cf = new CottonFalcon();
    cf.addOption("h", "help", false);
    cf.addLongOption(fileListLCO, true);
    cf.addLongOption(resultFolderLCO, true);

    cf.addOption(logLevelShCO, logLevelLCO, true);


    boolean cfpr = cf.process(args);
    if (!cfpr) {
      errorMessage = cf.getErrorMessage();
      return false;
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
        errorMessage = String.format("Unknown logging level \"%s\"", desiredLogLevel);
        return false ;
      }
    }

    if (cf.gotLongOption(fileListLCO)) {
      listFilename = cf.getLongOptionParameter(fileListLCO);
    }
    else {
      errorMessage = String.format("Please specify list to be processed." +
                                   "(\"--%s\" option)", fileListLCO);
      return false;
    }

    if (cf.gotLongOption(resultFolderLCO)) {
      resultFolder = cf.getLongOptionParameter(resultFolderLCO);
    }
    else {
      errorMessage = String.format("Please specify folder to store result files." +
              "(\"--%s\" option)", resultFolderLCO);
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

    if (!processArgs(args)) {
      logger.error(errorMessage);
      return false;
    }

    if (!loadFilesToProcess()) {
      logger.error(errorMessage) ;
      return false;
    }

    if (!processFiles()) {
      logger.error(errorMessage) ;
      return false;
    }

    logger.debug("Done something");

    return true;
  }

  List<String> filenamesToProcess;

  /**
   *   Tries to load filenames that should be processed later.
   * Saves loaded list of files in #filenamesToProcess
   * @return true on success
   */
  boolean loadFilesToProcess() {
    FileListLoader fileListLoader = new FileListLoader();
    if (fileListLoader.loadFilesFromFile(listFilename)) {
      filenamesToProcess = fileListLoader.getLoadedList();
    }
    else {
      errorMessage = fileListLoader.getErrorMessage();
      return false;
    }

    return true;
  }

  boolean processOneFile(String rawName, String transformedName) {
    BufferedReader br = null;
    String line = "";
    String rawJSON = "";
    try {
      br = new BufferedReader(new FileReader(rawName));
      while ((line = br.readLine()) != null) {
        rawJSON += line;
      }
    } catch (IOException e) {
      errorMessage = e.getMessage();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          errorMessage = e.getMessage();
        }
      }
    }

    if (rawJSON.isEmpty()||(!errorMessage.isEmpty())) {
      return false;
    }

    CourtJsonTransformer transformer = new CourtJsonTransformer();
    String transformedStr = transformer.transform(rawJSON);
    if (transformedStr.isEmpty()) {
      errorMessage = String.format("Failed to process \"%s\" with message \"%s\"",
              rawName, transformer.getErrorMessage());
      return false;
    }

    try {
      File file = new File(transformedName);

      if (!file.exists()) {
        file.createNewFile();
      }

      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(transformedStr);
      bw.close();
    } catch (IOException e) {
      errorMessage = e.getMessage();
      return false;
    }

    return true;
  }

  /**
   *   Processes files specified in #filenamesToProcess one by one.
   * @param
   * @return true on success
   */
  boolean processFiles() {
    if (filenamesToProcess.size()==0) {
      errorMessage = "Nothing to process, empty list" ;
      return false;
    }

    for(String fn: filenamesToProcess) {
      logger.debug("Processing {}", fn);

      File tf = new File(fn);
      String tfn = resultFolder + "/" + tf.getName();
      if (!processOneFile(fn, tfn)) {
        return false;
      }
    }

    //finally
    return true;
  }

}
