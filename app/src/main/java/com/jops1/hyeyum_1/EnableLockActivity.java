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

public class EnableLockActivity extends AppCompatActivity {

    Button btn_pass;
    EditText input;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    RelativeLayout enablelockback;

    int[] img = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16, R.mipmap.back17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_lock);

        enablelockback = (RelativeLayout) findViewById(R.id.enable_lock);
        Random ram = new Random();
        int num = ram.nextInt(img.length);
        enablelockback.setBackgroundResource(img[num]);

        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");

        input = (EditText) findViewById(R.id.password_input);
        input.requestFocus();

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //숫자 4자리로 비밀번호 설정
        btn_pass = (Button) findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.password_input);

                String pinNum = input.getText().toString();
                if (pinNum.equals("")) {
                    Toast.makeText(getApplicationContext(), "빈칸은 싫어요!", Toast.LENGTH_SHORT).show();
                } else if (pinNum.length() != 4) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 4자리로 설정해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), ConfirmLockActivity.class);
                    intent.putExtra("lock", 7);
                    intent.putExtra("pinlock", pinNum);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        String pinNum = input.getText().toString();
        pref = getSharedPreferences("lock", Activity.MODE_PRIVATE);
        editor = pref.edit();

        editor.putString("pinlock", pinNum);
        editor.commit();
    }

    @Override
    public void onBackPressed() {

    }
}
