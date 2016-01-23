package frozenYard.app;



public class CmdArgsResults {
  boolean error = false;
  String errorMessage = "";

  public void setError(String em) {
    error = true;
    errorMessage = em;
  }

  boolean showHelp = false;

  String dbFileName = "";
  String jsonFileName = "";
  String fileListFileName = "";
}