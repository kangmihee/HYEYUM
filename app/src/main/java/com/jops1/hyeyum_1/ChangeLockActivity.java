package com.jops1.hyeyum_1;

import android.content.Intent;
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

public class ChangeLockActivity extends AppCompatActivity {

    RelativeLayout change_layout;
    String pinNumCheck;
    String email;
    Button btn_ok;
    EditText ed_input;

    int[] img = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16, R.mipmap.back17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_lock);

        change_layout = (RelativeLayout) findViewById(R.id.change_lock);
        Random ram = new Random();
        int num = ram.nextInt(img.length);
        change_layout.setBackgroundResource(img[num]);

        //넘어온 값 확인하기
        Intent intent = getIntent();
        pinNumCheck = intent.getStringExtra("pinlock");
        email = intent.getStringExtra("email");

        ed_input = (EditText) findViewById(R.id.change_input);
        ed_input.requestFocus();

        ed_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        ed_input.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // 비밀번호 변경
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ed_input = (EditText) findViewById(R.id.change_input);
                String pinNum = ed_input.getText().toString();
                if (pinNum.equals("") || pinNum.length() != 4 || !pinNumCheck.equals(pinNum)) {
                    Toast.makeText(getApplicationContext(), "설정된 비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
                } else if (pinNumCheck.equals(pinNum)) {
                    Intent intent = new Intent(getApplicationContext(), EnableLockActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    //Back버튼 무력화
    public void onBackPressed() {
    }
}
