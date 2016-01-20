package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import java.util.Map;
import java.util.TreeMap;

/**
 *
 */
public class CourtRowIdKeeper {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  protected Map<String, Long> addr2rowid = new TreeMap<>();

  public void addPair(String str, Long rowid) {
    addr2rowid.put(str, rowid);
  }

  public int size() {
    return addr2rowid.size();
  }

  /**
   *   Returns row id for given court address. This id should be used in "cases"
   * table.
   * @param addr Court Address
   * @return
   */
  public long getRowid(String addr) {
    if (addr2rowid==null) {
      return -1;
    }

    Long l = addr2rowid.get(addr) ;
    long result = -1;
    if (l!=null) {
      result = l.longValue();
    }
    else {
      logger.error("Unknown court {}", addr);
    }

    return result;
  }

}