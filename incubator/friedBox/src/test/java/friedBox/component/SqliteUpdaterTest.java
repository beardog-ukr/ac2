package friedBox.component;

import org.junit.Test;
import static org.junit.Assert.*;

public class SqliteUpdaterTest {

  @Test
  public void testConcatenate() {
    SqliteUpdater su = new SqliteUpdater();
    String result = su.concatenate("one", "two");
    assertEquals("onetwo", result);
  }
}
