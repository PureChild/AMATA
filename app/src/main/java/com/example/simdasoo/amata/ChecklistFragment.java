package com.example.simdasoo.amata;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.CharacterPickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class ChecklistFragment extends Fragment {
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private EditText inputText;
    private Button inputButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //추가
        View rootview = inflater.inflate(R.layout.checklist_fragment, container, false);
        CheckBox checkboxvariable=(CheckBox)rootview.findViewById(R.id.checkBox);

        checkboxvariable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
//                    Toast.makeText(getActivity(),"Hi",Toast.LENGTH_LONG).show();
                } else {
                    //do this
                }
            }
        });
                return rootview;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.checklist_fragment); //해당 아이디에 자신이 만든 레이아웃의 이름을 쓴다
//
//        inputText = (EditText)findViewById(R.id.inputText);
//        inputButton = (Button)findViewById(R.id.inputButton);
//        list = new ArrayList<String>();
//
//        inputButton.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                list.add(inputText.getText().toString());
//                inputText.setText("");
//                adapter.notifyDataSetChanged();
//            }
//        });
//
//        adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, list);
//
//        setListAdapter(adapter);
//    }



}