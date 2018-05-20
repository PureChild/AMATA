package com.example.simdasoo.amata;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
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
    public static SQLiteDatabase database;
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
        Log.wtf("","****************create***************");
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
            tv.setText("+ 버튼을 눌러 태그를 등록해주세요. \n 기준이 없을 경우 기준으로 등록됩니다.");
        }
        else {
            mListView= (ListView) rootview.findViewById(R.id.stuff_list);
            showList(database);
            tv.setVisibility(View.GONE);
        }

        mainActivity = (MainActivity) this.getActivity();
        if(mainActivity.first_open){
            // 블루투스
            // BluetoothService 클래스 생성
            btService = new BluetoothService(this, mHandler);
            Log.wtf("","****************btService***************");

            btService.enableBluetooth();

            mainActivity.first_open = false;
        }
        else {
            btService = new BluetoothService(this, mHandler);
            Log.wtf("","****************btService***************");
        }

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //종료 시 DB와 연결 끊기
//        database.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected void showList(SQLiteDatabase database){
        Cursor mainTag = database.rawQuery("SELECT * FROM main", null);
        Cursor cursor = database.rawQuery("SELECT * FROM registered_list", null);
        mList = new ArrayList<String>();
        mAdapter =  new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                if(position==0){
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
                        mList.add(0,NAME);
                    } while (mainTag.moveToNext());
                }
                else mList.add(0,"없음");
            }
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
                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), ItemInfoDialog.class);
                        intent.putExtra("position",position);
                        intent.putExtra("beforeName",mList.get(position));
                        startActivity(intent);
                        return true;
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
        String sql = "";
        String tagName = "";
        Log.d("ID",tagID);
        if(query.isItMain(database,tagID)) {
            tagName = query.findValue(database, "main", "ID", tagID, "NAME");
        }
        else{
            tagName = query.findValue(database, "registered_list", "ID", tagID, "NAME");
        }
        if(tagName!="") {
            Toast.makeText(mainActivity, tagName, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(mainActivity, "등록되지 않은 태그입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeInOut(String id) {
        String tagID = id.substring(0,id.length()-2);
        String inOutInfo = "";
        if(query.isItMain(database,tagID)) {
            query.changeInOut(database, tagID, "main");
            inOutInfo = query.findValue(database, "main", "ID", tagID, "IN_OUT");
            if(inOutInfo.equals("I")) Toast.makeText(mainActivity, "현재위치 : 안", Toast.LENGTH_SHORT).show();
            else if(inOutInfo.equals("O")) Toast.makeText(mainActivity, "현재위치 : 밖", Toast.LENGTH_SHORT).show();
            judgement();
        }
        else {
            query.changeInOut(database, tagID, "inout_info");
            inOutInfo = query.findValue(database, "inout_info", "ID", tagID, "IN_OUT");
            if(inOutInfo.equals("I")) Toast.makeText(mainActivity, "현재위치 : 안", Toast.LENGTH_SHORT).show();
            else if(inOutInfo.equals("O")) Toast.makeText(mainActivity, "현재위치 : 밖", Toast.LENGTH_SHORT).show();
        }
        Log.d("inOutInfo", inOutInfo);

        inOutInfo = "";
    }

    public void judgement() {
        /*
        * 1. main의 현재 위치 상태 받아옴 - String
        * 2. check된 물건들 id 배열에 저장 - arraylist
        * 3. 해당 id로 in/out 정보 검색 - arraylist
        * 4. main이랑 위치가 다른 인덱스 저장 - arraylist
        * 5. 이름 얻기 - arraylist
        * */

        // 1
        String mainIoStat = "";
        String sql_main = "select * from main";

        Cursor cursor_main = database.rawQuery(sql_main, null);

        if (cursor_main != null){
            if(cursor_main.getCount()==0) ;
            else if (cursor_main.moveToFirst()) {
                do {
                    mainIoStat = cursor_main.getString(cursor_main.getColumnIndex("IN_OUT"));
                } while (cursor_main.moveToNext());
            }
        }
        Log.d("mainIoStat", String.valueOf(mainIoStat));

        // 2
        String sql_check = "select * from check_info";
        ArrayList<String> checkedID = new ArrayList<>();

        Cursor cursor = database.rawQuery(sql_check, null);

        if (cursor != null){
            if(cursor.getCount()==0) ;
            else if (cursor.moveToFirst()) {
                do {
                    String checkStat = cursor.getString(cursor.getColumnIndex("CHECK_VALUE"));
                    if(checkStat.equals("Y"))
                        checkedID.add(cursor.getString(cursor.getColumnIndex("ID")));
                } while (cursor.moveToNext());
            }
        }
        Log.d("checkedID", String.valueOf(checkedID));

        // 3
        ArrayList<String> IoInfo = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();
        for(int i = 0; i < checkedID.size(); i++){
            IoInfo.add(query.findValue(database,"inout_info","ID",checkedID.get(i),"IN_OUT"));
            index.add(i);
        }
        Log.d("IoInfo", String.valueOf(IoInfo));
        Log.d("index", String.valueOf(index));

        // 4 & 5
        ArrayList<String> missItem = new ArrayList<>();
        for(int i = 0; i < IoInfo.size(); i++){
            if(!IoInfo.get(i).equals(mainIoStat))
            missItem.add(query.findValue(database,"registered_list","ID",checkedID.get(index.get(i)),"NAME"));
        }
        Log.d("missItem", String.valueOf(missItem));

        // 알림
        if(!missItem.isEmpty())
            Toast.makeText(mainActivity, missItem + "챙기셨나요", Toast.LENGTH_SHORT).show();

        missItem = new ArrayList<>();
    }
}