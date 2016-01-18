package frozenYard.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
import java.nio.file.Paths;
import java.nio.file.Path;

class FilenameProcessing {

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
  public static String extractCourtId(String filename) {
    Pattern pt = Pattern.compile("\\w201\\d+_\\d+\\.(\\w?\\w\\w\\.\\w\\w)\\..*");

    Matcher matcher = pt.matcher(filename);
    while (matcher.find()) {
      return matcher.group(1);
    }
    //else
    return "";
  }

  /**
   * Extracts file name from long filename string.
   *
   *   For example, it will get 'd2015110_234340.bg.if.json' from full string
   * like "./sdfs/fff/d2015110_234340.bg.if.json"
   *
   * @param filename
   * @return
   */
  public static String extractFilename(String filename) {
    Path path = Paths.get(filename) ;
    return path.getFileName().toString();
  }
}
