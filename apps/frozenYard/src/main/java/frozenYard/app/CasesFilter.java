package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import java.util.ArrayList;
import java.util.LinkedList;
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
public class CasesFilter {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  String dbFileName;

  public CasesFilter(String dbFileName) {
    this.dbFileName = dbFileName;
  }

  String errorMessage = "";

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   *   Creates a list of items that were not mentioned in the db before.
   *   For each item from allItems list checks if it exists in the db; if not,
   * adds it to filteredItems list.
   * @param allItems
   * @param filteredItems
   * @return true on success
   */
  public boolean filterItems(ArrayList<CaseItem> allItems,
                             LinkedList<CaseItem> filteredItems) {
    boolean result = true;

    SqlJetDb database ;
    ISqlJetTable casesTable ;

    File dbFile = new File(dbFileName) ;
    try {
      database = SqlJetDb.open (dbFile, true);
      casesTable = database.getTable("cases");
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

    filteredItems.clear();

//    Set<String> inms = null;
//    try {
//      inms = casesTable.getIndexesNames();
//    }
//    catch ( SqlJetException e ) {
//      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
//      result=false;
//    }
//
//    for (String inm:inms) {
//      logger.debug("Got index " + inm);
//    }

    for (CaseItem caseItem:allItems) {
      logger.debug("Testing if we need to add \"{}\"", caseItem.getNumber());
      boolean found = false;//
      ISqlJetCursor cursor = null;
      try {
        cursor = casesTable.lookup("number_idx", caseItem.getNumber());
        if (!cursor.eof()) {
          do {
            String dsch = FilenameProcessing.dateToDbFormat(caseItem.getDate());

            found = dsch.equals(cursor.getString("date_scheduled")) &&
                    caseItem.getJudge().equals( cursor.getString("judge") ) &&
                    caseItem.getNumber().equals(cursor.getString("number")) &&
                    caseItem.getInvolved().equals(cursor.getString("involved")) &&
                    caseItem.getDescription().equals(cursor.getString("description")) &&
                    caseItem.getType().equals(cursor.getString("type"));

            //TODO: check court id here too

            if (found) {
              logger.debug("Record for {} already exists in db", caseItem.getNumber());
              break;
            }
          } while (cursor.next());
        }
//        else {
//          logger.debug("Definitely no {} in db", caseItem.getNumber());
//        }
      } catch (SqlJetException e) {
        errorMessage = e.getClass().getName() + ": " + e.getMessage();
        result = false;
        break;
      } finally {
        try {
          if (cursor!=null) {
            cursor.close();
          }
        } catch (SqlJetException e) {
          errorMessage = e.getClass().getName() + ": " + e.getMessage();
          result = false;
          break;
        }
      }

      if (!found) {
        filteredItems.add(caseItem);
      }
    }

    logger.debug("Finally need to add {} items to db", filteredItems.size());

    try {
      database.commit();
    }
    catch (SqlJetException e) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage();
      result = false;
    }

    return result;
  }

}