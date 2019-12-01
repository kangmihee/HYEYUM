package com.jops1.hyeyum_1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentList_One extends Fragment {
    View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment curFragment = fragmentManager.findFragmentById(R.id.test);
        Fragment_one one = new Fragment_one();

        //새로 호출될 때마다 tag값 체크해서 항상 fragment_one으로 보이게하기
        if (curFragment == null || curFragment.getTag() == "main") {
            fragmentTransaction.add(R.id.test, one, "main").commit();
            rootview = inflater.inflate(R.layout.fragmentlist_one, container, false);
        }
        return rootview;
    }
}