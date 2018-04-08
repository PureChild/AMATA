package com.example.simdasoo.amata;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


//첫화면
public class InitialFragment extends Fragment {
    int cntStuff;

    DBHelper dbHelper;
    SQLiteDatabase database;
    private ArrayList<HashMap<String, String>> itemList;
    private SimpleAdapter adapter;
    private ListView list;

    public View getView(){
        View rootview = getLayoutInflater().inflate(R.layout.initial_fragment,null);
        return rootview;
    }
    public void refresh() {
//        getFragmentManager().beginTransaction().replace(R.id.frag_container, this).commit();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //추가
        View rootview = getView();

        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog();
            }
        });

        //DB 연결
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.getWritableDatabase();

        //등록된 물건 수 확인
        cntStuff = count(database);

        //초기화면 등록된 물건이 없을 경우
        TextView tv = (TextView) rootview.findViewById(R.id.tv_main);
        if(cntStuff == 0){
//            tv.setText("test");
            tv.setText("+ 버튼을 눌러 기준이 될 태그를 등록해주세요.");
        }
        else {
//            tv.setText(" ");
            tv.setVisibility(View.GONE);
        }

        //ListView
        list = (ListView) rootview.findViewById(R.id.registered_list);
        itemList = new ArrayList<HashMap<String,String>>();

        return rootview;
    }

    //등록하기 위한 dialog
    public void Dialog(){
        final EditText et = new EditText(new ContextThemeWrapper(getActivity(), R.style.DialogEditTextStyle));
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyAlertDialogStyle));
        builder.setTitle("태그를 등록해주세요");
        builder.setView(et);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //등록 테스트
                        testInsert(database, String.valueOf(et.getText()));
//                        Toast.makeText(getActivity(),String.valueOf(et.getText())+" 등록됨",Toast.LENGTH_LONG).show();
                        showList(database);
                        //삭제 테스트
//                        testDelete(database);
                        refresh();
                    }
                });
        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"현재 " + String.valueOf(count(database)) + "개의 물건이 있습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    //DB에 데이터 삽입
    private void testInsert(SQLiteDatabase database, String name){
        database.execSQL("INSERT INTO registered_list VALUES('" + name + "','" + name + "')");
    }

    //DB 데이터 삭제
    private void testDelete(SQLiteDatabase database){
        database.execSQL("DELETE FROM registered_list");
        Toast.makeText(getActivity(),"ok",Toast.LENGTH_LONG).show();
    }


    //DB 데이터 개수 조회
    public int count(SQLiteDatabase database){
        int count = 0;
        String sql_reg = "select * from registered_list";
        Cursor cursor_reg = database.rawQuery(sql_reg, null);
        if(cursor_reg != null){
            count += cursor_reg.getCount(); //등록된 물품 개수얻기
        }
        return count;
    }


    protected void showList(SQLiteDatabase database){
        Cursor c = database.rawQuery("SELECT * FROM registered_list", null);
        try {
            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        //테이블에서 두개의 컬럼값을 가져와서
                        String ID = c.getString(c.getColumnIndex("ID"));
                        String NAME = c.getString(c.getColumnIndex("NAME"));
                        //HashMap에 넣고
                        HashMap<String,String> item = new HashMap<String,String>();
                        item.put("ID",ID);
                        item.put("NAME",NAME);
                        //ArrayList에 추가합니다..
                        itemList.add(item);
                    } while (c.moveToNext());
                }
            }
            database.close();
            //새로운 apapter를 생성하여 데이터를 넣은 후..
            adapter = new SimpleAdapter(
                    getActivity(), itemList, R.layout.list_item,
                    new String[]{"ID","NAME"},
                    new int[]{ R.id.id, R.id.name}
            );
            //화면에 보여주기 위해 Listview에 연결합니다.
            list.setAdapter(adapter);
        } catch (SQLiteException se) {
            Toast.makeText(getActivity(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }
}