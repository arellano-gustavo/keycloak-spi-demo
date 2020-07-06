package cinepolis.keycoak.user;

import java.util.List;
import java.util.stream.Collectors;

class UserRepository {
    private RemoteUser defaultUser = new RemoteUser("0", "NA", "p455W0rd*_!", "gus@aol.com", "fName", "lName");
    private DatabaseConnector dbc;

    public UserRepository() {
        dbc = DatabaseConnector.getInstance();
    }

    private List<RemoteUser> users() {
        return dbc.getAllUsers();
    }

    public List<RemoteUser> getAllUsers() {
        return users();
    }

    public int getUsersCount() {
        return users().size();
    }

    public RemoteUser findUserById(String id) {
        return users().stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    private boolean encontrado(RemoteUser user, String username) {
        return user.getUsername().equalsIgnoreCase(username) || user.getEmail().equalsIgnoreCase(username);
    }
    
    public RemoteUser findUserByUsernameOrEmail(String username) {
        RemoteUser usr = users().stream()
                .filter(user -> encontrado(user, username))
                .findFirst()
                .orElse(defaultUser);
        return usr;
        /** /
        for(DemoUser du : users) {
            if(du.getUsername().equals(username) || du.getEmail().equalsIgnoreCase(username)) {
                System.out.println("usuario "+username+" encontrado !!!!");
                return du;
            }
        }
        System.out.println("No se encontro al usuario: " + username);
        return null;
        /**/
    }

    public List<RemoteUser> findUsers(String query) {
        return users().stream()
                .filter(user -> user.getUsername().contains(query) || user.getEmail().contains(query))
                .collect(Collectors.toList());
    }

    public boolean validateCredentials(String username, String password) {
        ManageProperties.prn("Credentials: ["+username+"] ["+password+"]");
        RemoteUser u = findUserByUsernameOrEmail(username);
        if(u!=null && !"0".equals(u.getId())) {
            String pass = u.getPassword();
            ManageProperties.prn("************>"+pass+"<************");
            return pass.equals(password);
        }
        return false;
    }

    public boolean updateCredentials(String username, String password) {
        return dbc.updateCredentials(username, password);
    }

}
