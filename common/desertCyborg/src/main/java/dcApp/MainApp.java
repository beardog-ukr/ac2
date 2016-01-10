package dcApp;

import cottonfalcon.CottonFalcon;
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//

public class MainApp {

  private static final Logger logger = LoggerFactory.getLogger("dcApp.main");

  public static void main(String[] args) {
    logger.debug("application started"); //

    CottonFalcon cf = new CottonFalcon();
    cf.addShortOption("a", false);
    cf.addShortOption("b", false);
    boolean cfpr = cf.process(args);
    if (cf.gotShortOption("a")) {
      logger.debug("Working with type A"); //
      TestAppA taa = new TestAppA();
      taa.doIt(args);
    }

    if (cf.gotShortOption("b")) {
      logger.debug("Working with type B"); //
      TestAppB tab = new TestAppB();
      tab.doIt(args);
    }

    logger.debug("application finished."); //
  }
}
