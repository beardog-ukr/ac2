package friedBox.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class CourtIdExtractorTest {

  @Test
  public void testExtract01() {
    String s1 = CourtIdExtractor.extract("dfff");
    assertEquals("", s1);
  }

  @Test
  public void testExtract02() {
    String s1 = CourtIdExtractor.extract("d2015112_234340.bg.if.json");
    assertEquals("bg.if", s1);
  }

  @Test
  public void testExtract03() {
    String s1 = CourtIdExtractor.extract("d2015112_234340.bpg.ig.json");
    assertEquals("bpg.ig", s1);
  }

  @Test
  public void testExtract04() {
    String s1 = CourtIdExtractor.extract("d20161122_234340.sxs.ed.json.goo");
    assertEquals("sxs.ed", s1);
  }

  @Test
  public void testExtract05() {
    String s1 = CourtIdExtractor.extract("s201512_234340.bg.ig.json");
    assertEquals("bg.ig", s1);
  }
}


