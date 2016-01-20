package frozenYard.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import java.util.Date;
import java.util.LinkedList;
//
import java.io.File;
//
import desertCyborg.CaseItem;
//
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
//import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 *
 */
public class CasesAdder {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  String dbFileName;

  public CasesAdder(String dbFileName) {
    this.dbFileName = dbFileName;
  }

  String errorMessage = "";

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   *   Tries to add items to databaseCreates a list of items that were not mentioned in the db before.
   * Note it also updates "files" table during the same transaction
   *
   * @param items
   * @param courtRowIdKeeper
   * @return true on success
   */
  public boolean addItemsToDb(LinkedList<CaseItem> items,
                              String filename,
                              CourtRowIdKeeper courtRowIdKeeper) {
    boolean result = true;

    SqlJetDb database;
    ISqlJetTable casesTable;
    ISqlJetTable filesTable;

    File dbFile = new File(dbFileName);
    try {
      database = SqlJetDb.open(dbFile, true);
      casesTable = database.getTable("cases");
      filesTable = database.getTable("files");
    } catch (SqlJetException e) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage();
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

    long fileId =0;
    String basicFilename = FilenameProcessing.extractFilename(filename);
    String dateProc = FilenameProcessing.dateToDbFormat(new Date());
    try {
      // null goes instead of autoincrement integer field
      fileId = filesTable.insert(null, basicFilename, dateProc);
    } catch (SqlJetException e) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage();
      result = false;
    }

    if (!result) {
      return false;
    }

    for(CaseItem item: items) {
      try {
        // null goes instead of autoincrement integer field
        casesTable.insert(FilenameProcessing.dateToDbFormat(item.getDate()) ,
                          item.getJudge(),
                          item.getNumber(),
                          item.getInvolved(),
                          item.getDescription(),
                          item.getType(),
                          courtRowIdKeeper.getRowid(item.getAddress()),
                          fileId
                         );
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

    return result;
  }
}