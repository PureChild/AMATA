package com.example.simdasoo.amata;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ItemInfoDialog extends Activity {
    private final Query query = new Query(this);

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private EditText et;
    private String beforeName;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_info);
        et = (EditText) findViewById(R.id.nameValue);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        final Intent getIntent = getIntent();
        position = getIntent.getIntExtra("position", -1);
        beforeName = getIntent.getStringExtra("beforeName");
        final Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        this.setFinishOnTouchOutside(false);

        //DB 연결
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        //버튼 이벤트 등록
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"삭제버튼",Toast.LENGTH_SHORT).show();
                if(position != 0) query.deleteItme(database, beforeName);
                else database.execSQL("DELETE FROM main");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        Button btnModify = findViewById(R.id.btnModify);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"수정버튼",Toast.LENGTH_SHORT).show();
                if(et.getText().toString() == null || et.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"수정할 이름을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
                else {
                    String newName = String.valueOf(et.getText());
                    query.modifyItem(database, beforeName, newName);
                }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
            }
        });
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
        overridePendingTransition(0,0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}