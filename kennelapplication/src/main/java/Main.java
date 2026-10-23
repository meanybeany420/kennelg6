import database.Databaseinit;
import UI.MainFrame;

public class Main {
  public static void main(String[] args) {
    Databaseinit.initDatabase();

    new MainFrame();
  }
}
