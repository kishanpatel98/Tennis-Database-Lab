import java.sql.*;
import java.lang.String;

public class Assignment2 {

  // A connection to the database
  Connection connection;

  // Statement to run queries
  Statement sql;

  // Prepared Statement
  PreparedStatement ps;

  // Resultset for the query
  ResultSet rs;

  //CONSTRUCTOR
  Assignment2(){
    try {
      // Load the JDBC Driver
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      //System.out.println("Driver not found");
      return;
    }
  }

  public static void main(String[] argv) {
    Assignment2 db = new Assignment2();
    db.connectDB("jdbc:postgresql://db:5432/kishanpa", "kishanpa", "Kishan/100");
    //db.insertPlayer(999,"BOB MARLEY", 999, 135);
    //db.insertPlayer(916,"Zack KNIGHT", 998, 135);
    //db.getChampions(901);
    //db.getCourtInfo(310);
    //db.chgRecord(900, 2018, 50, 7);
    //db.deleteMatcBetween(914, 906);
    //db.deleteMatcBetween(915, 916);
    //db.findTriCircle();
    //db.updateDB();
    //db.listPlayerRanking();
  }
  //Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
  public boolean connectDB(String URL, String username, String password){
    try {
      connection = DriverManager.getConnection(URL, username, password);
      sql = connection.createStatement();
      //System.out.println("Database is Connected!");
    }

    catch (SQLException e) {
      //System.out.println("Database Connection Failed!");
      //e.printStackTrace();
      return false;
    }

    //System.out.println("Connected!");
    // System.out.println(connection != null);
    return (connection != null);

  }

  //Closes the connection. Returns true if closure was sucessful
  public boolean disconnectDB(){
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
      if (sql != null && !sql.isClosed()) {
        sql.close();
      }
      if (rs != null && !rs.isClosed()) {
        rs.close();
      }
      if (ps != null && !ps.isClosed()) {
        ps.close();
      }
      return (connection == null || connection.isClosed());
    }

