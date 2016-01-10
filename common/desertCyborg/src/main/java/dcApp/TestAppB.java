package dcApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;
//
import desertCyborg.CaseItem;
import desertCyborg.CourtsArchiveReader;

import java.util.ArrayList;

/**
 * Test application class.
 *
 * Uses hardcoded test string to test desertCyborg package classes.
 *
 * Created on 10.01.16.
 */
public class TestAppB {

  private static final Logger logger = LoggerFactory.getLogger("dcApp.main");

  public void doIt(String[] args) {
    CottonFalcon cf = new CottonFalcon();
    cf.addShortOption("h", false);
    cf.addLongOption("help", false);

    cf.addShortOption("a", false);
    cf.addShortOption("b", false);

    cf.addLongOption("file", true);
    boolean cfpr = cf.process(args);
    if (!cfpr) {
      logger.error("Error processing cmdline: " + cf.getErrorMessage());
      return;
    }

    if (cf.gotShortOption("h")) {
      logger.debug("need to show help message"); //
    }
    else {
      logger.debug("no help message needed"); //
    }

    if (!cf.gotLongOption("file")) {
      logger.debug("no file specified"); //
      return;
    }

    String fn = cf.getLongOptionParameter("file");
    CourtsArchiveReader car = new CourtsArchiveReader();
    boolean readResult = car.readFile(fn); //no file reading here
    if (!readResult) {
      logger.error("failed to read file : " + car.getErrorMessage());
      return;
    }

    ArrayList<CaseItem> items = car.getItems();
    String dmsg = String.format("Got %d items", items.size());
    logger.debug(dmsg);

    //logger.debug("application finished."); //
  }
}
