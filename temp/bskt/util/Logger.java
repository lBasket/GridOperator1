package bskt.util;


// set classpath=C:\Users\sarath_sivan\Desktop\jars\servlet-api.jar; C:\Users\sarath_sivan\Desktop\jars\spring-jdbc-3.0.2.RELEASE; C:\Users\sarath_sivan\Desktop\jars\spring-aop-3.0.2.RELEASE;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author sqlitetutorial.net
 */
public class Logger {
    private static Connection cn;


    public Logger(String player_name) {
        cn = createNewDatabase(player_name);
        createTables();
     }




    private static Connection createNewDatabase(String fileName) {
        Connection conn;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("except");
            System.out.println(e.getMessage());
        }
        //TODO Check if path exists, if not, create it. Instead of hardcoding & failing
        String url = "jdbc:sqlite:C:\\Users\\Basket\\Documents\\Java\\Griddbs\\"+fileName+".db";

        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                return conn;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }




    private void createTables () {

        String generation_table = "CREATE TABLE IF NOT EXISTS generation (\n"
                            + "genid integer,\n"
                            + "time integer,\n" //Best to use epoch seconds - will need way to convert back & forth. Think SQLITE has builtins but i may want them outside so I can query using my epoch secs
                            + "genname text NOT NULL,\n"
                            + "mwhgen real NOT NULL,\n"
                            + "mmbtu real NOT NULL,\n"
                            + "revenue real NOT NULL,\n"
                            + "emisslbs real NOT NULL,\n"
                            + "loadperc real NOT NULL,\n"
                            + "outload real NOT NULL,\n"
                            + "pm text NOT NULL,\n"
                            + "PRIMARY KEY (genid, time)"
                            + ");";

        String finance_table = "CREATE TABLE IF NOT EXISTS finance (\n" //this should be replaced with a view I think
                            + "id integer NOT NULL,\n"
                            + "time integer,\n"
                            + "revenues real NOT NULL,\n"
                            + "fuelcost real NOT NULL,\n"
                            + "fines real NOT NULL,\n"
                            + "PRIMARY KEY (id, time)\n"
                            + ");";

        String load_table = "CREATE TABLE IF NOT EXISTS load (\n"
                            + "loadid integer NOT NULL,\n"
                            + "time integer,\n"
                            + "loadtype text NOT NULL,\n"
                            + "loadmw real NOT NULL,\n"
                            + "PRIMARY KEY (loadid, time)"
                            + ");";

        try (Statement stmt = cn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS generation;");
            stmt.execute("DROP TABLE IF EXISTS load;");
            stmt.execute("DROP TABLE IF EXISTS finance;");
//            stmt.execute("COMMIT;");
            stmt.execute(generation_table);
            stmt.execute(finance_table);
            stmt.execute(load_table);
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println(e.getMessage());
        }


    }
    

    public void insertLoad (ArrayList<ArrayList> rows) {
        try {
            cn.setAutoCommit(false);
            PreparedStatement stmt = cn.prepareStatement("INSERT INTO load VALUES "
                    + "(?, ?, ?, ? );");

            for (ArrayList outlist : rows ) {
                for (Object inlist : outlist ) {
                    ArrayList list = (ArrayList) inlist;

                    stmt.setInt(1,(int) list.get(0));
                    stmt.setInt(2, (int) epochSecs((String) list.get(1)));
                    stmt.setString(3, (String) list.get(2));
                    stmt.setDouble(4, (Double) list.get(3));
                    stmt.addBatch();

                }

            }
            stmt.executeBatch();
            cn.setAutoCommit(true);
        } catch (SQLException e ) {
            System.out.println(e);
        }

    }

    public void insertGen (ArrayList<ArrayList> rows) {
        try {
            cn.setAutoCommit(false);
            PreparedStatement stmt = cn.prepareStatement("INSERT INTO generation VALUES "
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

            for (ArrayList outlist : rows ) {
                for (Object inlist : outlist ) {
                    ArrayList list = (ArrayList) inlist;
                    stmt.setInt(1,(int) list.get(0));
                    stmt.setInt(2, (int) epochSecs((String) list.get(1)));
                    stmt.setString(3, (String) list.get(2));
                    stmt.setDouble(4, (Double) list.get(3));
                    stmt.setDouble(5, (Double) list.get(4));
                    stmt.setDouble(6, (Double) list.get(5));
                    stmt.setDouble(7, (Double) list.get(6));
                    stmt.setDouble(8, (Double) list.get(7));
                    stmt.setDouble(9, (Double) list.get(8));
                    stmt.setString(10, (String) list.get(9));
                    stmt.addBatch();

                }

            }
            stmt.executeBatch();
            cn.setAutoCommit(true);
        } catch (SQLException e ) {
            System.out.println(e);
        }

    }

    
    
        public void insertFinance (ArrayList<ArrayList> rows) {
        try {
            cn.setAutoCommit(false);
            PreparedStatement stmt = cn.prepareStatement("INSERT INTO generation VALUES "
                    + "(?, ?, ?, ?, ? );");

            for (ArrayList outlist : rows ) {
                for (Object inlist : outlist ) {
                    ArrayList list = (ArrayList) inlist;
                    stmt.setInt(1,(int) list.get(0));
                    stmt.setInt(2, (int) epochSecs((String) list.get(1)));
                    stmt.setDouble(3, (Double) list.get(2));
                    stmt.setDouble(4, (Double) list.get(3));
                    stmt.setDouble(5, (Double) list.get(4));
                    stmt.addBatch();

                }

            }
            stmt.executeBatch();
            cn.setAutoCommit(true);
        } catch (SQLException e ) {
            System.out.println(e);
        }

    }


    
    
    
    private long epochSecs(String datestr) {
        /*
        Turns a string formatted date into EpochSeconds for sqlite storage
        */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime curdate = LocalDateTime.parse(datestr, formatter);
        return curdate.toEpochSecond(ZoneOffset.UTC);
    }

}