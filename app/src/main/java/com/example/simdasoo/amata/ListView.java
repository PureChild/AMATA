package com.example.simdasoo.amata;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ListView extends ListActivity {
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private EditText inputText;
    private Button inputButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_main); //해당 아이디에 자신이 만든 레이아웃의 이름을 쓴다

        inputText = (EditText)findViewById(R.id.inputText);
        inputButton = (Button)findViewById(R.id.inputButton);
        list = new ArrayList<String>();

        inputButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                list.add(inputText.getText().toString());
                inputText.setText("");
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);

        setListAdapter(adapter);
    }
}