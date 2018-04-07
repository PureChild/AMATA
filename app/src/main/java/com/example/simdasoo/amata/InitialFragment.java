package com.example.simdasoo.amata;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simdasoo.amata.R;


//첫화면
public class InitialFragment extends Fragment {
    //등록된 물품의 개수 - 추후 sql문으로 바꿔야함
    public int cntStuff = 0;

    public View getView(){
        View rootview = getLayoutInflater().inflate(R.layout.initial_fragment,null);
        return rootview;
    }
    public void refresh() {
//        getFragmentManager().beginTransaction().replace(R.id.frag_container, this).commit();
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

        //초기화면 등록된 물건이 없을 경우
        TextView tv = (TextView) rootview.findViewById(R.id.tv_main);
        if(cntStuff == 0){
//            tv.setText("test");
            tv.setText("If you want to register a item,\n please press the + button");
        }
        else {
//            tv.setText(" ");
            tv.setVisibility(View.GONE);
        }

        return rootview;
    }

    public void Dialog(){
        EditText et = new EditText(new ContextThemeWrapper(getActivity(), R.style.DialogEditTextStyle));
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyAlertDialogStyle));
        builder.setTitle("태그를 등록해주세요");
        builder.setView(et);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cntStuff++;
                        refresh();
                    }
                });
        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"Cancel 버튼 클릭됨",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
}