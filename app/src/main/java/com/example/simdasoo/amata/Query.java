package com.example.simdasoo.amata;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class Query {
    Context context;
    public Query(Context context) {
        this.context = context;
    }

    //DB에 데이터 삽입
    void testInsert(SQLiteDatabase database, String tagUID, String name) {
        String n = null;
        //this is the real codes
        //기등록된 태그가 아닌 경우 등록
        try {
            database.execSQL("INSERT INTO registered_list VALUES('" + tagUID + "','" + name + "')");
            database.execSQL("INSERT INTO check_info(ID) VALUES('" + tagUID + "')");
            database.execSQL("INSERT INTO inout_info(ID) VALUES('" + tagUID + "')");
        } catch (SQLException e) {
            Toast.makeText(context,"이미 등록된 태그입니다",Toast.LENGTH_SHORT).show();
        }
        //this codes for test
//        database.execSQL("INSERT INTO registered_list VALUES('" + name + "','" + name + "')");
//        database.execSQL("INSERT INTO check_info(ID) VALUES('" + name + "')");
//        database.execSQL("INSERT INTO inout_info(ID) VALUES('" + name + "')");
    }

    //DB 데이터 삭제
    void testDelete(SQLiteDatabase database, Context context) {
        database.execSQL("DELETE FROM registered_list");
        database.execSQL("DELETE FROM check_info");
        database.execSQL("DELETE FROM inout_info");
        Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
    }

    //DB 데이터 개수 조회
    public int count(SQLiteDatabase database) {
        int count = 0;
        String sql_reg = "select * from registered_list";
        Cursor cursor_reg = database.rawQuery(sql_reg, null);
        if (cursor_reg != null) {
            count += cursor_reg.getCount(); //등록된 물품 개수얻기
        }
        return count;
    }

    //InOut 변경
    public void changeInOut(SQLiteDatabase database, String tagID) {
        String inOutInfo = "";
        String query = String.format("SELECT * FROM inout_info where id = '%s'",tagID);
        Log.d("Query",query);
        Cursor cursor = database.rawQuery(query, null);
        Log.d("cursor", String.valueOf(cursor.getCount()));
        if (cursor != null) {
            if(cursor.getCount()==0) ;
            else if (cursor.moveToFirst()) {
                do {
                    //테이블에서 이름 가져오기
                    inOutInfo = cursor.getString(cursor.getColumnIndex("IN_OUT"));
                } while (cursor.moveToNext());
            }
        }

        String change2Out = String.format("UPDATE inout_info SET IN_OUT = 'O' where id = '%s'",tagID);
        String change2In = String.format("UPDATE inout_info SET IN_OUT = 'I' where id = '%s'",tagID);
        if(inOutInfo.equals("I")) database.execSQL(change2Out);
        else database.execSQL(change2In);
    }

    // 아이템 삭제
    public void deleteItme(SQLiteDatabase database, String beforeName) {
        String tagID = "";
        String sql = String.format("SELECT * FROM registered_list where NAME = '%s'", beforeName);
        Log.d("Query",sql);
        Cursor cursor = database.rawQuery(sql, null);
        Log.d("cursor", String.valueOf(cursor.getCount()));
        if (cursor != null) {
            if(cursor.getCount()==0) ;
            else if (cursor.moveToFirst()) {
                do {
                    //테이블에서 이름 가져오기
                    tagID = cursor.getString(cursor.getColumnIndex("ID"));
                } while (cursor.moveToNext());
            }
        }
        String dFromReg = String.format("DELETE FROM registered_list where ID = '%s'", tagID);
        String dFromCh = String.format("DELETE FROM check_info where ID = '%s'", tagID);
        String dFromIo = String.format("DELETE FROM inout_info where ID = '%s'", tagID);
        database.execSQL(dFromReg);
        database.execSQL(dFromCh);
        database.execSQL(dFromIo);
    }
}