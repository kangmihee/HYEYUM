package com.jops1.hyeyum_1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

public class CheckLockActivity extends AppCompatActivity {
    Button btn_pass;
    EditText input;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String pinNumCheck;
    String email;

    RelativeLayout checklockback;

    int[] img = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16, R.mipmap.back17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_lock);

        checklockback = (RelativeLayout) findViewById(R.id.check_lock);
        Random ram = new Random();
        int num = ram.nextInt(img.length);
        checklockback.setBackgroundResource(img[num]);

        Intent intent = getIntent();
        pinNumCheck = intent.getStringExtra("pinlock");
        email = intent.getStringExtra("pin_email");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        //키보드 숫자타입만 나타내기
        input = (EditText) findViewById(R.id.password_input);
        input.requestFocus();
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // 비밀번호 설정
        btn_pass = (Button) findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pinNumCon = input.getText().toString();

                if (pinNumCon.equals(pinNumCheck)) {
                    pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    editor = pref.edit();
                    editor.putString("pinlock", pinNumCheck);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("lock_number", 9);
                    intent.putExtra("test_email", email);
                    startActivity(intent);
                    finish();
                } else if (pinNumCon.equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "다시 한번 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        super.onStop();

        //설정된 비밀번호 저장하기
        final String pinNumCheck = input.getText().toString();
        pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("pinlock", pinNumCheck);
        editor.commit();
    }
}
