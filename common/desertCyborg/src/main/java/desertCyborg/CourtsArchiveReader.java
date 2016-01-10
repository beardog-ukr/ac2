package desertCyborg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// ===========================================================================

public class CourtsArchiveReader {

  private static final Logger logger = LoggerFactory.getLogger("dc.car");

  public boolean readFile(String filename){
    logger.debug("Doing something " + filename);

    String unprocessedData = "" ;



    return true;
  }

  public boolean processJSON(String jsonData) {
    JSONParser parser = new JSONParser();
    CourtsArchiveParser cap = new CourtsArchiveParser();
    boolean result = true;
    try {
      parser.parse(jsonData, cap);
    }
    catch(ParseException pe){
      pe.printStackTrace();
      result = false;
    }

    return result;
  }
}
