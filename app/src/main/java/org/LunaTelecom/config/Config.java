package org.LunaTelecom.config;

public class Config {
    public DBConfig db;

    public static class DBConfig {
        public String  host;
        public int     port;
        public String  database;
        public String  user;
        public String  password;
    }
}
