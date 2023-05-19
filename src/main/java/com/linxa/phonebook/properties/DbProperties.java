package com.linxa.phonebook.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DbProperties {

    private static final DbProperties INSTANCE = new DbProperties();

    private final String connectionUrl;
    private final String username;
    private final String password;

    private DbProperties() {
        try (InputStream input = DbProperties.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.connectionUrl = prop.getProperty("connectionUrl");
            this.username = prop.getProperty("username");
            this.password = prop.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static DbProperties getInstance() {
        return INSTANCE;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
