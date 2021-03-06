package mx.qbits.keycloak.plugin;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserStorageProvider implements 
        UserStorageProvider, 
        UserRegistrationProvider,
        UserLookupProvider, 
        UserQueryProvider, 
        CredentialInputUpdater, 
        CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserRepository repository;

    public CustomUserStorageProvider(KeycloakSession session, ComponentModel model, UserRepository repository) {
        this.session = session;
        this.model = model;
        this.repository = repository;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.validateCredentials(user.getUsername(), cred.getChallengeResponse());
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.updateCredentials(user.getUsername(), cred.getChallengeResponse());
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
    }

    @Override
    public void preRemove(RealmModel realm) {
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        String externalId = StorageId.externalId(id);
        return new UserAdapter(session, realm, model, repository.findUserById(externalId));
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        RemoteUser user = repository.findUserByUsernameOrEmail(username);
        return new UserAdapter(session, realm, model, user);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return getUserByUsername(email, realm);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return repository.getUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return repository.getAllUsers().stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return repository.findUsers(search).stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return searchForUser(search, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return getUsers(realm);

    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return getUsers(realm);
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        ManageProperties.prn("Agregando: " + username + " realm.algo:");
        RemoteUser user = new RemoteUser();
        user.setUsername(username);
        user = repository.createUser(user);
        return new UserAdapter(session, realm, model, user);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        RemoteUser u = repository.findUserById(StorageId.externalId(user.getId()));
        if(u == null) {
            ManageProperties.prn("Tried to delete invalid user with ID " + user.getId());
            return false;
        }
        repository.deleteUser(u);
        return true;
    }
}
