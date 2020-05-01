package cinepolis.keycoak.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class DemoUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        // here you can setup the user storage provider, initiate some connections, etc.

        UserRepository repository = new UserRepository();

        return new CustomUserStorageProvider(session, model, repository);
    }

    @Override
    public String getId() {
        return "cinepolis-auth-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property(
                    "customParam1", 
                    "Custom Param 1", 
                    "Parametro experimental by Goose 1", 
                    ProviderConfigProperty.STRING_TYPE, 
                    "algun valor cool 1", 
                    null
                )
                .property(
                    "customParam2", 
                    "Custom Param 2", 
                    "Parametro experimental by Goose 2", 
                    ProviderConfigProperty.STRING_TYPE, 
                    "algun valor cool 2", 
                    null)
                .build();
    }
}
