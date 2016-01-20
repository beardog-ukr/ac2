package frozenYard.app;

//import desertCyborg.CaseItem;
//
import java.io.File ;
//
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class FilesTableProcessor {

  private static final Logger logger = LoggerFactory.getLogger("frozenYard.app");

  static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss" ;

  String errorMessage = "";
  public String getErrorMessage() {
    return errorMessage;
  }

  String courtId = "und.ef";
  public void setCourtId (String ci){
    courtId = ci;
  }

  String dbFileName = ""; // full name of the database file

  public FilesTableProcessor(String filename) {
    dbFileName = filename;
    setCourtId(FilenameProcessing.extractCourtId(filename));
  }

  class FileInfo {
    public long rowid;
    public String filename;
    public String datep;
  }
  LinkedList<FileInfo> filesInfo;

  public boolean readFilesInfo() {
    SqlJetDb database = null;
    ISqlJetTable filesTable = null;

    File dbFile = new File(dbFileName) ;
    try {
      database = SqlJetDb.open (dbFile, true);
      filesTable = database.getTable("files");
    } catch ( SqlJetException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      return false;
    }

    filesInfo = new LinkedList<>();

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
        ISqlJetCursor cursor = filesTable.order(filesTable.getPrimaryKeyIndexName());
        if (!cursor.eof()) {
          do {
            FileInfo finfo = new FileInfo();
            finfo.rowid = cursor.getRowId();
            finfo.filename = cursor.getString("filename");
            finfo.datep = cursor.getString("date_processed");

            String dbgs = String.format("Got line: %d : %s | %s", finfo.rowid,
                    finfo.filename, finfo.datep);
            logger.debug(dbgs);

            filesInfo.add(finfo);
          } while (cursor.next());
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

    if (filesInfo!=null) {
      logger.debug("Got information about {} processed files", filesInfo.size());
    }
    else {
      logger.debug("Failed to get anything");
    }

    //finally
    return result;
  }

  public boolean wasProcessed(String filename) {
    if (filesInfo==null) {
      logger.error("There is no list of files");
      return false;
    }

    String fn = FilenameProcessing.extractFilename(filename);
    for (FileInfo finf: filesInfo) {
      if (finf.filename.equals(fn)) {
        return true;
      }
    }

    return false;
  }
}

