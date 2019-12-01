package com.jops1.hyeyum_1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentList_Two extends Fragment {

    ArrayList<TaData> data;
    View rootview;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_talist);
        Fragment_two two = new Fragment_two();

        //새로 호출될 때마다 tag값 체크해서 항상 fragment_two로 보이게
        if (curFragment == null) {
            fragmentTransaction.add(R.id.fragment_talist, two, "ta_list").commit();
            rootview = inflater.inflate(R.layout.fragmentlist_two, container, false);
        } else if (curFragment.getTag() != "ta_list") {
            fragmentTransaction.replace(R.id.fragment_talist, two, "ta_list").commit();
            rootview = inflater.inflate(R.layout.fragmentlist_two, container, false);
        } //end if
        return rootview;
    }
}