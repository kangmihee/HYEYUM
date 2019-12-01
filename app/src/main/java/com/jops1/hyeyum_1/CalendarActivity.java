package com.jops1.hyeyum_1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
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

public class CalendarActivity extends Fragment {

    private static final String TAG_RESULTS = "result";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_ANSWER = "answer";

    private CalendarView mCalendarView;
    String myJSON;
    JSONObject jsonObj;
    JSONArray ShowAnswer = null;
    static String email;
    static String question;
    static String answer;
    String bundlequestion;
    View rootView;
    static String calendardate;
    static String calendardate_1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceStateBundle) {
        rootView = (View) inflater.inflate(R.layout.calendar_layout, container, false);
        mCalendarView = (CalendarView) rootView.findViewById(R.id.calendarView);

        //main에 있는 email가져오기
        email = ((MainActivity) getActivity()).sendtoemail(getContext());

        //달력에서 넘어온 값이 있는지 없는지 확인
        if (getArguments() != null) {
            bundlequestion = getArguments().getString("question");
            ((TextView) rootView.findViewById(R.id.cal_question)).setText(bundlequestion);
        } //end if

        //달력 하단에 선택된 날짜에 관한 공개된 글의 갯수와 내가 공감한 것을 알려줌
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                calendardate = year + "/" + (month + 1) + "/" + dayOfMonth;
                calendardate_1 = year + "-" + (month + 1) + "-" + dayOfMonth;

                year = Calendar.getInstance().get(Calendar.YEAR);
                long day = calDday(year + "/01/01");
                int q = (int) day % 183;
                String qnumber = "" + q;
                getData(qnumber);
                answercount(email, calendardate_1);
            } //end onSelectedDayChange
        }); //end OnDateChangeListener

        return rootView;
    } //end onCreateView

    public static long calDday(String mday) {
        if (mday == null)
            return 0;
        mday = mday.trim();
        int first = mday.indexOf("/");
        int last = mday.lastIndexOf("/");
        int year = Integer.parseInt(mday.substring(0, first));
        int month = Integer.parseInt(mday.substring(first + 1, last));
        int day = Integer.parseInt(mday.substring(last + 1, mday.length()));

        String calendardate_dday = calendardate.trim();
        int d_first = calendardate_dday.indexOf("/");
        int d_last = calendardate_dday.lastIndexOf("/");
        int d_month = Integer.parseInt(calendardate_dday.substring(d_first + 1, d_last));
        int d_day = Integer.parseInt(calendardate_dday.substring(d_last + 1, calendardate_dday.length()));
        Calendar cal = Calendar.getInstance();
        cal.set(year, d_month - 1, d_day);
        long currentTime = cal.getTimeInMillis() / (1000 * 60 * 60 * 24);
        cal.set(year, month - 1, day);
        long birthTime = cal.getTimeInMillis() / (1000 * 60 * 60 * 24);
        int interval = (int) (currentTime - birthTime) + 1;
        return interval;
    } //end calDday

    public void getData(String qnumber) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s;
                show_qa(); //서버에서 넘어온 결과 처리하기기

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

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                } //end catch
            } //end doInBackground
        } //end GetDataJSON
        GetDataJSON task = new GetDataJSON();
        task.execute(qnumber);
    } //end getData

    //질문 화면에 나타내기
    protected void show_qa() {
        try {
            jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);
                question = c.getString(TAG_QUESTION);

                ((TextView) rootView.findViewById(R.id.cal_question)).setText(question);
            } //end for
        } catch (JSONException e) {
            e.printStackTrace();
        } //end catch
    } //end show_qa

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);

                answer = c.getString(TAG_ANSWER);

                //공개된 타인 답변의 갯수
                TextView answercount = (TextView) rootView.findViewById(R.id.cal_answercount);
                answercount.setText(answer + "개의 혜윰 글");
                answercount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_talist);
                        com.jops1.hyeyum_1.Fragment_two two = new com.jops1.hyeyum_1.Fragment_two();

                        //tag값으로 화면 전환
                        if (curFragment.getTag() == "calendar") {
                            fragmentTransaction.replace(R.id.fragment_talist, two, "ta_answer_cal").commit();
                            bundle.putString("cal_answer", calendardate);
                            two.setArguments(bundle);
                        } //end if
                    } //end onClick
                }); //end OnClickListener

                //내가 공감한 혜윰
                TextView likecount = (TextView) rootView.findViewById(R.id.cal_like);
                likecount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_talist);
                        com.jops1.hyeyum_1.Fragment_two two = new com.jops1.hyeyum_1.Fragment_two();

                        //tag값으로 화면 전환
                        if (curFragment.getTag() == "calendar") {
                            fragmentTransaction.replace(R.id.fragment_talist, two, "ta_like_cal").commit();
                            bundle.putString("cal_like", calendardate);
                            two.setArguments(bundle);
                        }
                    }
                }); //end OnClickListener
            }// end for

        } catch (JSONException e) {
            e.printStackTrace();
        }
    } //end showList

    private void answercount(String email, String calendardate_1) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s;
                showList();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String email = (String) params[0];
                    String calendardate_1 = (String) params[1];

                    String link = "http://hymanager.dothome.co.kr/hy_ta_answer_count.php";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("calendardate_1", "UTF-8") + "=" + URLEncoder.encode(calendardate_1, "UTF-8");

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
                    }//end while
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            } //end doInBackground
        } //end InsertData
        InsertData task = new InsertData();
        task.execute(email, calendardate_1);
    } //end answercount
} //end CalendarActivity