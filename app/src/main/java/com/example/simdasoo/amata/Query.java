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

    // DB에 데이터 삽입
    public void addStuff(SQLiteDatabase database, String tagUID, String name) {
        String n = null;
        // this is the real codes
        // 기등록된 태그가 아닌 경우 등록
        try {
            database.execSQL("INSERT INTO registered_list VALUES('" + tagUID + "','" + name + "')");
            database.execSQL("INSERT INTO check_info(ID) VALUES('" + tagUID + "')");
            database.execSQL("INSERT INTO inout_info(ID) VALUES('" + tagUID + "')");
        } catch (SQLException e) {
            Toast.makeText(context,"이미 등록된 태그입니다",Toast.LENGTH_SHORT).show();
        }

        // this codes for test
//        database.execSQL("INSERT INTO registered_list VALUES('" + name + "','" + name + "')");
//        database.execSQL("INSERT INTO check_info(ID) VALUES('" + name + "')");
//        database.execSQL("INSERT INTO inout_info(ID) VALUES('" + name + "')");
    }

    // 기준 태그가 있는지 판단
    public boolean existMain(SQLiteDatabase database){
        int exist = 0;
        String sql_main = "select * from main";

        Cursor cursor_main = database.rawQuery(sql_main, null);

        if (cursor_main != null){
            exist += cursor_main.getCount();
        }
        if(exist == 0) return false;
        else           return true;
    }

    // DB 데이터 개수 조회
    public int count(SQLiteDatabase database) {
        int count = 0;
        String sql_reg = "select * from registered_list";
        Cursor cursor_reg = database.rawQuery(sql_reg, null);
        if(existMain(database)) count++;
        if (cursor_reg != null) {
            count += cursor_reg.getCount(); // 등록된 물품 개수얻기
        }
        return count;
    }

    //InOut 변경
    public void changeInOut(SQLiteDatabase database, String tagID) {
        String inout = findValue(database, "inout_info", "ID", tagID, "IN_OUT");

        String change2Out = String.format("UPDATE inout_info SET IN_OUT = 'O' where id = '%s'",tagID);
        String change2In = String.format("UPDATE inout_info SET IN_OUT = 'I' where id = '%s'",tagID);
        if(inout.equals("I")) database.execSQL(change2Out);
        else database.execSQL(change2In);
    }

    // 아이템 삭제
    public void deleteItme(SQLiteDatabase database, String name) {
        String tagID = findValue(database, "registered_list", "NAME", name, "ID");
        String dFromReg = String.format("DELETE FROM registered_list where ID = '%s'", tagID);
        String dFromCh = String.format("DELETE FROM check_info where ID = '%s'", tagID);
        String dFromIo = String.format("DELETE FROM inout_info where ID = '%s'", tagID);
        database.execSQL(dFromReg);
        database.execSQL(dFromCh);
        database.execSQL(dFromIo);
    }

    // 아이템 이름 수정
    public void modifyItem(SQLiteDatabase database, String beforeName, String newName) {
        String value = findValue(database, "registered_list", "NAME", beforeName, "ID");
        String modifyName = String.format("UPDATE registered_list SET NAME = '%s' where ID = '%s'", newName, value);
        database.execSQL(modifyName);
    }

    // 체크값 변경
    public String changeCheckInfo(SQLiteDatabase database, String name) {
        String tagID = findValue(database, "registered_list", "NAME", name, "ID");

        String check_value = findValue(database, "check_info", "ID", tagID, "CHECK_VALUE");

        String modifyChecked = String.format("UPDATE check_info SET CHECK_VALUE = 'Y' where ID = '%s'", tagID);
        String modifyUnchecked = String.format("UPDATE check_info SET CHECK_VALUE = 'N' where ID = '%s'", tagID);
        if(check_value.equals("N")) {
            database.execSQL(modifyChecked);
        }
        else {
            database.execSQL(modifyUnchecked);
        }

        check_value = findValue(database, "check_info", "ID", tagID, "CHECK_VALUE");

        return check_value;
    }

    /**
    테이블에서 원하는 값 찾기.
        findValue(DB 이름, 테이블이름, 알고 있는 값을 가지고 있는 컬럼, 알고있는 값, 원하는 값을 갖고 있는 컬럼)
    **/
    public String findValue(SQLiteDatabase database, String table_name, String clue_column, String clue_value, String target_column) {
        String value = "";
        String query = String.format("SELECT * FROM %s where %s = '%s'",table_name, clue_column, clue_value);
        Log.d("Query",query);
        Cursor cursor = database.rawQuery(query, null);
        Log.d("cursor", String.valueOf(cursor.getCount()));
        if (cursor != null) {
            if(cursor.getCount()==0) ;
            else if (cursor.moveToFirst()) {
                do {
                    //테이블에서 in_out 정보 가져오기
                    value = cursor.getString(cursor.getColumnIndex(target_column));
                } while (cursor.moveToNext());
            }
        }
        return value;
    }
}