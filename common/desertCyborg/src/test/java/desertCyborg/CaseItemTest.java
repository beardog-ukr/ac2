package desertCyborg;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
//
import java.util.Date;

public class CaseItemTest {

  // ==========================================================================

  @Test
  public void testBasic() {

    CaseItem item = new CaseItem();
    item.setDate("28.12.2015 08:30");

    Date dt = item.getDate();

    assertEquals(dt.getDate(), 28);
    assertEquals(dt.getYear(), 115);
    assertEquals(dt.getMonth(), 11);
    assertEquals(dt.getHours(), 8);
    assertEquals(dt.getMinutes(), 30);
  }

  // ==========================================================================

}