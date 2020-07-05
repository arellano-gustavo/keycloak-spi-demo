package cinepolis.keycoak.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {

    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        // here we can setup the user storage provider, initiate some connections, etc.
        List<ProviderConfigProperty> props = getConfigProperties();
        ManageProperties.prn("*************************>" + props.get(0).getLabel());
        DatabaseConnector dbc = DatabaseConnector.getInstance();
        UserRepository repository = new UserRepository(dbc);
        return new CustomUserStorageProvider(session, model, repository);
    }

    @Override
    public String getId() {
        return ManageProperties.getInstance().getStrPropertyValue("provider.name");
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
                    null
                )
                .build();
    }
}
