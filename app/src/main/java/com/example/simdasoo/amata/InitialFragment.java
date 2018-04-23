package com.example.simdasoo.amata;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


//첫화면
public class InitialFragment extends Fragment {
    private final Query query = new Query();
    int cntStuff;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private TextView tv;
    private ArrayList<String> mList;
    private ListView mListView;
    private ArrayAdapter mAdapter;

    public View getView(){
        View rootview = getLayoutInflater().inflate(R.layout.initial_fragment,null);
        return rootview;
    }
    public void refresh() {
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
        cntStuff = query.count(database);

        //초기화면 등록된 물건이 없을 경우
        tv = (TextView) rootview.findViewById(R.id.tv_main);
        if(cntStuff == 0){
//            tv.setText("test");
            tv.setText("+ 버튼을 눌러 기준이 될 태그를 등록해주세요.");
        }
        else {
            mListView= (ListView) rootview.findViewById(R.id.registered_list);
            showList(database);
//            tv.setText(str);
            tv.setVisibility(View.GONE);
        }

        return rootview;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //종료 시 DB와 연결 끊기
        database.close();
    }

    //등록하기 위한 dialog
    public void Dialog(){
        final EditText et = new EditText(new ContextThemeWrapper(getActivity(), R.style.DialogEditTextStyle));
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyAlertDialogStyle));
        builder.setTitle("태그를 등록해주세요");
        builder.setView(et);
        //ok 버튼 누르면
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //등록 테스트
//                        Toast.makeText(getActivity(),String.valueOf(et.getText())+" 등록됨",Toast.LENGTH_LONG).show();
                        query.testInsert(database, String.valueOf(et.getText()));
                        //삭제 테스트
//                        query.testDelete(database, getActivity());
                        refresh();
                    }
                });
        //cancel 버튼 누르면
        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"현재 " + String.valueOf(query.count(database)) + "개의 물건이 있습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

    protected void showList(SQLiteDatabase database){
        Cursor cursor = database.rawQuery("SELECT * FROM registered_list", null);
        mList = new ArrayList<String>();
        mAdapter =  new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mList);
        try {
            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        //테이블에서 이름 가져오기
                        String NAME = cursor.getString(cursor.getColumnIndex("NAME"));
                        mList.add(NAME);
//                        Log.d("testing", str);
                    } while (cursor.moveToNext());
                }
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        } catch (SQLiteException se) {
            Toast.makeText(getActivity(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }
}