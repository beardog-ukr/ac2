package friedBox.component;

import org.junit.Test;
import static org.junit.Assert.*;

public class SqliteUpdaterTest {

  @Test
  public void testErrorMessageNoError() {
    SqliteUpdater su = new SqliteUpdater();
    String result = su.getErrorMessage();
    assertEquals("", result);
  }
}
