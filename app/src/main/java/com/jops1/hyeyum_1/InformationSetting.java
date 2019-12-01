package com.jops1.hyeyum_1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//앱 정보
public class InformationSetting extends Fragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.information_setting, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ImageView cancel = (ImageView) rootView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment_five five = new Fragment_five();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_setting);

                if (curFragment.getTag() == "info") {
                    fragmentTransaction.replace(R.id.fragment_setting, five, "setting_main").commit();
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}