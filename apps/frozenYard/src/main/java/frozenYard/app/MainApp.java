package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//

public class MainApp {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  public static void main(String[] args) {
//    logger.debug("application started"); //

    FrozenYardApp fya = new FrozenYardApp();
    if (!fya.doIt(args)) {
      logger.error(fya.getErrorMessage());
    }

//    logger.debug("application finished."); //
  }
}
