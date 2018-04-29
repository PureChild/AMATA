package com.example.simdasoo.amata;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;

//다이얼로그 역할
public class NfcRequestDialog extends Activity {
    private final Query query = new Query();

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_request);
        et = (EditText) findViewById(R.id.tagDesc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        this.setFinishOnTouchOutside(false);

        //DB 연결
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        //버튼 이벤트 등록
        Button btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //등록 테스트
//                Toast.makeText(getApplicationContext(),String.valueOf(et.getText())+" 등록됨",Toast.LENGTH_SHORT).show();
                query.testInsert(database, String.valueOf(et.getText()));
                //삭제 테스트
//                query.testDelete(database, getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"현재 " + String.valueOf(query.count(database)) + "개의 물건이 있습니다.",Toast.LENGTH_SHORT).show();
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
