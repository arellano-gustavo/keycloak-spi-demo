package cinepolis.keycoak.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class DemoUserStorageProviderFactory implements UserStorageProviderFactory<DemoUserStorageProvider> {

    @Override
    public DemoUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        // here you can setup the user storage provider, initiate some connections, etc.

        DemoRepository repository = new DemoRepository();

        return new DemoUserStorageProvider(session, model, repository);
    }

    @Override
    public String getId() {
        return "cinepolis-auth-provider";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("customParam", "Custom Param", "Parametro experimental by Goose", ProviderConfigProperty.STRING_TYPE, "algun valor cool", null)
                .build();
    }
}
