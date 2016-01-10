package dcApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import cottonfalcon.CottonFalcon;
//
import desertCyborg.CourtsArchiveReader;

/**
 * Test application class.
 *
 * Uses hardcoded test string to test desertCyborg package classes.
 *
 * Created on 10.01.16.
 */
public class TestAppA {

  private static final Logger logger = LoggerFactory.getLogger("dcApp.main");

  public void doIt(String[] args) {
    CottonFalcon cf = new CottonFalcon();
    cf.addShortOption("h", false);
    boolean cfpr = cf.process(args);
    if (cf.gotShortOption("h")) {
      logger.debug("need to show help message"); //
    }
    else {
      logger.debug("no help message needed"); //
    }

    CourtsArchiveReader car = new CourtsArchiveReader();
    //car.readFile("dd"); //no file reading here


    String td = "";
    td += "{\n";
    td += "\"cases\": [\n";
    td += "  {\n";
    td += "   \"date\": \"28.12.2015 08:30\",\n";
    td += "   \"forma\": \"АС\",\n";
    td += "   \"number\": \"338/1797/15-а\",\n";
    td += "   \"involved\": \"Позивач: Сухоручко Василь Іванович, відповідач: Управління  ПФУ\",\n";
    td += "   \"description\": \"про перерахунок пенсії\",\n";
    td += "   \"judge\": 0,\n";
    td += "   \"add_address\": 0\n";
    td += "  },\n";
    td += "  {\n";
    td += "   \"date\": \"28.12.2015 09:00\",\n";
    td += "   \"forma\": \"ЦС\",\n";
    td += "   \"number\": \"338/1670/15-ц\",\n";
    td += "   \"involved\": \"Позивач: Годованець Петро Богданович, відповідач: Годованець Світлана Миколаївна\",\n";
    td += "   \"description\": \"про розірвання шлюбу\",\n";
    td += "   \"judge\": 0,\n";
    td += "   \"add_address\": 0\n";
    td += "  } \n";
    td += "  ], \n";
    td += "\"judges\": [";
    td += "\"Круль І.В.\",";
    td += "\"Битківський Л.М.\",";
    td += "  \"Гутич П.Ф.\"";
    td += "],";
    td += "\"add_addresses\": [";
    td += "\"77701, Івано-Франківська, Богородчанський, смт. Богородчани, вул. Шевченка, 68\"";
    td += "]";
    td += "} ";

    car.processJSON(td);


    //logger.debug("application finished."); //
  }
}
