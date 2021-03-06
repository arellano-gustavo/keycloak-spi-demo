package mx.qbits.keycloak.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class ManageProperties {
    private static ManageProperties instance = null;
    private Properties properties = new Properties();
    private Properties connProperties = new Properties();
    private String perfil;
    
    public static ManageProperties getInstance() {
        if(instance==null) {
            instance = new ManageProperties();
        }
        return instance;
    }
    
    private void printInfo(Properties properties) {
    	prn("****************************************************************");
    	prn("****************************************************************");
    	prn("****************************************************************");
    	
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
            InputStream connFis = ManageProperties.class.getClassLoader().getResourceAsStream("conn.properties");
            
            properties.load(fis);
            connProperties.load(connFis);
            
            this.perfil = connProperties.getProperty("current.perfil");
            
            this.printInfo(properties);
            this.printInfo(connProperties);
        } catch (IOException e) {
            prn(e.getMessage());
        }
    }
    
    public String getStrPropertyValue(String name) {
    	if(this.perfil==null && this.perfil.trim().equals("")) {
    		ManageProperties.prn("El perfil actual es NULO");
    	} else {
    		ManageProperties.prn("Perfil:[[["+this.perfil+"]]]");
    	}
        String var = connProperties.getProperty(this.perfil + "." + name);
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
