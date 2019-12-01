package com.jops1.hyeyum_1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.Button;

public class HY_Sangse extends FragmentActivity {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;

    Button bt1, bt2, bt3, bt4, bt5, bt6;
    SharedPreferences pref;
    static int setting;
    static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hy__sangse);

        pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        //설정창을 통해서 다시 들어온 경우
        Intent setting_inent = getIntent();
        setting = setting_inent.getIntExtra("setting", 55);
        if (setting == 5) {
            email = setting_inent.getStringExtra("email");
        }

        //앱을 처음 사용하는 사용자인 경우
        String email = pref.getString("email", "");
        if (email == null || email.equals("")) {
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent);

        } else if (!email.equals("") && setting == 55) { //이미 회원가입을 한 사용자인 경우
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("garvege", 0);
            intent.putExtra("email_1", email);
            startActivity(intent);
        } //end if

        // 상세설명펭지 뷰페이저 설정
        bt1 = (Button) findViewById(R.id.btn1);
        bt2 = (Button) findViewById(R.id.btn2);
        bt3 = (Button) findViewById(R.id.btn3);
        bt4 = (Button) findViewById(R.id.btn4);
        bt5 = (Button) findViewById(R.id.btn5);
        bt6 = (Button) findViewById(R.id.btn6);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int position) {
                btnAction(position);

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void btnAction(int action) {
        switch (action) {
            case 0:
                setButton(bt1, 4, 15, R.drawable.circle2);
                setButton(bt2, 4, 15, R.drawable.circle);
                setButton(bt3, 4, 15, R.drawable.circle);
                setButton(bt4, 4, 15, R.drawable.circle);
                setButton(bt5, 4, 15, R.drawable.circle);
                setButton(bt6, 4, 15, R.drawable.circle);
                break;

            case 1:
                setButton(bt1, 4, 15, R.drawable.circle);
                setButton(bt2, 4, 15, R.drawable.circle2);
                setButton(bt3, 4, 15, R.drawable.circle);
                setButton(bt4, 4, 15, R.drawable.circle);
                setButton(bt5, 4, 15, R.drawable.circle);
                setButton(bt6, 4, 15, R.drawable.circle);
                break;

            case 2:
                setButton(bt1, 4, 15, R.drawable.circle);
                setButton(bt2, 4, 15, R.drawable.circle);
                setButton(bt3, 4, 15, R.drawable.circle2);
                setButton(bt4, 4, 15, R.drawable.circle);
                setButton(bt5, 4, 15, R.drawable.circle);
                setButton(bt6, 4, 15, R.drawable.circle);
                break;

            case 3:
                setButton(bt1, 4, 15, R.drawable.circle);
                setButton(bt2, 4, 15, R.drawable.circle);
                setButton(bt3, 4, 15, R.drawable.circle);
                setButton(bt4, 4, 15, R.drawable.circle2);
                setButton(bt5, 4, 15, R.drawable.circle);
                setButton(bt6, 4, 15, R.drawable.circle);
                break;

            case 4:
                setButton(bt1, 4, 15, R.drawable.circle);
                setButton(bt2, 4, 15, R.drawable.circle);
                setButton(bt3, 4, 15, R.drawable.circle);
                setButton(bt4, 4, 15, R.drawable.circle);
                setButton(bt5, 4, 15, R.drawable.circle2);
                setButton(bt6, 4, 15, R.drawable.circle);
                break;

            case 5:
                setButton(bt1, 4, 15, R.drawable.circle);
                setButton(bt2, 4, 15, R.drawable.circle);
                setButton(bt3, 4, 15, R.drawable.circle);
                setButton(bt4, 4, 15, R.drawable.circle);
                setButton(bt5, 4, 15, R.drawable.circle);
                setButton(bt6, 4, 15, R.drawable.circle2);
                break;
        }
    }

    private void setButton(Button btn, int w, int h, int c) {
        btn.setWidth(w);
        btn.setHeight(h);
        btn.setBackgroundResource(c);
    }
}