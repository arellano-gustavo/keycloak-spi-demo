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
    private static final String DB_USERNAME     = "db.username";
    private static final String DB_PASSWORD     = "db.password";
    private static final String DB_URL          = "db.url";
    private static final String DB_DRIVER_CLASS = "driver.class.name";
    
    private static final String C3P0_MIN_POOL_SIZE     = "c3p0.minPoolSize";
    private static final String C3P0_MAX_POOL_SIZE     = "c3p0.maxPoolSize";
    private static final String C3P0_ACQUIRE_INCREMENT = "c3p0.acquireIncrement";
    private static final String C3P0_MAX_IDDLE_TIME    = "c3p0.maxIdleTime";
    private static final String C3P0_IDLE_CONNECTION_TEST_PERIOD = "c3p0.idleConnectionTestPeriod";
    
    
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    private static final String JDBC_QUERY = "SELECT id, nombre, primer_apellido, segundo_apellido, usuario, contrasena, correo, activo, interno, fecha_alta, estatus FROM cinepolis.usuario";
    private static final String SQL_UPDATE = "UPDATE cinepolis.usuario SET contrasena=? WHERE username=?";

    private static DatabaseConnector instance = null;
    private Properties properties = new Properties();

    public static DatabaseConnector getInstance() {
        if(instance==null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }
    
    private void printInfo() {
        Set<Object> propKeySet = properties.keySet();
        for(Object propertyKey : propKeySet) {
            String propValue = properties.getProperty(propertyKey.toString());
            if(propValue!=null) {
                prn(propertyKey.toString() + ":" + propValue);
            }
        }
    }

    private DatabaseConnector() {
        try {
            InputStream fis = DatabaseConnector.class.getClassLoader().getResourceAsStream("database.properties");
            properties.load(fis);
            this.printInfo();
            
            dataSource.setDriverClass(getStrPropertyValue(DB_DRIVER_CLASS));
            dataSource.setJdbcUrl(getStrPropertyValue(DB_URL));
            dataSource.setUser(getStrPropertyValue(DB_USERNAME));
            dataSource.setPassword(getStrPropertyValue(DB_PASSWORD));
            
            dataSource.setMinPoolSize(getIntPropertyValue(C3P0_MIN_POOL_SIZE, 3));
            dataSource.setMaxPoolSize(getIntPropertyValue(C3P0_MAX_POOL_SIZE, 24));
            dataSource.setAcquireIncrement(getIntPropertyValue(C3P0_ACQUIRE_INCREMENT, 4));
            
            //https://github.com/metabase/metabase/issues/10063
            dataSource.setMaxIdleTime(getIntPropertyValue(C3P0_MAX_IDDLE_TIME, 600)); // 10 minutos
            dataSource.setIdleConnectionTestPeriod(getIntPropertyValue(C3P0_IDLE_CONNECTION_TEST_PERIOD, 300)); // 5 minutos
        } catch (IOException | PropertyVetoException e) {
            prn(e.getMessage());
        }
    }

    private String getStrPropertyValue(String key) {
        String value = properties.getProperty(key);
        prn("Obetniendo valor para propiedad ["+key+"]. Resultado: [" + value + "]");
        return value;
    }

    private int getIntPropertyValue(String key, int defaultValue) {
        String data = getStrPropertyValue(key);
        try {
            int valor = Integer.parseInt(data);
            prn("Se ha convertido el dato: ["+data+"] al valor entero: [" + valor + "]");
            return valor;
        } catch (Exception e) {
            prn(defaultValue+" Sera usado como valor de retorno al no encontar la llave: "+data);
            prn(e.toString());
            return defaultValue;
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
            while (rs.next()) {
                // id, nombre, primer_apellido, segundo_apellido,          1,2,3,4
                // usuario, contrasena, correo,                            5,6,7
                // activo, interno, fecha_alta, estatus                    8,9,10,11
                String id = ""+rs.getInt(1);
                String username = rs.getString(5);
                String password = rs.getString(6);
                String email = rs.getString(7);
                String firstName = rs.getString(2);
                String lastName = rs.getString(4);
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
    
    public static void main(String...argv) {
    	DatabaseConnector.getInstance();
    }
}
