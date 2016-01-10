package desertCyborg;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

public class CaseItem {
  protected Date date;
  protected String  type;
  protected String number;
  protected String involved;
  protected String description;
  protected String judge;
  protected String address;

  public boolean setDate(String dateStr) {
    DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    ParsePosition pp = new ParsePosition(0);
    date = format.parse(dateStr, pp);

    if (pp.getErrorIndex()>-1) {
      return false;
    }

    return true;
  }

  public Date getDate() {
    return date;
  }

  public boolean setType(String typeStr) {
    type = typeStr;
    return true;
  }

  public String getType() {
    return type;
  }

  public boolean setNumber(String numberStr) {
    number = numberStr;
    return true;
  }

  public String getNumber() {
    return number;
  }

  public boolean setInvolved(String involvedStr) {
    involved = involvedStr;
    return true;
  }

  public String getInvolved() {
    return involved;
  }

  public boolean setDescription(String descriptionStr) {
    description = descriptionStr;
    return true;
  }

  public String getDescription() {
    return description;
  }

  public boolean setJudge(String judgeStr) {
    judge = judgeStr;
    return true;
  }

  public String getJudge() {
    return judge;
  }

  public boolean setAddress(String addressStr) {
    address = addressStr;
    return true;
  }

  public String getAddress() {
    return address;
  }

}
