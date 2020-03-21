package cinepolis.keycoak.user;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatabaseConnector {
    private static final String DB_USERNAME="db.username";
    private static final String DB_PASSWORD="db.password";
    private static final String DB_URL ="db.url";
    private static final String DB_DRIVER_CLASS="driver.class.name";
    
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    private static final String JDBC_QUERY = "SELECT id, nombre, primer_apellido, segundo_apellido, usuario, contrasena, correo, activo, interno, fecha_alta, estatus FROM cinepolis.usuario";
    private static final String SQL_UPDATE = "UPDATE cinepolis.usuario SET contrasena=? WHERE username=?";

    private static DatabaseConnector instance = null;

    public static DatabaseConnector getInstance() {
        if(instance==null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    private DatabaseConnector() {
        try {
        	Properties properties = new Properties();
            InputStream fis = DatabaseConnector.class.getClassLoader().getResourceAsStream("database.properties");
            properties.load(fis);
            
            Set<Object> propKeySet = properties.keySet();
            for(Object propertyKey : propKeySet) {
                String propValue = properties.getProperty(propertyKey.toString());
                if(propValue!=null) {
                	prn(propertyKey.toString() + ":" + propValue);
                }
            }
            
            dataSource.setDriverClass(properties.getProperty(DB_DRIVER_CLASS));
            dataSource.setJdbcUrl(properties.getProperty(DB_URL));
            dataSource.setUser(properties.getProperty(DB_USERNAME));
            dataSource.setPassword(properties.getProperty(DB_PASSWORD));
            
            dataSource.setMinPoolSize(3);
            dataSource.setMaxPoolSize(24);
            dataSource.setAcquireIncrement(4);
            
        } catch (IOException | PropertyVetoException e) {
            prn(e.getMessage());
        }
    }
    public boolean updateCredentials(String username, String password) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE);
            ) {
                pstmt.setString(1, password);
                pstmt.setString(2, username);
                pstmt.execute();
                return true;
            } catch (SQLException e) {
                prn(e.toString());
                return false;
            }
    }
    public List<RemoteUser> getAllUsers() {
        List<RemoteUser> lista = new ArrayList<>();
        try (Connection        con   = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(JDBC_QUERY);
            ResultSet         rs    = pstmt.executeQuery();) {
        	
            // id, nombre, primer_apellido, segundo_apellido, 		1,2,3,4
        	// usuario, contrasena, correo, 						5,6,7
        	// activo, interno, fecha_alta, estatus					8,9,10,11
        	String id = ""+rs.getInt(1);
        	String username = rs.getString(5);
        	String password = rs.getString(6);
        	String email = rs.getString(7);
        	String firstName = rs.getString(2);
        	String lastName = rs.getString(4);
        	
            while (rs.next()) {
               lista.add(new RemoteUser(id, username, password, email, firstName, lastName));
            }
        } catch (SQLException e) {
            prn(e.toString());
        } 
        return lista;
    }
    
    private void prn(String s) {
        System.out.println(s);
    }
}
