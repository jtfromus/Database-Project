package Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class main {

    public static void createNewDatabase(String fileName){
        String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        createNewDatabase("Phase3");
        SQL sql = new SQL("jdbc:sqlite:C:/sqlite/db/Phase3");
        sql.createTables();
        Menu menu = new Menu(sql);
        menu.menu();
    }
}
