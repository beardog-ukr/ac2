package desertCyborg;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
//
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.ContentHandler;
//
import java.util.ArrayList;
//
// import org.slf4j.LoggerFactory;
// import ch.qos.logback.classic.Level;
// import ch.qos.logback.classic.Logger;

public class CourtsArchiveParserTest {

  // ==========================================================================



  // ==========================================================================

  @Test
  public void testBasic() {

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



    JSONParser parser = new JSONParser();
    CourtsArchiveParser cap = new CourtsArchiveParser();
    ContentHandler ch = cap;
    try {
      parser.parse(td, ch);
    }
    catch(ParseException pe){
       pe.printStackTrace();
    }

    assertEquals(cap.numberOfCases(), 2);
    ArrayList<CaseItem> items = cap.getItems();
    assertEquals(items.size(), 2);
    assertEquals(items.get(0).getNumber(), "338/1797/15-а");
    assertEquals(items.get(1).getNumber(), "338/1670/15-ц");
    assertEquals(items.get(1).getJudge(), "Круль І.В.");

  }

  // ==========================================================================

  // ==========================================================================
}
