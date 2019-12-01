package com.jops1.hyeyum_1;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class Fragment_five extends Fragment {

    String email;
    View rootView;
    FragmentManager fragmentManager;
    Switch lock_setting, past_alram, question_alram;
    AlarmManager am;
    TextView change_lock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (View) inflater.inflate(R.layout.fragment_five, container, false);
        email = ((MainActivity) getActivity()).sendtoemail(getContext());
        TextView user_email = (TextView) rootView.findViewById(R.id.user_email);
        past_alram = (Switch) rootView.findViewById(R.id.past_switch);
        question_alram = (Switch) rootView.findViewById(R.id.question_switch);
        lock_setting = (Switch) rootView.findViewById(R.id.lock_lock);
        change_lock = (TextView) rootView.findViewById(R.id.txt_change);
        user_email.setText(email);

        //체크되어있는지 확인 한 후에 switchr값을 true로
        Boolean ch_tr = ((MainActivity) getActivity()).past_alarm(getContext());
        if (ch_tr == true) {
            past_alram.setChecked(true);
        }

        Boolean qu_tr = ((MainActivity) getActivity()).question_alarm(getContext());
        if (qu_tr == true) {
            question_alram.setChecked(true);
        }

        Boolean lo_tr = ((MainActivity) getActivity()).lock_alarm(getContext());
        if (lo_tr == true) {
            lock_setting.setChecked(true);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ImageView logout = (ImageView) rootView.findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity에 있는 로그아웃 메소드 호출
                ((MainActivity) getActivity()).logout_method(getContext());
            }
        });

        ImageView app_use = (ImageView) rootView.findViewById(R.id.app_use);
        app_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HY_Sangse.class);
                intent.putExtra("setting", 5);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        ImageView app_info = (ImageView) rootView.findViewById(R.id.app_info);
        app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                InformationSetting info = new InformationSetting();

                //fragment tag를 활용하여 화면전환
                Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_setting);
                if (curFragment.getTag() == "setting_main") {
                    fragmentTransaction.replace(R.id.fragment_setting, info, "info").commit();
                }
            }
        });

        past_alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (past_alram.isChecked() == true) {
                    SharedPreferences pref = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("past_checked", true);
                    editor.commit();
                    Toast toast = Toast.makeText(getContext(), "같은질문에 대한 과거의 답을 리스트로 확인 가능합니다. ", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (past_alram.isChecked() == false) {
                    SharedPreferences pref = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("past_checked");
                    editor.commit();
                }
            }
        });

        question_alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question_alram.isChecked()) {

                    SharedPreferences pref = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("question_checked", true);
                    editor.commit();
                    Toast.makeText(getContext(), "매일 오후 6시에 알림을 받으실 수 있습니다!", Toast.LENGTH_SHORT).show();

                    new AlarmHATT(getContext()).Alarm();

                } else if (question_alram.isChecked() == false) {
                    unregisterAlarm(getContext());
                    SharedPreferences pref = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("question_checked");
                    editor.commit();
                }
            }
        });

        lock_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lock_setting.isChecked() == true) {
                    SharedPreferences pref = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("lock_checked", true);
                    editor.commit();

                    Intent i = new Intent(getContext(), EnableLockActivity.class);
                    i.putExtra("email", email);
                    startActivity(i);

                } else if (lock_setting.isChecked() == false) {
                    SharedPreferences pref = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("lock_checked");
                    editor.remove("pinlock");
                    editor.commit();
                }
            }
        });

        change_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                String lock_check = pref.getString("pinlock", "");
                if (lock_check != null && !lock_check.equals("")) {
                    Intent intent = new Intent(getContext(), ChangeLockActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("pinlock", lock_check);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "설정된 잠금이 없습니다. 잠금을 설정해주세요!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    public static void unregisterAlarm(Context context) {
        Intent intent = new Intent();
        PendingIntent sender
                = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager manager =
                (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);

        manager.cancel(sender);
    }

    public class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        public void Alarm() {
            am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getContext(), BroadcastD.class);

            PendingIntent sender = PendingIntent.getBroadcast(getContext(), 0, intent, 0);

            Calendar calendar = Calendar.getInstance();

            //알람시간 calendar에 set해주기
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 18, 00, 0);

            //알람 예약
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);
        }
    }
}