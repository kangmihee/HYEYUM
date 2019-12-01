package com.jops1.hyeyum_1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class HY_pwdfound extends AppCompatActivity {
    private static final String TAG_RESULTS = "result";
    private static final String TAG_PASSWORD = "password";

    Button button;
    TextView pwdfind_txt;
    EditText eidt;
    String myJSON;
    JSONArray peoples = null;
    RelativeLayout pwdfoundback;

    int[] img = {R.mipmap.back1, R.mipmap.back2, R.mipmap.back3, R.mipmap.back4, R.mipmap.back5,
            R.mipmap.back6, R.mipmap.back7, R.mipmap.back8, R.mipmap.back9, R.mipmap.back10,
            R.mipmap.back11, R.mipmap.back12, R.mipmap.back13, R.mipmap.back14, R.mipmap.back15, R.mipmap.back16, R.mipmap.back17};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hy_pwdfound);

        pwdfoundback = (RelativeLayout) findViewById(R.id.pwdfoundlayout);
        Random ram = new Random();
        int num = ram.nextInt(img.length);
        pwdfoundback.setBackgroundResource(img[num]);

        button = (Button) findViewById(R.id.btn_pwd);
        pwdfind_txt = (TextView) findViewById(R.id.txt_pwd);
        eidt = (EditText) findViewById(R.id.edit_email);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eidt.getText().toString();
                selectToPassword(email); //입력한 이메일로 비밀번호 찾아오기
            }
        });
    }

    private void selectToPassword(String email) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HY_pwdfound.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                myJSON = s; //서버에서 온 값 저장
                showList();
            }

            protected void showList() {
                StringBuffer stringbuffer = new StringBuffer();
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    peoples = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < peoples.length(); i++) {
                        JSONObject c = peoples.getJSONObject(i);
                        String password = c.getString(TAG_PASSWORD);
                        pwdfind_txt = (TextView) findViewById(R.id.txt_pwd);
                        pwdfind_txt.setText(stringbuffer.append(password)); //받아온 값 textview에 나타내기
                    } //end for
                } catch (JSONException e) {
                    e.printStackTrace();
                } //end try
            } //end showList

            @Override
            protected String doInBackground(String... params) {
                try {
                    String email = (String) params[0]; //서버로 넘기는 값
                    String link = "http://hymanager.dothome.co.kr/hy_pwdfound.php";
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
    } //end selectToPassword
} //end HY_pwdfound