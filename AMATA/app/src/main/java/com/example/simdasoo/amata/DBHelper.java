package com.example.simdasoo.amata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "AMATA.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //criterion 테이블 SQL문
        StringBuffer crit_sb = new StringBuffer();
        crit_sb.append(" CREATE TABLE criterion ( ");
        crit_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        crit_sb.append(" IN_OUT VARCHAR(20)); ");

        //registered_list 테이블 SQL문
        StringBuffer reg_sb = new StringBuffer();
        reg_sb.append(" CREATE TABLE registered_list ( ");
        reg_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        reg_sb.append(" NAME VARCHAR(20)); ");

        //check_info 테이블 SQL문
        StringBuffer check_sb = new StringBuffer();
        check_sb.append(" CREATE TABLE check_info ( ");
        check_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        check_sb.append(" CHECK VARCHAR(20)); ");

        //in_out_info 테이블 SQL문
        StringBuffer io_sb = new StringBuffer();
        io_sb.append(" CREATE TABLE in_out_info ( ");
        io_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        io_sb.append(" IN_OUT VARCHAR(20)); ");

        db.execSQL(crit_sb.toString());
        db.execSQL(reg_sb.toString());
        db.execSQL(check_sb.toString());
        db.execSQL(io_sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
