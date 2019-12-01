package com.jops1.hyeyum_1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ViewPager myViewPager;
    private TabLayout tabLayout;
    private int[] IconResID = {
            R.drawable.selector_one,
            R.drawable.selector_two,
            R.drawable.selector_three,
            R.drawable.selector_four,
            R.drawable.selector_five};
    static String email;

    LinearLayout mainback;
    InputMethodManager imm;
    List<Fragment> fragmentList;
    MyFragmentAdapter myFragmentAdapter;

    int[] mainimg = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        //글꼴 적용
        FontClass.setDefaultFont(this, "MONOSPACE", "DX시인과나.ttf");

        //배경랜덤
        mainback = (LinearLayout) findViewById(R.id.mainlayout);
        Random ram = new Random();
        int num = ram.nextInt(mainimg.length);
        mainback.setBackgroundResource(mainimg[num]);

        myViewPager = (ViewPager) findViewById(R.id.myViewPager);
        myViewPager.setOffscreenPageLimit(4); //화면미리 생성해주기
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        setViewPager();
        tabLayout.setupWithViewPager(myViewPager); //viewpager안에 tablayout달기
        setTabLayoutIcon();


        //어디에서 넘어온 값들인지 체크하여 이전화면으로 나타내주기
        Intent ss_intent = getIntent();
        int email_1 = ss_intent.getIntExtra("garvege", 1);
        int five = ss_intent.getIntExtra("five", 55);
        int lock_num = ss_intent.getIntExtra("lock_num", 88);
        int lock_test = ss_intent.getIntExtra("lock_number", 99);
        int alarm_email = ss_intent.getIntExtra("alarm", 22);

        if (email_1 == 0) { //이미 로그인을 한 사용자일 경우
            Intent intent_spl = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent_spl);
            email = ss_intent.getStringExtra("email_1");
        } else if (email_1 != 0 && five != 4 && lock_test != 9 && lock_num != 8 && alarm_email != 2) {
            //처음 로그인한 사용자일 경우
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
        } else if (five == 4) { //상세설명을 보고 난 후에 온 사용자일 경우
            email = ss_intent.getStringExtra("five_email");
            pageChange(4);
        } else if (lock_test == 9) { //잠금설정 후, 입력하고 들어온 사용자일 경우
            email = ss_intent.getStringExtra("test_email");
        } else if (lock_num == 8) { //잠금을 설정하고 온 사용자일 경우
            email = ss_intent.getStringExtra("con_email");
            pageChange(4);
        } else if (alarm_email == 2) { //상단 알람을 통해서 들어온 사용자일 경우
            SharedPreferences pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
            String pinnumber = pref.getString("pinlock", "");
            String auto_eamil = pref.getString("email", "");
            Intent intent_spl = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent_spl);

            if (pinnumber != null && !pinnumber.equals("")) {

            } else if (pinnumber == null || pinnumber.equals("")) {
                email = auto_eamil; //잠금을 설정하지 않았을 경우
            }
        } //end if

        if (!NetworkConnected(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("네트워크가 연결되어 있지 않습니다. 네트워크를 확인하신 후 다시 실행해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        myViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pageReload(); //화면이 변환 될 때마다 화면을 새로고침해줌
                setTabLayoutIcon(); //tab이 사라지는 경우를 대비해서 tab도 다시 호출해줌
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onRestart() {
        //화면이 다시 시작되면 잠금화면 호출하기
        lock_intent(this);
        super.onRestart();
    }

    public void lock_intent(Context context) {
        SharedPreferences pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String test = pref.getString("pinlock", "");
        String auto_eamil = pref.getString("email", "");

        //잠금 설정시에만 잠금화면으로 넘기기
        if (!test.equals("") && test != null) {
            Intent intent = new Intent(getApplicationContext(), CheckLockActivity.class);
            intent.putExtra("pinlock", test);
            email = auto_eamil;
            intent.putExtra("pin_email", email);
            startActivity(intent);
        }
    }

    //네트워크 연결 확인하기
    public boolean NetworkConnected(Context context) {
        boolean isConnected = false;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()) {
            isConnected = true;
        } else {
            isConnected = false;
        }
        return isConnected;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //앱 종료
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("앱종료")
                        .setMessage("앱을 종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        }).setNegativeButton("아니오", null).show();
                return false;
            default:
                return false;
        }
    }

    protected InputMethodManager sendtoimm(Context context) {
        return imm;
    }

    //각각의 fragment에 email 넘겨주기 위한 함수
    protected String sendtoemail(Context context) {
        return email;
    }

    //설정해놓은 값들이 설정화면에 나타날 수 있게 설정하기
    protected boolean past_alarm(Context context) {
        SharedPreferences pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        Boolean test = pref.getBoolean("past_checked", false);
        Boolean ch_tr = true;

        if (test == true) {
            ch_tr = test;
        } else if (test == false) {
            ch_tr = test;
        }
        return ch_tr;
    }

    protected boolean lock_alarm(Context context) {
        SharedPreferences pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        Boolean test = pref.getBoolean("lock_checked", false);
        String pinnumber = pref.getString("pinlock", "");
        Boolean lo_tr = true;

        if (test == true && !pinnumber.equals("") && pinnumber != null) {
            lo_tr = test;
        } else {
            lo_tr = false;
        }
        return lo_tr;
    }

    protected boolean question_alarm(Context context) {
        SharedPreferences pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        Boolean test = pref.getBoolean("question_checked", false);
        Boolean qu_tr = true;
        if (test == true) {
            qu_tr = test;
        } else if (test == false) {
            qu_tr = test;
        }
        return qu_tr;
    }

    //로그아웃버튼 클릭 시, 저장된 모든 값들을 지우기
    protected void logout_method(Context context) {
        Intent intent = new Intent(MainActivity.this, HY_Login.class);
        startActivity(intent);

        SharedPreferences pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(MainActivity.this, "로그아웃.", Toast.LENGTH_SHORT).show();
        finish();
    }

    //tab에 아이콘 설정하기
    public void setTabLayoutIcon() {
        for (int i = 0; i < IconResID.length; i++) {
            tabLayout.getTabAt(i).setIcon(IconResID[i]);
        }
    }

    private void setViewPager() {
        FragmentList_One myFragment1 = new FragmentList_One();
        FragmentList_Two myFragment2 = new FragmentList_Two();
        FragmentList_Three myFragment3 = new FragmentList_Three();
        FragmentList_Four myFragment4 = new FragmentList_Four();
        FragmentList_Five myFragment5 = new FragmentList_Five();

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(myFragment1);
        fragmentList.add(myFragment2);
        fragmentList.add(myFragment3);
        fragmentList.add(myFragment4);
        fragmentList.add(myFragment5); //각각의 fragment add시켜주기

        myFragmentAdapter
                = new MyFragmentAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myFragmentAdapter);
    }

    //page의 번호를 활용하여 원하는 화면을 볼 수 있는 함수
    public void pageChange(int page) {
        myViewPager.setCurrentItem(page, true);
    }

    //화면을 새로고쳐주는 함수
    public void pageReload() {
        myFragmentAdapter.notifyDataSetChanged();
    }

    //FragmentStatePagerAdapter를 customizing
    class MyFragmentAdapter extends FragmentStatePagerAdapter {
        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}