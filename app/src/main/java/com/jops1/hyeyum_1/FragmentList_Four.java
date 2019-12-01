package com.jops1.hyeyum_1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentList_Four extends Fragment {

    ArrayList<MyData> data;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
        Fragment_four four = new Fragment_four();

        //새로 호출될 때마다 tag값 체크해서 항상 fragment_four로 보이게 하기
        if (curFragment == null) {
            fragmentTransaction.add(R.id.frgment_mylist, four, "my_list").commit();
            rootView = inflater.inflate(R.layout.fragmentlist_four, container, false);
        } else if (curFragment.getTag() == "my_list" || curFragment.getTag() == "my_answer" || curFragment.getTag() == "my_update") {
            fragmentTransaction.replace(R.id.frgment_mylist, four, "my_list").commit();
            rootView = inflater.inflate(R.layout.fragmentlist_four, container, false);
        } //end if

        return rootView;
    }
}