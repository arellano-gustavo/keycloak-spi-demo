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
        ManageProperties.prn("-------------------------> 003");
        // here we can setup the user storage provider, initiate some connections, etc.
        List<ProviderConfigProperty> props = getConfigProperties();
        if(props!=null) {
            for(int i=0;i<3;i++) {
                ManageProperties.prn("*****>"+props.get(i).getDefaultValue());
            }
        }
        ManageProperties.prn("EJEMPLITO *****************>"+System.getenv("EJEMPLITO"));
        DatabaseConnector dbc = DatabaseConnector.getInstance();
        UserRepository repository = new UserRepository(dbc);
        return new CustomUserStorageProvider(session, model, repository);
    }

    @Override
    public String getId() {
        ManageProperties.prn("-------------------------> 001");
        return mp.getStrPropertyValue("provider.name");
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        ManageProperties.prn("-------------------------> 002");
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
