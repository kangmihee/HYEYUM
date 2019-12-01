package com.jops1.hyeyum_1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

public class HY_Login extends AppCompatActivity {

    private static final String TAG_RESULTS = "result";
    private static final String TAG_Email = "email";
    private static final String TAG_PASSWORD = "password";

    EditText id_edit;
    EditText password_edit;
    String php_password;
    String php_email;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String myJSON;
    JSONArray peoples = null;
    RelativeLayout loginback;

    int[] img = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16, R.mipmap.back17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hy__login);

        if (!NetworkConnected(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("네트워크가 연결되어 있지 않습니다. 네트워크를 확인하신 후 다시 실행해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        editor = pref.edit();

        //로그인 배경 랜덤
        loginback = (RelativeLayout) findViewById(R.id.loginlayout);
        Random ram = new Random();
        int num = ram.nextInt(img.length);
        loginback.setBackgroundResource(img[num]);

        //폰트 적용
        FontClass.setDefaultFont(this, "MONOSPACE", "DX시인과나.ttf");

        id_edit = (EditText) findViewById(R.id.edit_loginemail);
        password_edit = (EditText) findViewById(R.id.edit_loginpwd);

        //비밀번호 찾기로 이동
        findViewById(R.id.btn_pwdfind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HY_pwdfound.class);
                startActivity(intent);
            }
        });

        //회원가입으로 이동
        findViewById(R.id.btn_joingo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.jops1.hyeyum_1.HY_Join.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭시 빈칸 유무 확인
        findViewById(R.id.btn_loginok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = id_edit.getText().toString();
                String password = password_edit.getText().toString();
                if (email.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "빈칸없이 입력해주세요ㅠㅠ", Toast.LENGTH_SHORT).show();
                    id_edit.requestFocus();
                } else {  //빈칸이 없을 경우 로그인하는 함수 호출
                    confirminformation(email);
                } //end if
            } //end onClick
        });
    }

    // 네트워크 연결 확인
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

    @Override
    protected void onStop() {

        //자동 로그인을 위한 값 저장
        pref = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        super.onStop();

        String con_id = id_edit.getText().toString();
        String con_pw = password_edit.getText().toString();

        id_edit = (EditText) findViewById(R.id.edit_loginemail);
        password_edit = (EditText) findViewById(R.id.edit_loginpwd);

        editor.putString("email", con_id);
        editor.putString("password", con_pw);

        editor.commit();
    }

    private void confirminformation(String email) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                myJSON = result; //서버에서 넘겨온 값 저장
                showList();
            }

            protected void showList() {
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    peoples = jsonObj.getJSONArray(TAG_RESULTS);
                    String con_id = id_edit.getText().toString();
                    String con_pw = password_edit.getText().toString();

                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject c = peoples.getJSONObject(i);
                        php_email = c.getString(TAG_Email);
                        php_password = c.getString(TAG_PASSWORD);
                    } //end for

                    //DB에 저장된 값이랑 입력된 값이랑 비교하여 일치하는 것만 로그인 성공시켜주기
                    if (php_email == null) { //가입된 아이디가 아닐 경우
                        Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 확인해 주세요^^", Toast.LENGTH_SHORT).show();
                    } else if (php_password.equals(con_pw) && php_email.equals(con_id)) { //로그인 성공

                        editor = pref.edit();

                        editor.putString("email", con_id);
                        editor.putString("password", con_pw);
                        editor.commit();

                        Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();

                        String email = id_edit.getText().toString();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();

                    } else if (php_email.equals(con_id) && !php_password.equals(con_pw)) { //가입된 비밀번호가 아닐 경우
                        Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
                        id_edit.requestFocus();
                    } //end if
                } catch (JSONException e) {
                    e.printStackTrace();
                } //end try
            } //end showList

            @Override
            protected String doInBackground(String... params) {
                try {
                    String email = (String) params[0];

                    String link = "http://hymanager.dothome.co.kr/hy_id_test.php";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    //서버에서 받은 값
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        break;
                    } //end while

                    return sb.toString().trim();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                } //end try
            } //end doInBackground
        } //end InsertData
        InsertData task = new InsertData();
        task.execute(email);
    } //end confirminformation
} //end HY_Login
