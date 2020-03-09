package cinepolis.keycoak.user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

	/*
	#c3p0.jdbcUrl=
#c3p0.user=sa
#c3p0.password=gustavo
# The last data goes in the application.properties, NOT here !!!

c3p0.driverClass=org.h2.Driver

	*/
	public static List<DemoUser> getUsers() {
		List<DemoUser> lista = new ArrayList<>();
		try {
			Class.forName("org.h2.Driver");
			Connection con = DriverManager.getConnection("jdbc:h2:tcp://database.ci.ultrasist.net:1521/h2-data", "sa", "gustavo");
			// here sonoo is database name, root is username and password
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from users where id>217");
			while (rs.next()) {
				//System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
				lista.add(  new DemoUser(""+rs.getInt(1), rs.getString(2), rs.getString(3))  );
			}
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return lista;
	}
}
