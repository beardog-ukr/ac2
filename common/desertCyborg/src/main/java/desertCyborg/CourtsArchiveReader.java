package desertCyborg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
//
import java.util.ArrayList;
//

// ===========================================================================

public class CourtsArchiveReader {

  private static final Logger logger = LoggerFactory.getLogger("desertCyborg");

  String errorMessage = "";
  public String getErrorMessage() {
    return errorMessage;
  }

  ArrayList<CaseItem> items = new ArrayList<CaseItem>();
  public ArrayList<CaseItem> getItems() {
    return items;
  }

  /**
   * Reads JSON file with cases information.
   * @param filename
   * @return  true on success, false if any error (see #getErrorMessage) to get
   *          some error description
   */
  public boolean readFile(String filename){
    logger.debug("Doing something " + filename);

    String unprocessedData = "" ;
    boolean result = true;

    try {
      FileReader fileReader = new FileReader(filename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      String line ;
      while((line = bufferedReader.readLine()) != null) {
        unprocessedData += line;
      }

      bufferedReader.close();
    }
    catch(FileNotFoundException ex) {
      errorMessage = "Unable to open file '" + filename + "'" ;
      result = false;
    }
    catch(IOException ex) {
      errorMessage = "Error reading file '" + filename + "'";
      result = false;
      // ex.printStackTrace();
    }

    if (!result) {
      return false;
    }

    result = processJSON(unprocessedData);

    return result;
  }

  /**
   * Processes given string as JSON with courts data.
   * @param jsonData  cases info in JSON
   * @return true on success (almost always really)
   */
  public boolean processJSON(String jsonData) {
    JSONParser parser = new JSONParser();
    CourtsArchiveParser cap = new CourtsArchiveParser();
    boolean result = true;
    try {
      parser.parse(jsonData, cap);
    }
    catch(ParseException pe){
      //pe.printStackTrace();
      errorMessage = pe.getMessage();
      result = false;
    }

    if (result) {
      items = cap.getItems();
    }

    return result;
  }
}
