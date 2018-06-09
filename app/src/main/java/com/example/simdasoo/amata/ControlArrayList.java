package com.example.simdasoo.amata;

import java.util.ArrayList;

// 넘어온 아이디 값을 제어할 클래스
public class ControlArrayList {

    // 중복 제거
    public ArrayList<String> singularization(ArrayList<String> arrayList){
        ArrayList<String> resultList = new ArrayList<String>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (!resultList.contains(arrayList.get(i))) {
                resultList.add(arrayList.get(i));
            }
        }
        return resultList;
    }

    // 기준 태그 찾기
    // 기준 태그 얻기

}
