package friedBox.app;

import desertCyborg.CaseItem;

public interface DbUpdater {
  public boolean connectToDB(String dbFileName) ;
  public boolean addOneItem(CaseItem item) ;
  public boolean finish() ;

  public String getErrorMessage();
  public void setCourtId(String courtIdStr) ;
}