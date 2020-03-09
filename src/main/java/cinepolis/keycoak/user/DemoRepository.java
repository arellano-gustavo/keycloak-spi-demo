package cinepolis.keycoak.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
class DemoRepository {

    private List<DemoUser> users;

    DemoRepository() {
        users = Arrays.asList(
                new DemoUser("1", "Fred", "Flintstone"),
                new DemoUser("3", "Wilma", "Flintstone"),
                new DemoUser("5", "Pebbles", "Flintstone"),
                new DemoUser("2", "Barney", "Rubble"),
                new DemoUser("4", "Betty", "Rubble"),
                new DemoUser("6", "Bam Bam", "Rubble")
        );
    }

    List<DemoUser> getAllUsers() {
        return users;
    }

    int getUsersCount() {
        return users.size();
    }

    DemoUser findUserById(String id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    private boolean encontrado(DemoUser user, String username) {
        return user.getUsername().equalsIgnoreCase(username) || user.getEmail().equalsIgnoreCase(username);
    }
    
    DemoUser findUserByUsernameOrEmail(String username) {
        DemoUser usr = users.stream()
                .filter(user -> encontrado(user, username))
                .findFirst()
                .orElse(null);
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

    List<DemoUser> findUsers(String query) {
        return users.stream()
                .filter(user -> user.getUsername().contains(query) || user.getEmail().contains(query))
                .collect(Collectors.toList());
    }

    boolean validateCredentials(String username, String password) {
        //System.out.println(username+"************"+password);
        DemoUser u = findUserByUsernameOrEmail(username);
        if(u!=null) {
            String pass = u.getPassword();
            //System.out.println("************"+pass);
            return pass.equals(password);
        }
        return false;
    }

    boolean updateCredentials(String username, String password) {
        findUserByUsernameOrEmail(username).setPassword(password);
        return true;
    }

}
