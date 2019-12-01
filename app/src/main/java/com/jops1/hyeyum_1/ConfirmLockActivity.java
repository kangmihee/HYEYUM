package com.jops1.hyeyum_1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

public class ConfirmLockActivity extends AppCompatActivity {
    Button btn_pass;
    EditText input;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String pinNumCon;
    String email;

    RelativeLayout confirmlock;

    int[] img = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16, R.mipmap.back17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_lock);

        confirmlock = (RelativeLayout) findViewById(R.id.confirm_lock);
        Random ram = new Random();
        int num = ram.nextInt(img.length);
        confirmlock.setBackgroundResource(img[num]);

        //비밀번호 설정창에서 넘어온게 맞는지 확인
        Intent intent = getIntent();
        int lock = intent.getIntExtra("lock", 77);
        if (lock == 7) {
            pinNumCon = intent.getStringExtra("pinlock");
            email = intent.getStringExtra("email");
        }

        input = (EditText) findViewById(R.id.password_input_con);
        input.requestFocus();
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // 비밀번호 재확인
        btn_pass = (Button) findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinNum = input.getText().toString();

                if (pinNum.equals(pinNumCon)) {
                    pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putString("pinlock", pinNum);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("lock_num", 8);
                    intent.putExtra("con_email", email);
                    intent.putExtra("pinlock", pinNumCon);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "다시 한번 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        super.onStop();

        final String pinNumCon = input.getText().toString();
        pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = pref.edit();

        editor.putString("pinlock", pinNumCon);
        editor.commit();
    }
}
