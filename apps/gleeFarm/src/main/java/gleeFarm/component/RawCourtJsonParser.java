package gleeFarm.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;
//
import java.io.IOException;
//
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

class RawCourtJsonParser implements ContentHandler{

  private static final Logger logger = LoggerFactory.getLogger("gleeFarm.component");

  enum ProcessingStage{
    START
    , READING_ITEMS
    , RCI_DATE // like "Reading Case Item : Date"
    , RCI_TYPE
    , RCI_NUMBER
    , RCI_INVOLVED
    , RCI_DESC
    , RCI_JUDGE
    , RCI_ADDR
    , ERROR
    , FINISHED
  }

  ProcessingStage stage;

  int judgesCounter =0;
  Map<String, Integer> judges;
  int addressesCounter =0;
  Map<String, Integer> addresses;

  class CaseItem {
    String date;
    String type;
    String number;
    String involved;
    String description;
    int jugdeN;
    int addrN;
  }

  List<CaseItem> items;

  CaseItem currentCaseItem;

  public void startJSON() throws ParseException, IOException {
    stage = ProcessingStage.START;
    items = new LinkedList<>();
    judges = new HashMap<>();
    judgesCounter =0;
    addresses = new HashMap<>() ;
    addressesCounter =0;

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

    if (stage==ProcessingStage.READING_ITEMS) {
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
            stage == ProcessingStage.RCI_JUDGE   ||
            stage == ProcessingStage.RCI_ADDR) {
      stage = ProcessingStage.READING_ITEMS;

      items.add(currentCaseItem);
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
      stage = ProcessingStage.RCI_JUDGE;
    }

    if (key.equals("add_address")) {
      stage = ProcessingStage.RCI_ADDR;
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
    //The only array in this data is array of items
    stage = ProcessingStage.READING_ITEMS;
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
    String className = value.getClass().getSimpleName();
    if (className.equals("String")) {
      stringValue = (String)value;
      logger.debug("got primitive value: " + className + " as \"" + stringValue + "\"");
    }
    else {
      logger.debug("got primitive value of " + className + "(unknown, unexpected)");
    }


    switch (stage) {
      case RCI_DATE:
        currentCaseItem.date = stringValue;
        break;
      case RCI_TYPE:
        currentCaseItem.type = transformType(stringValue);
        break;
      case RCI_NUMBER:
        currentCaseItem.number = stringValue;
        break;
      case RCI_INVOLVED:
        currentCaseItem.involved = stringValue;
        break;
      case RCI_DESC:
        currentCaseItem.description = stringValue;
        break;
      case RCI_JUDGE:
        currentCaseItem.jugdeN = transformJudgeName(stringValue);
        break;
      case RCI_ADDR:
        currentCaseItem.addrN = transformAddress(stringValue);
        break;
    }

    return true;
  }

  public String getTransformedJson() {
    JSONArray casesArr = new JSONArray();

    for (CaseItem item:items) {
      JSONObject jo = new JSONObject();
      jo.put("date",item.date);
      jo.put("forma", item.type);
      jo.put("number", item.number);
      jo.put("involved", item.involved);
      jo.put("description", item.description);
      jo.put("judge", new Integer(item.jugdeN));
      jo.put("add_address", new Integer(item.addrN));
      /*
      Example is
         "date": "28.12.2015 08:30",
   "forma": "АС",
   "number": "338/1797/15-а",
   "involved": "Позивач: Сухоручко Василь Іванович, відповідач: Управління  ПФУ",
   "description": "про перерахунок пенсії",
   "judge": 0,
   "add_address": 0
       */
      casesArr.add(jo);
    }


    String[] jarr = new String[judges.size()];
    Set<String> jset = judges.keySet();
    for (String jstr: jset) {
      jarr[judges.get(jstr).intValue()] = jstr;
    }
    JSONArray judgesArr = new JSONArray();
    for(int i=0; i<judges.size(); i++) {
      judgesArr.add(jarr[i]);
    }

    String[] addrarr = new String[addresses.size()];
    Set<String> addrset = addresses.keySet();
    for (String astr: addrset) {
      addrarr[addresses.get(astr).intValue()] = astr;
    }
    JSONArray addressesArr = new JSONArray();
    for(int i=0; i<addresses.size(); i++) {
      addressesArr.add(addrarr[i]);
    }

    JSONObject rootObj = new JSONObject();
    rootObj.put("cases", casesArr);
    rootObj.put("judges", judgesArr);
    rootObj.put("add_addresses", addressesArr);

    return JSONValue.toJSONString(rootObj);
    //return rootObj.toString();
  }

//  protected String transformDate(String date) {
//    //usual date is like "27.10.2015 08:30"
//    Pattern pattern = Pattern.compile("(\\d\\d)\\.(\\d\\d)\\.(201\\d) (\\d\\d):(\\d\\d)");
//    Matcher matcher = pattern.matcher(date);
//
//    String result = "";
//    if (matcher.find()) {
//      String yearStr = matcher.group(3);
//      String monthStr = matcher.group(2);
//      String dayStr = matcher.group(1);
//      String hourStr = matcher.group(4);
//      String minuteStr = matcher.group(5);
//
//      result = String.format("%s-%s-%s %s:%s:00",yearStr, monthStr, dayStr, hourStr, minuteStr);
//    }
//    //resulting format is "yyyy-MM-dd HH:mm:ss"
//    return result;
//  }

  protected String transformType(String type) {
    String result = type;

    if (type.equals("Цивільні справи")) {
      result = "ЦС";
    }
    else if (type.equals("Кримінальні справи")) {
      result = "КС";
    }
    else if (type.equals("Адміністративні справи")) {
      result = "АС";
    }
    else if (type.equals("Справи про адмінправопорушення")) {
      result = "СпАП";
    }

    return result;
  }

  protected int transformJudgeName(String judge) {
    judge = judge.trim();
    Integer rint = judges.get(judge) ;
    int result;
    if (rint==null) {
      judges.put(judge, new Integer(judgesCounter));
      result = judgesCounter;
      judgesCounter++ ;
    }
    else {
      result = rint.intValue();
    }
    return result;
  }

  protected int transformAddress(String address) {
    Integer rint = addresses.get(address) ;
    int result;
    if (rint==null) {
      addresses.put(address, new Integer(addressesCounter));
      result = addressesCounter;
      addressesCounter++ ;
    }
    else {
      result = rint.intValue();
    }
    return result;
  }
}

