package friedBox.component;

import desertCyborg.CaseItem;
//
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
//
import java.io.File ;
//
import java.text.SimpleDateFormat;
import java.util.Date;
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import friedBox.app.DbUpdater;

public class NativeUpdater implements DbUpdater{

  private static final Logger logger = LoggerFactory.getLogger("friedBox.app");

  String errorMessage = "";
  public String getErrorMessage() {
    return errorMessage;
  }

  String courtId = "und.ef";
  public void setCourtId (String ci){
    courtId = ci;
  }

  static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss" ;

  SQLiteConnection connection = null;
  SQLiteStatement insertStatement = null;
  SQLiteStatement checkStatement = null;

  File prepareFile(String dbFileName) {
    File fl = new File(dbFileName);
    if (!fl.exists()) {
      errorMessage = String.format("File \"%s\" does not exist", dbFileName);
      return null;
    }
    else  {
      logger.debug("File {} exists", dbFileName);
    }

    if (!fl.canRead()) {
      errorMessage = String.format("Can\'t read file \"%s\".", dbFileName);
      return null;
    }

    if (!fl.canWrite()) {
      errorMessage = String.format("Can\'t write to file \"%s\".", dbFileName);
      return null;
    }

    //finally, normally
    return fl;
  }


  public boolean connectToDB(String dbFileName) {
    boolean result = true;

    File dbFile = prepareFile(dbFileName) ;
    if (dbFile==null) {
      return false;
    }
    connection = new SQLiteConnection(dbFile);

    try {
      connection.open(false) ;
    } catch ( SQLiteException e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      result = false ;
    }

    logger.debug("Connected to db");

    Date tdt = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
    String insertDateStr = dateFormat.format(tdt);

    if (result) {
      String qs = "";
      qs += "INSERT INTO cases  (date_scheduled, date_inserted,  judge, " ;
      qs += "number, involved, description, type, court_id, court_address)";
      qs += "VALUES ( ?, " ;// 1 date scheduled;
      qs += "\"" + insertDateStr + "\"," ;  //  date ins
      qs += "?,";   // 2 judge
      qs += "?,";   // 3 number
      qs += "?,";   // 4 involved
      qs += "?,";   // 5 desc
      qs += "?,";   // 6 type
      qs += "\"" + courtId + "\","; //
      qs += "?)";   // 7 address

      String sqs = "";
      sqs += "SELECT count(*) FROM cases where ";
      sqs += "date_scheduled = ? AND judge=? AND number=? " ;
      sqs += "AND involved=? AND description=? AND type=? ";
      sqs += "AND court_id=\"" + courtId + "\"";
      sqs += "AND court_address=?";

      try {
        insertStatement = connection.prepare(qs);
        checkStatement = connection.prepare(sqs);

        connection.exec("BEGIN TRANSACTION; ");
      } catch ( Exception e ) {
        errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
        result = false ;
      }
    }

    logger.debug("Prepared to work with db");
    return result;
  }

  public boolean addOneItem(CaseItem item) {
    boolean result = true;

    int checkresult =0;

    try {
      checkStatement.reset();

      SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
      String dateSch = dateFormat.format(item.getDate());
      checkStatement.bind(1, dateSch);

      checkStatement.bind(2, item.getJudge());
      checkStatement.bind(3, item.getNumber());
      checkStatement.bind(4, item.getInvolved());
      checkStatement.bind(5, item.getDescription());
      checkStatement.bind(6, item.getType());
      checkStatement.bind(7, item.getAddress());


      if (checkStatement.step()) {
        checkresult = checkStatement.columnInt(0);
      }
      else {
        errorMessage = String.format("Error with check query ");
        result = false;
      }
    } catch ( Exception e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      result = false ;
    }

    if (!result) {
      return false;
    }

    if (checkresult>0) {
      logger.debug("Case \"" + item.getNumber()
              + "\" already exists in the database");
      return true;
    }

    //-- Now we can work with insert statement
    try {
      insertStatement.reset();

      SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
      String dateSch = dateFormat.format(item.getDate());
      //logger.debug("Going to add param #1 (" + dateSch + ")");
      insertStatement.bind(1, dateSch);
      //logger.debug("Going to add param #2 (" + item.getJudge() + ")");
      insertStatement.bind(2, item.getJudge());
      insertStatement.bind(3, item.getNumber());
      insertStatement.bind(4, item.getInvolved());
      insertStatement.bind(5, item.getDescription());
      insertStatement.bind(6, item.getType());
      insertStatement.bind(7, item.getAddress());
      logger.debug("Going to update db for \"" + item.getNumber() + "\"");

      insertStatement.stepThrough();

    } catch ( Exception e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      result = false ;
    }

    return result;
  }

  public boolean finish() {
    boolean result = true;
    if (connection != null) {
      try {
        connection.exec("COMMIT; ");

        insertStatement.dispose();
        checkStatement.dispose();
        connection.dispose();

        connection = null;
        insertStatement = null;
        checkStatement = null;
      } catch ( Exception e ) {
        errorMessage = "at finish: " + e.getClass().getName() +
                ": " + e.getMessage() ;
        result = false ;
      }
    }
    return result;
  }

}
