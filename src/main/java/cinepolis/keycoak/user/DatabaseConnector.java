package cinepolis.keycoak.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    //private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String JDBC_URL = "jdbc:h2:tcp://database.ci.ultrasist.net:1521/h2-data";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "gustavo";
    private static final String JDBC_QUERY = "select * from users where id>217";
    
    private static DatabaseConnector instance = null;
    private PreparedStatement pstmt = null;
    
    public static DatabaseConnector getInstance() {
        if(instance==null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }
    
    private DatabaseConnector() {
        try {
            Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            this.pstmt = con.prepareStatement(JDBC_QUERY);
        } catch(SQLException e) {
            prn(e.toString());
        }
    }

    public List<DemoUser> getUsers() {
        List<DemoUser> lista = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = this.pstmt.executeQuery();
            while (rs.next()) {
                lista.add(  new DemoUser(""+rs.getInt(1), rs.getString(2), rs.getString(3))  );
            }
        } catch (SQLException e) {
            prn(e.toString());
        } finally {
            try {
                if(rs!=null) {
                    rs.close();
                }
            } catch (SQLException e) {
                prn(e.toString());
            }
        }
        return lista;
    }
    
    private void prn(String s) {
        System.out.println(s);
    }
}
