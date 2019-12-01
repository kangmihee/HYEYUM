package com.jops1.hyeyum_1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentList_Three extends Fragment {

    String myJSON;
    JSONObject jsonObj;
    JSONArray ShowAnswer = null;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_DATE = "date";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ASNUMBER = "asnumber";


    static String email;
    View rootView;
    String strDate;
    Integer asnumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 페이지를 리로드할때 이전 asnumber 초기화
        asnumber = null;


        //main에서 이메일 받아오기
        email = ((MainActivity) getActivity()).sendtoemail(getContext());
        rootView = inflater.inflate(R.layout.fragmentlist_three, container, false);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
        strDate = dateFormat.format(date);

        //오늘날짜에 대한 답의 여부에 따라서 화면을 전환시켜주는 함수
        getData(email, strDate);

        return rootView;
    }

    public void getData(String email, String strDate) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s; //서버에서 넘겨온 값 저장
                showList();

                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String email = (String) params[0];
                    String strDate = (String) params[1]; //서버에 전송될 값

                    String link = "http://hymanager.dothome.co.kr/hy_todayAnswerView.php";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("strDate", "UTF-8") + "=" + URLEncoder.encode(strDate, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    } //end while
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                } //end try
            } //end doInBackground
        } //end GetDataJSON

        GetDataJSON task = new GetDataJSON();
        task.execute(email, strDate);
    } //end getData

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);
                String date = c.getString(TAG_DATE);
                email = c.getString(TAG_EMAIL);
                asnumber = c.getInt(TAG_ASNUMBER);
            } //end for

            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment_three three = new Fragment_three();
            HY_my_answer_view my_answer = new HY_my_answer_view();

            if (asnumber == null) { //사용자가 오늘날짜에 쓴 글이 없을 경우
                fragmentTransaction.replace(R.id.frgmentlist_three, three, "three").commit();
            } else { //사용자가 오늘날짜에 쓴 글이 있을 경우
                fragmentTransaction.replace(R.id.frgmentlist_three, my_answer, "three_my_answer").commit();
                bundle.putInt("asnumber", asnumber);
                my_answer.setArguments(bundle);
            } //end if

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showList
} //end FragmentList_Three