package frozenYard.app;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CourtIdExtractor {

  /**
   * Extracts court if from archive filename.
   *
   *   For example, file name can be like 'd2015110_234340.bg.if.json'
   * and result will be 'bg.if'.
   *   Court id consists of two parts, one is three or two letters and another
   * is exactly two letters.
   *
   * @param filename
   * @return
   */
  public static String extract(String filename) {
    Pattern pt = Pattern.compile("\\w201\\d+_\\d+\\.(\\w?\\w\\w\\.\\w\\w)\\..*");

    Matcher matcher = pt.matcher(filename);
    while (matcher.find()) {
      return matcher.group(1);
    }
    //else
    return "";
  }
}
