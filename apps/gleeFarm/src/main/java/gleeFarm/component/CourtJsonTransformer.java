package gleeFarm.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *   Transforms "original" court json data to "new" form.
 */
public class CourtJsonTransformer {

  private static final Logger logger = LoggerFactory.getLogger("gleeFarm.app");

  String errorMessage = "";
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   *   Transforms "old" data to "new" format.
   * @param jsonData data to be transformed
   * @return JSON data of the new form; empty string in case of some error
   */
  public String transform(String jsonData) {

    JSONParser parser = new JSONParser();
    RawCourtJsonParser rawCourtJsonParser = new RawCourtJsonParser();
    boolean result = true;
    try {
      parser.parse(jsonData, rawCourtJsonParser);
    }
    catch(ParseException pe){
      pe.printStackTrace();
      errorMessage = pe.getMessage();
      return "";
    }

    String sr = rawCourtJsonParser.getTransformedJson();

    return sr;
  }

}