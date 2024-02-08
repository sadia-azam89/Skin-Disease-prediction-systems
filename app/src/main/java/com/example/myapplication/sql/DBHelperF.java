package com.example.myapplication.sql;

import android.widget.EditText;

public class DBHelperF {
    String userName,number,email;
    long pass;

    public DBHelperF() {
        this.userName = userName;
        this.number = number;
        this.email = email;
        this.pass = pass;
    }
    public DBHelperF(String userName,String number) {
        this.userName = userName;
        this.number = number;
        this.email = email;
        this.pass = pass;
    }




    public String getUserName() {
        return userName;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public long getPass() {
        return pass;
    }
}