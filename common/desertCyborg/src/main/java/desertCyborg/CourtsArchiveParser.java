package desertCyborg;

//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
//import org.json.simple.parser.JSONParser;
import java.util.ArrayList;

class CourtsArchiveParser implements ContentHandler{

  private static final Logger logger = LoggerFactory.getLogger("dc.cap");

  enum ProcessingStage{
    START
    , READING_CASE_ITEMS
    , RCI_DATE // like "Reading Case Item : Date"
    , RCI_TYPE
    , RCI_NUMBER
    , RCI_INVOLVED
    , RCI_DESC
    , RCI_JUDGE_N
    , RCI_ADDR_N
    , READING_JUDGES
    , READING_ADDRESSES
    , ERROR
    , FINISHED
  }

  ProcessingStage stage;

  ArrayList<String> judges;
  ArrayList<String> addresses;

  class IncompleteCaseItem {
    CaseItem item;
    int jugdeN;
    int addrN;
  }

  ArrayList<IncompleteCaseItem> items;

  CaseItem currentCaseItem;
  int currentJudgeN;
  int currentAddrN;


  public void startJSON() throws ParseException, IOException {
    stage = ProcessingStage.START;
    items = new ArrayList<IncompleteCaseItem>();
    judges = new ArrayList<String>();
    addresses = new ArrayList<String>() ;
    logger.debug("all started");
  }

  public void endJSON() throws ParseException, IOException {
    stage = ProcessingStage.FINISHED;
    logger.debug("all finished");
  }


  public boolean startObject() throws ParseException, IOException {
    logger.debug("started object");
    if (stage==ProcessingStage.START) {
      return true; // nothing to do here
    }

    if (stage==ProcessingStage.READING_CASE_ITEMS) {
      currentCaseItem = new CaseItem();
    }

    return true;
  }

  /**
   * Receive notification of the end of a JSON object.
   *
   * @return false if the handler wants to stop parsing after return.
   * @throws ParseException
   *
   * @see #startObject
   */
  public boolean endObject() throws ParseException, IOException {
    logger.debug("finished object");

    if (stage == ProcessingStage.RCI_DATE ||
        stage == ProcessingStage.RCI_TYPE ||
        stage == ProcessingStage.RCI_NUMBER    ||
        stage == ProcessingStage.RCI_INVOLVED  ||
        stage == ProcessingStage.RCI_DESC      ||
        stage == ProcessingStage.RCI_JUDGE_N   ||
        stage == ProcessingStage.RCI_ADDR_N) {
      stage = ProcessingStage.READING_CASE_ITEMS;

      IncompleteCaseItem ii = new IncompleteCaseItem();
      ii.item = currentCaseItem;
      ii.jugdeN = currentJudgeN;
      ii.addrN = currentAddrN;
      items.add(ii);
      currentCaseItem = null;
    }

    return true; //
  }

  /**
   * Receive notification of the beginning of a JSON object entry.
   *
   * @param key - Key of a JSON object entry.
   *
   * @return false if the handler wants to stop parsing after return.
   * @throws ParseException
   *
   * @see #endObjectEntry
   */
  public boolean startObjectEntry(String key) throws ParseException, IOException {
    logger.debug("started entry: " + key);

    if (key.equals("cases")) {
      stage = ProcessingStage.READING_CASE_ITEMS;
    }

    if (key.equals("date")) {
      stage = ProcessingStage.RCI_DATE;
    }

    if (key.equals("forma")) {
      stage = ProcessingStage.RCI_TYPE;
    }

    if (key.equals("number")) {
      stage = ProcessingStage.RCI_NUMBER;
    }

    if (key.equals("involved")) {
      stage = ProcessingStage.RCI_INVOLVED;
    }

    if (key.equals("description")) {
      stage = ProcessingStage.RCI_DESC;
    }

    if (key.equals("judge")) {
      stage = ProcessingStage.RCI_JUDGE_N;
    }

    if (key.equals("add_address")) {
      stage = ProcessingStage.RCI_ADDR_N;
    }

    // this is a start of "judges" array
    if (key.equals("judges")) {
      stage = ProcessingStage.READING_JUDGES;
    }

    if (key.equals("add_addresses")) {
      stage = ProcessingStage.READING_ADDRESSES;
    }

    return true;
  }

  /**
   * Receive notification of the end of the value of previous object entry.
   *
   * @return false if the handler wants to stop parsing after return.
   * @throws ParseException
   *
   * @see #startObjectEntry
   */
  public boolean endObjectEntry() throws ParseException, IOException {
    logger.debug("finished entry");
    return true;
  }

  /**
   * Receive notification of the beginning of a JSON array.
   *
   * @return false if the handler wants to stop parsing after return.
   * @throws ParseException
   *
   * @see #endArray
   */
  public boolean startArray() throws ParseException, IOException {
    logger.debug("started array");
    return true;
  }

  /**
   * Receive notification of the end of a JSON array.
   *
   * @return false if the handler wants to stop parsing after return.
   * @throws ParseException
   *
   * @see #startArray
   */
  public boolean endArray() throws ParseException, IOException {
    logger.debug("finished array");
    return true;
  }

  /**
   * Receive notification of the JSON primitive values:
   * 	java.lang.String,
   * 	java.lang.Number,
   * 	java.lang.Boolean
   * 	null
   *
   * @param value - Instance of the following:
   * 			java.lang.String,
   * 			java.lang.Number,
   * 			java.lang.Boolean
   * 			null
   *
   * @return false if the handler wants to stop parsing after return.
   * @throws ParseException
   */
  public boolean primitive(Object value) throws ParseException, IOException {
    String stringValue = "";
    int intValue = 0;
    String className = value.getClass().getSimpleName();
    if (className.equals("String")) {
      stringValue = (String)value;
      logger.debug("got primitive value: " + className + " as \"" + stringValue + "\"");
    }
    else if (className.equals("Long")) {
      intValue = ((Long)value).intValue();
      logger.debug("got primitive value: " + className + " as \"" + intValue + "\"");
    }
    else {
      logger.debug("got primitive value of " + className + "(unknown, unexpected)");
    }


    switch (stage) {
      case RCI_DATE:
        currentCaseItem.setDate(stringValue);
        break;
      case RCI_TYPE:
        currentCaseItem.setType(stringValue);
        break;
      case RCI_NUMBER:
        currentCaseItem.setNumber(stringValue);
        break;
      case RCI_INVOLVED:
        currentCaseItem.setNumber(stringValue);
        break;
      case RCI_DESC:
        currentCaseItem.setDescription(stringValue);
        break;
      case RCI_JUDGE_N:
        currentJudgeN = intValue;
        break;
      case RCI_ADDR_N:
        currentAddrN = intValue;
        break;

      case READING_JUDGES:
        judges.add(stringValue);
        break;

      case READING_ADDRESSES:
        addresses.add(stringValue);
        break;
    }

    return true;
  }

  public int numberOfCases() {
    if (items == null) {
      return 0;
    }
    //else
    return items.size();
  }

  public ArrayList<CaseItem> getItems () {
    ArrayList<CaseItem> result = new ArrayList<CaseItem>();

    for(IncompleteCaseItem ici:items) {
      CaseItem ci = ici.item;
      if (ici.jugdeN<judges.size()) {
        ci.setJudge( judges.get(ici.jugdeN) );
      }
      if (ici.addrN<addresses.size()) {
        ci.setAddress( addresses.get(ici.addrN));
      }

      result.add(ci);
    }

    return result;
  }

}

