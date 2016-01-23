package frozenYard.app;

import desertCyborg.CaseItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//
import desertCyborg.CourtsArchiveReader;

/**
 * Main class of the FrozenYard app.
 */
public class FrozenYardApp {
  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  String errorMessage = "";
  String getErrorMessage() {
    return errorMessage;
  }

  boolean processOneFile(String dbFileName, String jsonFileName) {

    if (!performProcessedFilesCheck(dbFileName, jsonFileName)) {
      return false;
    }

    CourtsArchiveReader car = new CourtsArchiveReader();
    boolean readResult = car.readFile(jsonFileName); //
    if (!readResult) {
       errorMessage = "failed to read json : " + car.getErrorMessage();
       return false;
    }

    ArrayList<CaseItem> caseItems = car.getItems();
    logger.debug(String.format("Got \"%d\" items in json file", car.getItems().size()));

    CourtAddressesProcessor addressesProc = new CourtAddressesProcessor(jsonFileName, dbFileName);
    CourtRowIdKeeper crik = new CourtRowIdKeeper();
    if (!addressesProc.processItems(caseItems, crik)) {
      errorMessage = addressesProc.getErrorMessage();
      return false;
    }

    CasesFilter casesFilter = new CasesFilter(dbFileName);
    LinkedList<CaseItem> newItems = new LinkedList<>();
    if (!casesFilter.filterItems(caseItems, newItems)) {
      errorMessage = casesFilter.getErrorMessage() ;
      return false;
    }

    logger.debug("Finally has to add {} cases", newItems.size());

    CasesAdder adder = new CasesAdder(dbFileName);
    if (!adder.addItemsToDb(newItems, jsonFileName, crik)) {
      logger.error(adder.getErrorMessage()) ;
      return false;
    }

    return true;
  }

  /**
   *    Loads list of files to be processed.
   *    Reads the list from file (if it was specified) and/or adds one filename
   * specified with "single file" option.
   * @param listFilename  Filename of a text file, this file should contain
   *                     a list of files that should be processed
   * @param singleFilename
   * @return null on failure, list of files as result
   */
  List<String> prepareListOfFilesToProcess(String listFilename, String singleFilename) {
    List<String> result= new LinkedList<>();

    if (!performBasicCheck(listFilename)) {
      return null;
    }

    if (!listFilename.isEmpty()) {
      try {
        FileReader fileReader = new FileReader(listFilename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
          result.add(line);
        }

        bufferedReader.close();
      } catch (IOException ex) {
        errorMessage = "Error reading file '" + listFilename + "'";
        return null;
        // ex.printStackTrace();
      }
    }

    if ((!singleFilename.isEmpty()) && performBasicCheck(singleFilename)) {
      result.add(singleFilename);
    }

    return result;
  }

  /**
   * Performs all required actions.
   * @param args   command line arguments, as in "main".
   * @return true on success, false on some error.
   */
  public boolean doIt(String[] args) {

    CmdArgsResults cmdOptions = CmdArgsProcessing.proccessArgs(args);
    if (cmdOptions.error) {
      errorMessage = cmdOptions.errorMessage;
      return false;
    }

    if (cmdOptions.showHelp) {
      CmdArgsProcessing.printHelpMessage();
      return true;
    }

//    if (!jsonFileName.isEmpty()) {
//      if (!performBasicCheck(jsonFileName)) {
//        logger.error(errorMessage);
//        return false;
//      }
//    }
//
//    if (!fileListFileName.isEmpty()) {
//      if (!performBasicCheck(fileListFileName)) {
//        logger.error(errorMessage);
//        return false;
//      }
//    }

    List<String> filesToProcess = prepareListOfFilesToProcess(
                         cmdOptions.fileListFileName, cmdOptions.jsonFileName);
    if (filesToProcess.isEmpty()) {
      errorMessage = "Nothing to load";
      return false;
    }

    for (String fn:filesToProcess) {
      logger.info("Processing " + fn +
                   FilenameProcessing.dateToDbFormat(new Date()));
      processOneFile(cmdOptions.dbFileName, fn);
    }

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

  boolean performProcessedFilesCheck(String dbFileName, String jsonFileName) {
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
