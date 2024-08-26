package com.example.stokTakipProjesi.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
        protected String dbUrl;
        protected String username;
        protected String password;
        protected Connection connection;

        public DBconnection(String dbUrl, String username, String password) {
            this.dbUrl = dbUrl;
            this.username = username;
            this.password = password;
        }
        public Connection createConnection() {
            try {
                this.connection = DriverManager.getConnection(this.dbUrl, this.username, this.password);
                System.out.println("Connection Successful");
            } catch (SQLException e) {
                System.out.println("Connection Failed: " + e.getMessage());
            }
            return this.connection;
        }
        public void closeConnection() {
            try {
                this.connection.close();
                this.connection = null;
                System.out.println("Connection Closed");
            } catch (SQLException e) {
                System.out.println("Connection Failed: " + e.getMessage());
            }
        }

    }

