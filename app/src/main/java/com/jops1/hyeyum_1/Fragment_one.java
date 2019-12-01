package com.jops1.hyeyum_1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Fragment_one extends Fragment {
    private static final String TAG_RESULTS = "result";
    private static final String TAG_QUESTION = "question";

    String myJSON;
    JSONObject jsonObj;
    JSONArray peoples = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int year = Calendar.getInstance().get(Calendar.YEAR);
        long day = Dday(year + "-01-01");

        //Dday값에 질문 숫자만큼 나눈 후 나머지 값으로 질문 가져오기
        int q = (int) (day % 183);
        String qnumber = "" + q;
        getData(qnumber);
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    //Dday계산하는 메소드
    public static long Dday(String mday) {
        if (mday == null)
            return 0;
        mday = mday.trim();
        int first = mday.indexOf("-");
        int last = mday.lastIndexOf("-");
        int year = Integer.parseInt(mday.substring(0, first));
        int month = Integer.parseInt(mday.substring(first + 1, last));
        int day = Integer.parseInt(mday.substring(last + 1, mday.length()));

        GregorianCalendar cal = new GregorianCalendar();
        long currentTime = cal.getTimeInMillis() / (1000 * 60 * 60 * 24);
        cal.set(year, month - 1, day);
        long birthTime = cal.getTimeInMillis() / (1000 * 60 * 60 * 24);
        int interval = (int) (currentTime - birthTime) + 1;

        return interval;
    }

    public void getData(String qnumber) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s; //서버에서 넘기는 값 저장
                showList();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String qnumber = (String) params[0];

                    String link = "http://hymanager.dothome.co.kr/hy_getdata.php";
                    String data = URLEncoder.encode("qnumber", "UTF-8") + "=" + URLEncoder.encode(qnumber, "UTF-8");

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
        task.execute(qnumber);
    } //end getData

    protected void showList() {
        StringBuffer stringbuffer = new StringBuffer();
        try {
            //서버에서 넘겨받은 JSON값을 처리하기
            jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String question = c.getString(TAG_QUESTION);
                TextView maintitle = (TextView) getView().findViewById(R.id.txt_maintitle);
                maintitle.setText(stringbuffer.append(question)); //질문 text에 나타내기
            } //end for

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showList
} //end Fragment_One