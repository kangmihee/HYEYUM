package com.jops1.hyeyum_1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class HY_ta_answer_view extends Fragment {

    private static final String TAG_RESULTS = "result";
    private static final String TAG_COUNTRESULT = "result_count";
    private static final String TAG_ANSWER = "answer";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_DATE = "date";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_COUNT = "count";
    private static final String TAG_SAVETIME = "savetime";
    private static final String TAG_COUNTCHECK = "count_check";

    JSONArray ShowAnswer = null;
    String myJSON;
    ImageButton heart;
    private ImageButton back_list_btn;
    Button menu_btn;
    TextView heart_count;
    String getemail;
    String strDate;
    String asnumber;
    String email;
    String count;
    String or_count;
    String date;
    int int_or_count;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (View) inflater.inflate(R.layout.activity_hy_ta_answer_view, container, false);
        email = ((MainActivity) getActivity()).sendtoemail(getActivity());

        //list에서 넘어온 값 확인하기
        if (getArguments() != null) {
            asnumber = getArguments().getString("asnumber");
            sendToasnumberandSelectgeul(asnumber, email);
            date = getArguments().getString("date");
        }

        heart_count = (TextView) rootView.findViewById(R.id.txt_heart_ta_answer_view);
        heart = (ImageButton) rootView.findViewById(R.id.img_heart_ta_answer_view);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String count_up = "1";
                clickheart(asnumber, count_up, email); //count증가
            }
        });

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
        strDate = dateFormat.format(date);

        //답변이 긴 경우에 스크롤로 전체를 볼 수 있게 설정
        TextView tatextview = (TextView) rootView.findViewById(R.id.txt_ta_answer_view);
        tatextview.setMovementMethod(ScrollingMovementMethod.getInstance());
        tatextview.setVerticalScrollBarEnabled(false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        back_list_btn = (ImageButton) getView().findViewById(R.id.btn_back_ta_answer_view);
        back_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_talist);
                Fragment_two two = new Fragment_two();

                if (curFragment.getTag() == "ta_answer") { //두번째에서 눌렀을 경우
                    fragmentTransaction.replace(R.id.fragment_talist, two, "ta_list").commit();
                } else if (curFragment.getTag() == "ta_answer_cal" && date != null) { //달력에서 답변을 누르고 들어온 경우
                    Bundle bundle = new Bundle();
                    bundle.putString("cal_answer", date);
                    two.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_talist, two, "ta_answer_cal").commit();
                } else if (curFragment.getTag() == "ta_like_cal" && date != null) { //달력을 누르고 공감한 혜윰에서 들어온 경우
                    Bundle bundle = new Bundle();
                    bundle.putString("cal_like", date);
                    two.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_talist, two, "ta_like_cal").commit();
                } //end if
            } //end onClick
        });
        super.onActivityCreated(savedInstanceState);
    }

    protected void update_heart() {

        try {
            //서버에서 넘어온 값을 가져오기
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray ShowHeart = jsonObj.getJSONArray(TAG_COUNTRESULT);
            String up_count;

            for (int i = 0; i < ShowHeart.length(); i++) {
                JSONObject c = ShowHeart.getJSONObject(i);
                up_count = c.getString(TAG_COUNT);
                heart_count.setText(up_count);

                if (or_count.equals(up_count)) {
                    Toast.makeText(getContext(), "공감 완료!", Toast.LENGTH_SHORT).show();
                    ImageView imageView = (ImageView) rootView.findViewById(R.id.img_heart_ta_answer_view);
                    imageView.setImageResource(R.drawable.heart_full);
                } else if (!or_count.equals(up_count)) {
                    Toast.makeText(getContext(), "공감 취소!", Toast.LENGTH_SHORT).show();
                    ImageView imageView = (ImageView) rootView.findViewById(R.id.img_heart_ta_answer_view);
                    imageView.setImageResource(R.drawable.heart_line);
                } //end if
            } //end for
        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end update_heart

    protected void showList() {

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);
                String question = c.getString(TAG_QUESTION);
                String answer = c.getString(TAG_ANSWER);
                String date = c.getString(TAG_DATE);
                String location = c.getString(TAG_LOCATION);
                String email = c.getString(TAG_EMAIL);
                count = c.getString(TAG_COUNT);
                String like_check = c.getString(TAG_COUNTCHECK);
                String savetime = c.getString(TAG_SAVETIME);

                if (!question.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_questiontitle_ta_answer_view)).setText(question);
                }
                if (!answer.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_ta_answer_view)).setText(answer);
                }
                if (!date.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_date_ta_answer_view)).setText(date);
                }
                if (!location.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_loc_ta_answer_view)).setText(location);
                }
                if (email.equals(getemail) && (strDate.equals(date))) {
                    menu_btn.setVisibility(View.VISIBLE);
                }
                heart_count.setText(count);
                or_count = heart_count.getText().toString();

                if (like_check.equals("0")) { //공감을 아직 하지 않았을 경우
                    int_or_count = Integer.parseInt(or_count);
                    int_or_count = int_or_count + 1; //서버에서 가져온 값 증가시키기
                } else if (like_check.equals("1")) { //공감을 이미 했을 경우
                    int_or_count = Integer.parseInt(or_count);
                    ImageView imageView = (ImageView) rootView.findViewById(R.id.img_heart_ta_answer_view);
                    imageView.setImageResource(R.drawable.heart_full); //아이콘 변경
                }
                or_count = Integer.toString(int_or_count);
                ((TextView) rootView.findViewById(R.id.relative_ta_answer_savetime)).setText(savetime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //기본키로 화면에 나타내야 하는 것들 서버에서 가져오기
    private void sendToasnumberandSelectgeul(String asnumber, String email) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s; //서버에서 받아온 값 저장
                showList();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String asnumber = (String) params[0];
                    String email = (String) params[1]; //서버로 보내는 값
                    String link = "http://hymanager.dothome.co.kr/hy_Tatest.php";
                    String data = URLEncoder.encode("asnumber", "UTF-8") + "=" + URLEncoder.encode(asnumber, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

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
        } //end InsertData
        InsertData task = new InsertData();
        task.execute(asnumber, email);
    } //end sendToasnumberandSelectgeul

    private void clickheart(String asnumber, String count_up, String email) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                myJSON = s;

                if (myJSON != null) {
                    update_heart();
                } else {
                    Toast.makeText(getContext(), "공감 실패...", Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String asnumber = (String) params[0];
                    String count_up = (String) params[1];
                    String email = (String) params[2];

                    String link = "http://hymanager.dothome.co.kr/hy_heartupdate.php";
                    String data = URLEncoder.encode("asnumber", "UTF-8") + "=" + URLEncoder.encode(asnumber, "UTF-8");
                    data += "&" + URLEncoder.encode("count_up", "UTF-8") + "=" + URLEncoder.encode(count_up, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

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
        } //end InsertData
        InsertData task = new InsertData();
        task.execute(asnumber, count_up, email);
    } //end clickheart
} //end HY_ta_answer_view