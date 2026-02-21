package net.ooder.server.httpproxy.core;

public class ConfigOption {
    String propertyName;
    String defaultValue;
    boolean isRequired;
    String helpString;


    public ConfigOption(String propertyName, String helpString) {
        this(propertyName, false, helpString);
    }

    public ConfigOption(String propertyName, String defaultValue, String helpString) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.isRequired = false;
        this.helpString = helpString;
    }

    public ConfigOption(String propertyName, boolean required, String helpString) {
        this.propertyName = propertyName;
        this.defaultValue = null;
        this.isRequired = required;
        this.helpString = helpString;
    }


    public String getProperty(Server server, String name) {
        String key = propertyName;
        if (name != null) {
            key = name + "." + key;
        }
        String value = server.getProperty(key, defaultValue);
        if (isRequired && value == null) {
            throw new IllegalArgumentException(key + " is a required argument.");
        }

        return value.trim();
    }


    public Boolean getBoolean(Server server, String name) {
        return new Boolean(getProperty(server, name));
    }


    public Integer getInteger(Server server, String name) {
        return new Integer(getProperty(server, name));
    }


    public String toHelp() {
        return propertyName + "\t" + (isRequired ? "Required" : "Optional") + "\t" + defaultValue + "\t" + helpString;
    }


    public String getName() {
        return propertyName;
    }
}
