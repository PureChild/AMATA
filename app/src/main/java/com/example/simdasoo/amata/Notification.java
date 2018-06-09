package com.example.simdasoo.amata;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.TimerTask;

public class Notification extends TimerTask{
    InitialFragment initialFragment;

    // 생성할때 fragment를 전달 받음
    public Notification(InitialFragment initialFragment){
        this.initialFragment = initialFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    // 푸시알림 메소드가 타임 태스크로 동작하도록 설정
    @Override
    public void run() {
        initialFragment.judgement();
    }
}
