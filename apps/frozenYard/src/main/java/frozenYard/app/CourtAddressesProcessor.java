package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
//
import java.io.File;
//
import desertCyborg.CaseItem;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 *
 */
public class CourtAddressesProcessor {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  String dbFileName;

  public CourtAddressesProcessor(String jsonFilename, String dbFileName) {
    this.dbFileName = dbFileName;
    setCourtId(FilenameProcessing.extractCourtId(jsonFilename));
  }

  String courtId;
  void setCourtId(String id) {
    courtId = id;
  }

  String errorMessage = "";
  public String getErrorMessage() {
    return errorMessage;
  }

  Map<String, Long> addr2rowid = null;

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

  /**
   *   Creates unique list of court addresses mentioned in case items.
   * @param items
   * @return
   */
  LinkedList<String> loadUsedCourts(ArrayList<CaseItem> items){
    LinkedList<String> usedAddresses= new LinkedList<String>() ;

    for (CaseItem item:items ) {
      boolean found = false;
      for (String uaddr: usedAddresses) {
        if (uaddr.equals(item.getAddress())) {
          found = true;
          break;
        }
      }
      if (!found) {
        usedAddresses.add(item.getAddress()) ;
      }
    }

    logger.debug("There is {} different addresses in this file", usedAddresses.size()) ;
    return usedAddresses;
  }

  /**
   *   Checks if address exists in "court_addresses" db table.
   * @param addresses List of court addresses used in case items data.
   * @param unlistedAddresses List of addresses that were not mentioned in db (actual result)
   * @return true on success, false otherwise
   */
  boolean checkForUnlisted(LinkedList<String> addresses,
                           LinkedList<String> unlistedAddresses) {
    boolean result = true;

    SqlJetDb database ;
    ISqlJetTable caTable ;

    File dbFile = new File(dbFileName) ;
    try {
      database = SqlJetDb.open (dbFile, true);
      caTable = database.getTable("court_addresses");
    } catch ( SqlJetException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      return false;
    }

    try {
      database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
    }
    catch ( SqlJetException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      result=false;
    }

    TreeMap<String, Boolean> a2m = new TreeMap<>();
    for (String a: addresses) {
      Boolean b = new Boolean(false);
      a2m.put(a, b);
    }

    if (result) {
      try {
        ISqlJetCursor cursor = caTable.order(caTable.getPrimaryKeyIndexName());
        if (!cursor.eof()) {
          do {
            String currentCourtId = cursor.getString("court_id");
            if (courtId.equals(currentCourtId)) {
              String currentAddr = cursor.getString("court_address");
              for (Map.Entry<String,Boolean> me : a2m.entrySet()) {
                if (currentAddr.equals(me.getKey())) {
                  me.setValue(new Boolean(true));
                  logger.debug("Address {} exists in db", currentAddr);
                }
              }
            }
          } while (cursor.next());
        } else {
          errorMessage = "Failed to read court addresses table (first time)";
          result = false;
        }
      } catch (SqlJetException e) {
        errorMessage = e.getClass().getName() + ": " + e.getMessage();
        result = false;
      }
    }

    try {
      database.commit();
    }
    catch (SqlJetException e) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage();
      result = false;
    }

    unlistedAddresses.clear();
    for (Map.Entry<String, Boolean> me: a2m.entrySet()) {
      if (!me.getValue().booleanValue()) {
        unlistedAddresses.add(me.getKey());
        logger.debug("Address {} added to unlisted", me.getKey());
      }
    }

    return result;
  }

  /**
   *   Adds strings from newAddresses list to "court_addresses" table of the db.
   * @param newAddresses List of addresses that were not mentioned in db (actual result)
   * @return true on success, false otherwise
   */
  boolean addUnlistedToDB(LinkedList<String> newAddresses) {
    boolean result = true;

    SqlJetDb database ;
    ISqlJetTable caTable ;

    File dbFile = new File(dbFileName) ;
    try {
      database = SqlJetDb.open (dbFile, true);
      caTable = database.getTable("court_addresses");
    } catch ( SqlJetException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      return false;
    }

    try {
      database.beginTransaction(SqlJetTransactionMode.WRITE);
    }
    catch ( SqlJetException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      result=false;
    }

    if (!result) {
      return false;
    }

    try {
      for (String na: newAddresses) {
        caTable.insert(null, courtId, na);// null goes instead of autoincrement integer field
        logger.debug("Adding {} to db", na);
      }
    } catch (SqlJetException e) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage();
      result = false;
    }

    try {
      database.commit();
    }
    catch (SqlJetException e) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage();
      result = false;
    }

    return result;
  }

  boolean prepareRowidInfo(LinkedList<String> addresses) {
    SqlJetDb database ;
    ISqlJetTable caTable ;

    File dbFile = new File(dbFileName) ;
    try {
      database = SqlJetDb.open (dbFile, true);
      caTable = database.getTable("court_addresses");
    } catch ( SqlJetException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      return false;
    }

    addr2rowid = new HashMap<>();

    boolean result = true;
    try {
      database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
    }
    catch ( SqlJetException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      result=false;
    }

    if (result) {
      try {
        ISqlJetCursor cursor = caTable.order(caTable.getPrimaryKeyIndexName());
        if (!cursor.eof()) {
          do {
            String currentCourtId = cursor.getString("court_id");
            if (courtId.equals(currentCourtId)) {
              String currentAddr = cursor.getString("court_address");

              boolean found = true;
              for (String uaddr : addresses) {
                if (uaddr.equals(currentAddr)) {
                  found = true;
                  break;
                }
              }
              if (found) {
                Long lv = new Long(cursor.getRowId());
                addr2rowid.put(currentAddr, lv);
                logger.debug("Adding to map {} as {} ", currentAddr, lv);
              }
            }
          } while (cursor.next());
        } else {
          errorMessage = "Failed to read court addresses table";
          result = false;
        }
      } catch (SqlJetException e) {
        errorMessage = e.getClass().getName() + ": " + e.getMessage();
        result = false;
      }
    }

    try {
      database.commit();
    }
    catch (SqlJetException e) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage();
      result = false;
    }

    if (addresses.size() != addr2rowid.size()) {
      logger.warn("Some court addresses were not processed {} / {}"
                  , addresses.size(), addr2rowid.size());
      for (Map.Entry me: addr2rowid.entrySet()) {
        logger.debug("Map: {} -> {}", me.getKey(), me.getValue());
      }
    }


    logger.debug("prepareRowidInfo finished ({})", result);
    return result;
  }

  public boolean processItems(ArrayList<CaseItem> items) {
    boolean result = true;
    //
    LinkedList<String> usedAddresses = loadUsedCourts(items);

    LinkedList<String> unlisted = new LinkedList<>();
    if (!checkForUnlisted(usedAddresses, unlisted)) {
      return false;
    }

    if (unlisted.size()!=0) {
      if (!addUnlistedToDB(unlisted)) {
        return false;
      }
    }

    // now reload list of courts (only for used)
    if (!prepareRowidInfo(usedAddresses)) {
      return false;
    }



    return result;
  }
}