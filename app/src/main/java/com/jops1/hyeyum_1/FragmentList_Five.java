package com.jops1.hyeyum_1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentList_Five extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_setting);
        Fragment_five five = new Fragment_five();

        //새로 호출될 때마다 tag값 체크해서 항상 fragment_five로 보이게하기
        if (curFragment == null) {
            fragmentTransaction.add(R.id.fragment_setting, five, "setting_main").commit();
            view = inflater.inflate(R.layout.fragmentlist_five, container, false);
        } else if (curFragment.getTag() != "setting_main") {
            fragmentTransaction.replace(R.id.fragment_setting, five, "setting_main").commit();
            view = inflater.inflate(R.layout.fragmentlist_five, container, false);
        } //end if

        return view;
    }
}