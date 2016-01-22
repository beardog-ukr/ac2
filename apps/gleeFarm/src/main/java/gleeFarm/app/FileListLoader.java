package gleeFarm.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
//
import java.io.File;
import java.util.LinkedList;
//import java.util.ArrayList;

//

/**
 *   Loads lists of files
 */
public class FileListLoader {

  private static final Logger logger = LoggerFactory.getLogger("gleeFarm.app");

  String errorMessage = "";
  public String getErrorMessage() {
    return errorMessage;
  }

  protected List<String> getListImpl() {
    return new LinkedList<String>() ;
  }

  List<String> loadedFiles = null ;
  public List<String> getLoadedList() {
    if (loadedFiles==null) {
      return getListImpl();
    }
    //else, normally
    return loadedFiles;
  }



  public boolean loadFilesFromFile(String filename) {
    if (!performBasicCheck(filename)) {
      return false;
    }

    List<String> lines = getListImpl() ;

    try {
      FileReader fileReader = new FileReader(filename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      String line ;
      while((line = bufferedReader.readLine()) != null) {
        lines.add(line);
      }

      bufferedReader.close();
    }
    catch(IOException ex) {
      errorMessage = "Error reading file '" + filename + "'";
      return false;
      // ex.printStackTrace();
    }

    //TODO: check if the files exist???

    loadedFiles = lines;
    //normally
    return true;
  }

  public boolean loadFolder(String folderName, String nameTemplate) {
    return true;
  }

  public boolean checkFiles(List<String> filenames) {
    boolean result = true;
    //TODO: implement
    return result;
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

}