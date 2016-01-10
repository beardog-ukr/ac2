package friedBox.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import friedBox.component.SqliteUpdater;
//
// import cottonfalcon.CottonFalcon;
//
// import desertCyborg.CourtsArchiveReader;
//
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;


public class MainApp {

  private static final Logger logger = LoggerFactory.getLogger("friedBox.app");

  public static void main(String[] args) {
    logger.debug("application started"); //

    // CottonFalcon cf = new CottonFalcon();
    // cf.addShortOption("h", false);
    // boolean cfpr = cf.process(args);
    // if (cf.gotShortOption("h")) {
    //   logger.debug("need to show help message"); //
    // }
    // else {
    //   logger.debug("no help message needed"); //
    // }
    SqliteUpdater sup = new SqliteUpdater();
    sup.say();



    logger.debug("application finished."); //
  }
}