    catch (SQLException e) {
      // e.printStackTrace();
      return false;
    }
  }

  public boolean insertPlayer(int pid, String pname, int globalRank, int cid) {
    try{
      String sqlQuery = "SELECT pid FROM A2.player WHERE pid = " + pid + ";";
      rs = sql.executeQuery(sqlQuery);
      System.out.println(rs);

      if (!rs.next()) {
        String sqlText = "INSERT INTO A2.player VALUES (" + pid + ", '" + pname + "', " + globalRank + ", " + cid + ")";

        ps = connection.prepareStatement(sqlText);
        int numRows = ps.executeUpdate();
        ps.close();
        rs.close();

        if (numRows == 1) {
          System.out.println("Player has been added to your database!");
          return true;
        }

        else {
          System.out.println("Error! Player not added!");
          return false;
        }
      }
      else {
        System.out.println("Player already exists!");
        return false;
      }
    }
    catch (SQLException e){
      System.out.println("UNKNOWN ERROR");
      e.printStackTrace();
      return false;
    }
  }

  public int getChampions(int pid) {
    try {
      String sqlText = "SELECT count(pid) as count " +
      "FROM A2.champion " +
      "WHERE pid = " + pid +
      " GROUP BY pid;";
      rs = sql.executeQuery(sqlText);

      if (!rs.next()) { //player doesnt ahve championship wins
        rs.close();
        //System.out.println("boo");
        return 0;
      } else { // player has championship wins
        int count = rs.getInt("count");
        rs.close();
        //System.out.println("WINS = " +  count);
        return count;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public String getCourtInfo(int courtid){
    try {
      String sqlText = "SELECT * " + "FROM A2.court " + "WHERE courtid = " + courtid + ";";
      rs = sql.executeQuery(sqlText);

      if (rs.next()) {
        int courtId = rs.getInt("courtid");
        String courtName = rs.getString("courtname");
        int courtCap = rs.getInt("capacity");
        int courtTour = rs.getInt("tid");
        rs.close();
        //System.out.println(Integer.toString(courtId) + ":" + courtName + ":" + Integer.toString(courtCap)+ ":" + Integer.toString(courtTour));
        return Integer.toString(courtId) + ":" + courtName + ":" + Integer.toString(courtCap)+ ":" + Integer.toString(courtTour);
      } else {
        rs.close();
        return "";
      }
    } catch (SQLException e) {
      // e.printStackTrace();
      return "";
    }
  }

  public boolean chgRecord(int pid, int year, int wins, int losses){
    try {
      String sqlText = "UPDATE A2.record " + "SET wins = " + wins + ", losses = " + losses +
      " WHERE pid = " + pid +
      " AND year = " + year + ";";
      int numRows = sql.executeUpdate(sqlText);
      if (numRows == 1) {
        return true;
      } else {
        return false;
      }
    } catch (SQLException e) {
      //e.printStackTrace();
      return false;
    }
  }

  public boolean deleteMatcBetween(int p1id, int p2id){
    try {
      String sqlText = "DELETE FROM A2.event " +
      "WHERE (winid= " + p1id +
      " AND lossid= " + p2id + ") OR (winid= " + p2id +
      " AND lossid= " + p1id + ");";
      int numRows = sql.executeUpdate(sqlText);
      //System.out.println("# matches "+numRows);

      if (numRows >= 1) {
        //System.out.println("True "+numRows);
        return true;
      }

      //System.out.println("NO MATCH False "+numRows);
      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public String listPlayerRanking(){
    try {
      String sqlText = "SELECT P.pname AS name, P.globalrank AS rank "
      + "FROM A2.player P "
      + "ORDER BY P.globalrank ASC;";
      rs = sql.executeQuery(sqlText);
      //System.out.println("query");
      String answer = "";

      if (rs != null) {
        while (rs.next()) {
          answer += rs.getString("name") + ":" + rs.getInt("rank") + "\n";
            //System.out.println("Loop"+answer);
        }
      } else {
        answer = "";
      }
      rs.close();
      //System.out.println("True "+answer);
      return answer;
    }

    catch (SQLException e) {
       //e.printStackTrace();
      return "";
    }
  }

  public int findTriCircle(){
    try {
      String sqlText = "SELECT COUNT(*)/3 AS counter " +
      "FROM A2.event A, A2.event B, A2.event C " +
      "WHERE A.lossid = B.winid AND B.lossid = C.winid AND C.lossid = A.winid;";
      rs = sql.executeQuery(sqlText);

      //System.out.println("query worked");

      if (!rs.next()) {
        rs.close();
        //System.out.println("# of tri " + 0);
        return 0;
      }
      else {
        int count = rs.getInt("counter");
        rs.close();
        //System.out.println("# of tri " + count);
        return count;
      }
    } catch (SQLException e) {
      // e.printStackTrace();
      return -1;
    }
  }

  public boolean updateDB(){
    try {
      String sqlText = "CREATE TABLE A2.championPlayers (pid INTEGER, pname VARCHAR(20), nchampions INTEGER, PRIMARY KEY (pid, pname));";
      sql.executeUpdate(sqlText);
      String sqlQuery;
      //System.out.println("table created");
      sqlQuery = "INSERT INTO A2.championPlayers (SELECT P.pid, P.pname, COUNT(C.tid) " +
      "FROM A2.champion C JOIN A2.player P ON C.pid = P.pid " +
      "GROUP BY P.pid " +
      "ORDER BY P.pid ASC);";

      int numRows = sql.executeUpdate(sqlQuery);
      //System.out.println("# rows " + numRows);
      if (numRows >= 1){
        //System.out.println("True");
        return true;
      }
      else
      {
        //System.out.println("False");
        return false;
      }
    }

    catch (SQLException e) {
      //e.printStackTrace();
      return false;
    }
  }
}
