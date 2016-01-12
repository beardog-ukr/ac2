package friedBox.component;

import desertCyborg.CaseItem;
//
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
//
import java.text.SimpleDateFormat;
import java.util.Date;
//
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqliteUpdater {

  private static final Logger logger = LoggerFactory.getLogger("friedBox.dbA");

  String errorMessage = "";
  public String getErrorMessage() {
    return errorMessage;
  }

  String courtId = "und.ef";
  public void setCourtId (String ci){
    courtId = ci;
  }

  Connection connection = null;
  PreparedStatement insertStatement = null;
  PreparedStatement checkStatement = null;
  //String insertDateStr = null;
  static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss" ;



  public boolean connectToDB(String dbFileName) {
    boolean result = true;
    String insertDateStr = "";
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:"+dbFileName);
      connection.setAutoCommit(false);

      Date tdt = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
      insertDateStr = dateFormat.format(tdt);
    } catch ( Exception e ) {
      errorMessage = e.getClass().getName() + ": " + e.getMessage() ;
      result = false ;
    }

    logger.debug("Connected to db");

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
        insertStatement = connection.prepareStatement(qs);
        checkStatement = connection.prepareStatement(sqs);
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
      SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
      String dateSch = dateFormat.format(item.getDate());
      checkStatement.setString(1, dateSch);
      checkStatement.setString(2, item.getJudge());
      checkStatement.setString(3, item.getNumber());
      checkStatement.setString(4, item.getInvolved());
      checkStatement.setString(5, item.getDescription());
      checkStatement.setString(6, item.getType());
      checkStatement.setString(7, item.getAddress());

      ResultSet rs = checkStatement.executeQuery();
      if (rs.next()) {
        checkresult = rs.getInt(1);
      }
      else {
        //logger.error("No results in check query");
        errorMessage = "No results in check query";
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

    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIMEFORMAT);
      String dateSch = dateFormat.format(item.getDate());
      //logger.debug("Going to add param #1 (" + dateSch + ")");
      insertStatement.setString(1, dateSch);
      //logger.debug("Going to add param #2 (" + item.getJudge() + ")");
      insertStatement.setString(2, item.getJudge());
      insertStatement.setString(3, item.getNumber());
      insertStatement.setString(4, item.getInvolved());
      insertStatement.setString(5, item.getDescription());
      insertStatement.setString(6, item.getType());
      insertStatement.setString(7, item.getAddress());
      logger.debug("Going to update db for \"" + item.getNumber() + "\"");
      insertStatement.executeUpdate();
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
        connection.commit();
        connection.close();
      } catch ( Exception e ) {
        errorMessage = "at finish: " + e.getClass().getName() +
                       ": " + e.getMessage() ;
        result = false ;
      }
    }
    return result;
  }

}
