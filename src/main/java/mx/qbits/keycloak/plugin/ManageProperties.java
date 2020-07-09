package mx.qbits.keycloak.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class ManageProperties {

    private static ManageProperties instance = null;
    private Properties properties = new Properties();

    public static ManageProperties getInstance() {
        if(instance==null) {
            instance = new ManageProperties();
        }
        return instance;
    }
    
    private void printInfo() {
        Set<Object> propKeySet = properties.keySet();
        for(Object propertyKey : propKeySet) {
            String propValue = properties.getProperty(propertyKey.toString());
            if(propValue!=null) {
                prn(propertyKey.toString() + ":" + propValue);
            }
        }
    }

    private ManageProperties() {
        try {
            InputStream fis = ManageProperties.class.getClassLoader().getResourceAsStream("database.properties");
            properties.load(fis);
            this.printInfo();
        } catch (IOException e) {
            prn(e.getMessage());
        }
    }
    
    public String getStrPropertyValue(String name) {
        String var = System.getenv(name);
        if(var!=null) {
            ManageProperties.prn("Usando variable de ambiente "+name+" con valor:"+var);
            return var;
        }
        var = properties.getProperty(name);
        ManageProperties.prn("Usando parametro de propiedades "+name+" con valor:"+var);
        return var;
    }

    public int getIntPropertyValue(String key, int defaultValue) {
        String data = getStrPropertyValue(key);
        try {
            int valor = Integer.parseInt(data);
            prn("Se ha convertido el dato: ["+data+"] al valor entero: [" + valor + "]");
            return valor;
        } catch (Exception e) {
            prn(defaultValue+" Sera usado como valor de retorno al no encontar la llave: "+data);
            prn(e.toString());
            return defaultValue;
        }
    }

    public static void prn(String s) {
        System.out.println(s);
    }

    public static void main(String...argv) {
        ManageProperties.getInstance();
    }

}
