package mx.qbits.keycloak.plugin;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatabaseConnector {
    private static DatabaseConnector instance = null;
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
    private static ManageProperties mp = ManageProperties.getInstance();

    public static final String DB_USERNAME     = "db.username";
    public static final String DB_PASSWORD     = "db.password";
    public static final String DB_URL          = "db.url";
    public static final String DB_DRIVER_CLASS = "driver.class.name";
    
    private static final String C3P0_MIN_POOL_SIZE     = "c3p0.minPoolSize";
    private static final String C3P0_MAX_POOL_SIZE     = "c3p0.maxPoolSize";
    private static final String C3P0_ACQUIRE_INCREMENT = "c3p0.acquireIncrement";
    private static final String C3P0_MAX_IDDLE_TIME    = "c3p0.maxIdleTime";
    private static final String C3P0_IDLE_CONNECTION_TEST_PERIOD = "c3p0.idleConnectionTestPeriod";
    
    //private static String JDBC_QUERY = "select * from usuario";//mp.getStrPropertyValue("jdbc.query");
    private static final String JDBC_QUERY = "SELECT id, nombre, primer_apellido, segundo_apellido, usuario, contrasena, correo, activo, interno, fecha_alta, estatus FROM usuario";

    private static final String SQL_UPDATE = "UPDATE usuario SET contrasena=? WHERE username=?";
    private static final String SQL_DELETE = "DELETE FROM usuario WHERE usuario=?";
    private static final String SQL_INSERT = "insert into usuario(nombre, primer_apellido, usuario, contrasena, correo) values(?,?,?,?,?)";


    public static DatabaseConnector getInstance() {
        if(instance==null) {
            try {
                instance = new DatabaseConnector();
            } catch (PropertyVetoException e) {
                ManageProperties.prn("Retornando un DatabaseConnector nulo. Razon: "+e.getMessage());
            }
        }
        return instance;
    }
    
    private DatabaseConnector() throws PropertyVetoException {
        dataSource.setDriverClass(mp.getStrPropertyValue(DB_DRIVER_CLASS));
        dataSource.setJdbcUrl(mp.getStrPropertyValue(DB_URL));
        dataSource.setUser(mp.getStrPropertyValue(DB_USERNAME));
        dataSource.setPassword(mp.getStrPropertyValue(DB_PASSWORD));
        
        dataSource.setMinPoolSize(mp.getIntPropertyValue(C3P0_MIN_POOL_SIZE, 3));
        dataSource.setMaxPoolSize(mp.getIntPropertyValue(C3P0_MAX_POOL_SIZE, 24));
        dataSource.setAcquireIncrement(mp.getIntPropertyValue(C3P0_ACQUIRE_INCREMENT, 4));
        
        //https://github.com/metabase/metabase/issues/10063
        dataSource.setMaxIdleTime(mp.getIntPropertyValue(C3P0_MAX_IDDLE_TIME, 600)); // 10 minutos
        dataSource.setIdleConnectionTestPeriod(mp.getIntPropertyValue(C3P0_IDLE_CONNECTION_TEST_PERIOD, 300)); // 5 minutos
        
        Connection con = null;
        try {
        	con = dataSource.getConnection();
        	System.out.println("Successful first connection:" + JDBC_QUERY); 
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Finishing first connection:" + con.toString()); 
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
                ManageProperties.prn(e.toString());
                return false;
            }
    }

    public List<RemoteUser> getAllUsers() {
    	long counter = 0;
    	ManageProperties.prn("Obteniendo lista de usuarios...");
        List<RemoteUser> lista = new ArrayList<>();
        try (Connection        con   = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(JDBC_QUERY);
            ResultSet         rs    = pstmt.executeQuery();) {
            while (rs.next()) {
            	counter++;
                // SELECT usuario_id, nombre, primer_apellido, segundo_apellido, 
                // usuario, contrasena, correo, activo, interno, fecha_alta, cat_edo_usuario_id FROM usuario
                //
                // private static final String JDBC_QUERY = "
                // id, nombre, primer_apellido, segundo_apellido,          1,2,3,4
                // usuario, contrasena, correo,                            5,6,7
                // activo, interno, fecha_alta, estatus                    8,9,10,11
                //rs.getInt(columnLabel)
                String id        = rs.getInt(1)+""; // es numero porque aveces el ID cambia
                String username  = rs.getString("usuario");
                String password  = rs.getString("contrasena");
                String email     = rs.getString("correo");
                String firstName = rs.getString("primer_apellido");
                String lastName  = rs.getString("segundo_apellido");
               lista.add(new RemoteUser(id, username, password, email, firstName, lastName));
            }
        } catch (SQLException e) {
            ManageProperties.prn(e.toString());
            ManageProperties.prn(JDBC_QUERY);
        }
        ManageProperties.prn("Numero total de usuarios: "+counter);
        return lista;
    }

    public void deleteUser(String usuario) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement pstmt = con.prepareStatement(SQL_DELETE);
            ) {
                pstmt.setString(1, usuario);
                pstmt.execute();
            } catch (SQLException e) {
                ManageProperties.prn(e.toString());
            }
    }

    public RemoteUser addUser(RemoteUser user) {
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement pstmt = con.prepareStatement(SQL_INSERT);
            ) {
                pstmt.setString(1, user.getFirstName()); // nombre
                pstmt.setString(2, user.getLastName());  // primer_apellido
                pstmt.setString(3, user.getUsername());  // usuario
                pstmt.setString(4, user.getPassword());  // contrasena
                pstmt.setString(5, user.getEmail());     // correo
                pstmt.execute();
                return user;
            } catch (SQLException e) {
                ManageProperties.prn(e.toString());
                return null;
            }
    }
}
