package com.example.simdasoo.amata;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


//첫화면
public class InitialFragment extends Fragment {
    private final Query query = new Query(getActivity());
    int cntStuff;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private TextView tv;
    private ArrayList<String> mList;
    private ListView mListView;
    private ArrayAdapter mAdapter;
    private MainActivity mainActivity;

    // <bluetooth>

    // Debugging
    private static final String TAG = "Main";

    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothService btService = null;


    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    };

    // </bluetooth>

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

        //버튼
        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NfcRequestDialog.class);
                startActivity(intent);
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

        mainActivity = (MainActivity) this.getActivity();
        if(mainActivity.first_open){
            // 블루투스
            // BluetoothService 클래스 생성
            if(btService == null) {
                btService = new BluetoothService(this, mHandler);
            }

            btService.enableBluetooth();

            mainActivity.first_open = false;
        }

        return rootview;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //종료 시 DB와 연결 끊기
        database.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                // 아이템 클릭 이벤트 리스너 추가
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getActivity(), mList.get(position), Toast.LENGTH_SHORT).show();
                    }
                });
                mAdapter.notifyDataSetChanged();
            }
        } catch (SQLiteException se) {
            Toast.makeText(getActivity(),  se.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("",  se.getMessage());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            /** 추가된 부분 시작 **/
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    btService.getDeviceInfo(data);
                }
                break;
            /** 추가된 부분 끝 **/
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Next Step
                    btService.scanDevice();
                } else {

                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }

    public void showTagName(String id) {
        String tagID = id.substring(0,id.length()-2);
        Log.d("ID",tagID);
        String query = String.format("SELECT * FROM registered_list where id = '%s'",tagID);
        Log.d("Query",query);
        Cursor cursor = database.rawQuery(query, null);
        Log.d("cursor", String.valueOf(cursor.getCount()));
        if (cursor != null) {
            if(cursor.getCount()==0) Toast.makeText(getActivity(), "등록되지 않은 태그입니다.", Toast.LENGTH_SHORT).show();
            else if (cursor.moveToFirst()) {
                do {
                    //테이블에서 이름 가져오기
                    String NAME = cursor.getString(cursor.getColumnIndex("NAME"));
                    Toast.makeText(getActivity(), NAME, Toast.LENGTH_SHORT).show();
                } while (cursor.moveToNext());
            }
        }
    }

    public void changeInOut(String id) {
        String tagID = id.substring(0,id.length()-2);
        query.changeInOut(database, tagID);

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

        Log.d("inOutInfo", inOutInfo);
        if(inOutInfo.equals("I")) Toast.makeText(getActivity(), "현재위치 : 안", Toast.LENGTH_SHORT).show();
        else if(inOutInfo.equals("O")) Toast.makeText(getActivity(), "현재위치 : 밖", Toast.LENGTH_SHORT).show();
        inOutInfo = "";
    }
}