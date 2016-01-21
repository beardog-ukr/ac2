package gleeFarm.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//

public class MainApp {

  private static final Logger logger = LoggerFactory.getLogger("gleeFarm.app");

  public static void main(String[] args) {
    logger.debug("application started"); //

    GleeFarmApp gfa = new GleeFarmApp();
    gfa.doIt(args);

    logger.debug("application finished."); //
  }
}
