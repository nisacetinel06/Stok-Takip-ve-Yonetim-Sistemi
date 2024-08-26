package com.example.stokTakipProjesi.Connection;
public class Auth {
      private String UserName;
      private String Password;

      public Auth(String userName, String password) {
          this.UserName = userName;
          this.Password = password;
      }
        public String getPassword() {
            return Password;
        }
        public String getUserName() {
            return UserName;
        }
    }

