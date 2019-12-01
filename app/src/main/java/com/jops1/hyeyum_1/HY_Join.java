package com.jops1.hyeyum_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

public class HY_Join extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPassword_again;
    private Button join_btn;
    final int REQUEST_CODE = 1;

    String email;
    RelativeLayout joinback;

    int[] img = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16, R.mipmap.back17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hy_join);
        editTextEmail = (EditText) findViewById(R.id.edit_email);
        editTextPassword = (EditText) findViewById(R.id.edit_pwd);
        editTextPassword_again = (EditText) findViewById(R.id.edit_pwdcfm);
        join_btn = (Button) findViewById(R.id.btn_joincfm);

        joinback = (RelativeLayout) findViewById(R.id.activity_hy_join);
        Random ram = new Random();  //배경 이미지 랜덤
        int num = ram.nextInt(img.length);
        joinback.setBackgroundResource(img[num]);

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String password_again = editTextPassword_again.getText().toString();

                //이메일 형식인지 확인하고 빈값이 아닌 상태에서 회원가입 메소트 호출
                if (password.equals(password_again) && email.contains("@") && !password.equals("") && !password_again.equals("")) {
                    editTextEmail.setText("");
                    editTextPassword.setText("");
                    editTextPassword_again.setText("");
                    insertToDatabase(email, password); //DB에 저장하는 함수 호출

                } else if (email.equals("") || password.equals("") || password_again.equals("")) { //빈칸이 있을 경우
                    Toast.makeText(getApplicationContext(), "빈칸없이 입력해주세요!.", Toast.LENGTH_SHORT).show();

                } else if (!password.equals(password_again) && email.contains("@")) { //비밀번호가 서로 일치하지 않을 경우
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    editTextPassword.setText("");
                    editTextPassword_again.setText("");

                } else if (password.equals(password_again) && !email.equals("%@%")) { //이메일 형식이 아닐 경우
                    Toast.makeText(getApplicationContext(), "이메일 형식으로 입력해주세요", Toast.LENGTH_SHORT).show();
                    editTextEmail.requestFocus();
                } //end if
            } //end onClick
        });
    }

    private void insertToDatabase(final String email, String password) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HY_Join.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                if (s.contains("다시")) { //서버에서 받아온 결과값 중에 다시가 포함되어있으면 가입 실패
                    editTextEmail.requestFocus();
                } else { //가입성공
                    Intent intent = new Intent(HY_Join.this, com.jops1.hyeyum_1.HY_Login.class);
                    intent.putExtra("email", email);
                    startActivityForResult(intent, REQUEST_CODE);
                    finish();
                } //end if
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String email = (String) params[0];
                    String password = (String) params[1]; //서버로 넘기는 값


                    String link = "http://hymanager.dothome.co.kr/hy_insert.php";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    } //end while
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                } //end try
            } //end doInBackground
        } //end InsertData

        InsertData task = new InsertData();
        task.execute(email, password);
    } //end insertToDatabase
} //end HY_Join