package cinepolis.keycoak.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {
    private ManageProperties mp = ManageProperties.getInstance();

    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        // here we can setup the user storage provider, initiate some connections, etc.
        DatabaseConnector dbc = DatabaseConnector.getInstance();
        UserRepository repository = new UserRepository(dbc);
        return new CustomUserStorageProvider(session, model, repository);
    }

    @Override
    public String getId() {
        ManageProperties.prn("First line to be executed after ManageProperties ----------> CustomUserStorageProviderFactory::getId");
        return mp.getStrPropertyValue("provider.name");
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property(
                    "jdbcUrl", 
                    "Jdbc URL", 
                    "Cadena de conección a la base de datos", 
                    ProviderConfigProperty.STRING_TYPE, 
                    mp.getStrPropertyValue(DatabaseConnector.DB_URL), 
                    null
                )
                .property(
                    "driverJdbc", 
                    "Driver Jdbc", 
                    "Nombre del driver de conección", 
                    ProviderConfigProperty.STRING_TYPE, 
                    mp.getStrPropertyValue(DatabaseConnector.DB_DRIVER_CLASS), 
                    null
                )
                .property(
                        "dbUser", 
                        "Usuario", 
                        "Usuario de conección a la base", 
                        ProviderConfigProperty.STRING_TYPE, 
                        mp.getStrPropertyValue(DatabaseConnector.DB_USERNAME), 
                        null
                )
                .build();
    }
}
