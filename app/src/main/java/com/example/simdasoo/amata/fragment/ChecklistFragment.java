package com.example.simdasoo.amata.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.simdasoo.amata.R;
import com.example.simdasoo.amata.contents.DBHelper;

import java.util.ArrayList;

public class ChecklistFragment extends Fragment {
    private ArrayList<String> cList;
    private ArrayAdapter<String> cAdapter;
    private CheckedTextView checkedTextView;
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
    protected void showList(SQLiteDatabase database){
        Cursor cursor = database.rawQuery("SELECT * FROM registered_list", null);
        cList = new ArrayList<String>();
        cAdapter =  new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_multiple_choice, cList);
        try {
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
                cAdapter.notifyDataSetChanged();
            }
        } catch (SQLiteException se) {
            Toast.makeText(getActivity(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }
}