package com.example.simdasoo.amata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class Query {
    public Query() {
    }

    //DB에 데이터 삽입
    void testInsert(SQLiteDatabase database, String tagUID, String name) {
        database.execSQL("INSERT INTO registered_list VALUES('" + tagUID + "','" + name + "')");
    }

    //DB 데이터 삭제
    void testDelete(SQLiteDatabase database, Context context) {
        database.execSQL("DELETE FROM registered_list");
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
}