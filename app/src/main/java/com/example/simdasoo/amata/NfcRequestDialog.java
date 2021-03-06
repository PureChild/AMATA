package com.example.simdasoo.amata;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;

//다이얼로그 역할
public class NfcRequestDialog extends Activity {
    private final Query query = new Query(this);

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private EditText et;
    private String tagUid;
    private String tagName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_request);
        et = (EditText) findViewById(R.id.nameValue);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        this.setFinishOnTouchOutside(false);

        //DB 연결
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        //버튼 이벤트 등록
        Button btnOk = findViewById(R.id.btnDelete);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et.getHint() == null){
                    Toast.makeText(getApplicationContext(),"태그를 등록해주세요",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (et.getText().toString() == null || et.getText().toString().equals("")) {
                        Log.d("isNull?", "true");
                        tagUid = String.valueOf(et.getHint());
                        tagName = String.valueOf(et.getHint());
                    } else {
                        Log.d("isNull?", "false");
                        tagUid = String.valueOf(et.getHint());
                        tagName = String.valueOf(et.getText());
                    }
                    if(query.existMain(database)) {
                        query.addStuff(database, tagUid, tagName);
                    }
                    else {
                        database.execSQL("INSERT INTO main(ID, NAME) VALUES('" + tagUid + "','" + tagName + "')");
                    }

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        });

        Button btnCancel = findViewById(R.id.btnModify);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"현재 " + String.valueOf(query.count(database)) + "개의 물건이 있습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
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
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            et.setHint(hex2uid(bin2hex(tagId)));
            et.setCursorVisible(true);
            et.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"현재 " + String.valueOf(query.count(database)) + "개의 물건이 있습니다.",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    private String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    private String hex2uid(String hex) {
        String[] dec = new String[4];
        int index = 0;
        for(int i = 0; i <= hex.length()-2; i = i+2) {
            dec[index] = String.valueOf(Long.parseLong(hex.substring(i, i + 2), 16));
            index++;
        }
        String id = dec[0] + " " + dec[1] + " " + dec[2] + " " + dec[3];
        return id;
    }
}
