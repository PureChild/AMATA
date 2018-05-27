package com.example.simdasoo.amata;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChecklistFragment extends Fragment {
    private final Query query = new Query(getActivity());
    private ArrayList<String> cList;
    private ArrayAdapter<String> cAdapter;
    private ListView cListView;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //추가
        View rootview = inflater.inflate(R.layout.checklist_fragment, null);
        cListView= (ListView) rootview.findViewById(R.id.necessary_list);
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.getWritableDatabase();
        showList(database);

        return rootview;
    }
    protected void showList(final SQLiteDatabase database){
        Cursor mainTag = database.rawQuery("SELECT * FROM main", null);
        Cursor cursor = database.rawQuery("SELECT * FROM registered_list", null);
        cList = new ArrayList<String>();
        cAdapter =  new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, cList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                if(position==0) {
                    CheckedTextView tv = (CheckedTextView) view.findViewById(android.R.id.text1);
                    tv.setText("기준 : " + tv.getText());
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        try {
            if(mainTag != null) {
                if(mainTag.moveToFirst()){
                    do {
                        String NAME = mainTag.getString(mainTag.getColumnIndex("NAME"));
                        cList.add(0,NAME);
                    } while (mainTag.moveToNext());
                }
            }
            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다.
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        //테이블에서 이름 가져오기
                        String NAME = cursor.getString(cursor.getColumnIndex("NAME"));
                        cList.add(NAME);
                    } while (cursor.moveToNext());
                }
                cListView.setAdapter(cAdapter);
                cListView.setItemChecked(0, true);
                // 체크 상태 유지
                for(int i = 0; i < cList.size(); i++){
                    String tagID = query.findValue(database,"registered_list","NAME",cList.get(i),"ID");
                    String checkValue = query.findValue(database,"check_info", "ID", tagID,"CHECK_VALUE");
                    if(checkValue.equals("Y")){
                        cListView.setItemChecked(i,true);
                    }
                }
                cListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Toast.makeText(getActivity(), cList.get(position), Toast.LENGTH_SHORT).show();
                        if(position==0) {
                            cListView.setItemChecked(0, true);
                        }
                        else {
                            String name = cList.get(position);
//                            Toast.makeText(getActivity(), query.changeCheckInfo(database, name), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cAdapter.notifyDataSetChanged();
            }
        } catch (SQLiteException se) {
            Toast.makeText(getActivity(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }
}