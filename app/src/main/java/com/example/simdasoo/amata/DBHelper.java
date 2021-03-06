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
        // main 테이블 SQL문
        StringBuffer main_sb = new StringBuffer();
        main_sb.append(" CREATE TABLE main ( ");
        main_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        main_sb.append(" NAME VARCHAR(20), ");
        main_sb.append(" IN_OUT VARCHAR(2) DEFAULT 'I', ");
        main_sb.append(" CHECK_VALUE VARCHAR(2) DEFAULT 'Y'); ");

        // registered_list 테이블 SQL문
        StringBuffer reg_sb = new StringBuffer();
        reg_sb.append(" CREATE TABLE registered_list ( ");
        reg_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        reg_sb.append(" NAME VARCHAR(20)); ");

        // check_info 테이블 SQL문
        StringBuffer check_sb = new StringBuffer();
        check_sb.append(" CREATE TABLE check_info ( ");
        check_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        check_sb.append(" CHECK_VALUE VARCHAR(2) DEFAULT 'N'); ");

        // inout_info 테이블 SQL문
        StringBuffer io_sb = new StringBuffer();
        io_sb.append(" CREATE TABLE inout_info ( ");
        io_sb.append(" ID VARCHAR(20) PRIMARY KEY NOT NULL, ");
        io_sb.append(" IN_OUT VARCHAR(2) DEFAULT 'I'); ");

        db.execSQL(main_sb.toString());
        db.execSQL(reg_sb.toString());
        db.execSQL(check_sb.toString());
        db.execSQL(io_sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
