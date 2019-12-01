package com.jops1.hyeyum_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

//상세설명페이지_여섯번째화면
public class HY_SS_SixthFragment extends Fragment {

    int five_fragment;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.hy_ss_sixth_fragment, container, false);

        five_fragment = HY_Sangse.setting;
        email = HY_Sangse.email;

        // 설정화면에서 사용법을 봤을 때
        if (five_fragment == 5 && !email.equals("")) {
            ImageButton start = (ImageButton) rootView.findViewById(R.id.btn_start);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    int sendnumber = 4;
                    intent.putExtra("five", sendnumber);
                    intent.putExtra("five_email", email);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

        } else if (five_fragment != 5) { //앱을 처음 사용한 사용자일 경우, 로그인 창으로
            rootView.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), HY_Login.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        } //end if
        return rootView;
    }
}