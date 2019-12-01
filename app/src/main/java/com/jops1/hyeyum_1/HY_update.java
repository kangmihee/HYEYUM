package com.jops1.hyeyum_1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class HY_update extends Fragment {
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ANSWER = "answer";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_DATE = "date";
    private static final String TAG_ASOPEN = "asopen";
    private static final String TAG_LOCATION = "location";
    private String question;
    private String answer;
    private ImageButton up_location_btn;
    private ImageButton up_lock_btn;
    private String location_str;
    private String asopen;

    String myJSON;
    JSONArray ShowAnswer = null;
    TextView up_location;

    String email;
    String asnumber_str;
    Integer asnumber;
    String up_answer;
    View rootView;

    EditText edit_answer;
    String up_asopen;
    int id;
    String up_tag;
    FragmentManager fragmentManager;
    InputMethodManager imm;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (View) inflater.inflate(R.layout.activity_hy_update, container, false);
        up_lock_btn = (ImageButton) rootView.findViewById(R.id.up_lock_btn);
        email = ((MainActivity) getActivity()).sendtoemail(getActivity());
        asnumber = getArguments().getInt("asnumber");
        asnumber_str = Integer.toString(asnumber);
        sendToasnumberandupdategeul(asnumber_str); //저장되어있는 값들 가져오는 함수

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        up_location = (TextView) rootView.findViewById(R.id.up_etLocation);
        up_location_btn = (ImageButton) rootView.findViewById(R.id.up_location_btn);

        //위치설정
        up_location_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showPlacePickerDialog();
                }
                return false;
            }
        });

        up_lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (up_tag.equals("공개")) { //공개 -> 비공개
                    up_lock_btn.setImageResource(R.drawable.lock_marsala);
                    up_lock_btn.setTag("비공개");
                    up_tag = (String) up_lock_btn.getTag();
                } else if (up_tag.equals("비공개")) { //비공개 -> 공개
                    up_lock_btn.setImageResource(R.drawable.lock_unchecked_cut);
                    up_lock_btn.setTag("공개");
                    up_tag = (String) up_lock_btn.getTag();
                } //end if
            } //end onClick
        });

        ImageButton update = (ImageButton) getView().findViewById(R.id.btn_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(); //키보드 숨기기
                edit_answer = (EditText) getView().findViewById(R.id.up_edit_answer1);
                up_answer = edit_answer.getText().toString();
                up_asopen = up_tag;
                Bundle bundle = new Bundle();
                String location_st = up_location.getText().toString();
                updateToAnswer(up_answer, email, up_asopen, location_st, asnumber_str);
                Toast.makeText(getContext(), "글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
                HY_my_answer_view my_answer = new HY_my_answer_view();
                Fragment update_curFragment = fragmentManager.findFragmentById(R.id.frgmentlist_three);
                Fragment_four four = new Fragment_four();
                if (curFragment.getTag() == "my_update") { //네번째 화면에서 수정했을 경우
                    fragmentTransaction.replace(R.id.frgment_mylist, four, "my_list").commit();
                } else if (update_curFragment.getTag() == "update") { //세번째 화면에서 수정했을 경우
                    bundle.putInt("asnumber", asnumber);
                    my_answer.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frgmentlist_three, my_answer, "three_my_answer").commit();
                } //end if
            } //end onClick
        });

        ImageButton back = (ImageButton) getView().findViewById(R.id.up_btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
                Fragment_four four = new Fragment_four();
                HY_my_answer_view my_answer = new HY_my_answer_view();
                Fragment update_curFragment = fragmentManager.findFragmentById(R.id.frgmentlist_three);
                if (curFragment.getTag() == "my_update") {
                    fragmentTransaction.replace(R.id.frgment_mylist, four, "my_list").commit();
                } else if (update_curFragment.getTag() == "update") {
                    bundle.putInt("asnumber", asnumber);
                    my_answer.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frgmentlist_three, my_answer, "three_my_answer").commit();
                } //end if
            } //end onClick
        });

        super.onActivityCreated(savedInstanceState);
    }

    private void showPlacePickerDialog() {
        PlaceSearchDialog placeSearchDialog = new PlaceSearchDialog.Builder(getContext())
                .setLocationNameListener(new PlaceSearchDialog.LocationNameListener() {
                    @Override
                    public void locationName(String locationName) {
                        up_location.setText(locationName);
                        location_str = locationName;
                        change_icon(location_str);
                    }
                })
                .build();
        placeSearchDialog.getWindow().setGravity(Gravity.TOP);
        placeSearchDialog.show();
    }

    //위치 설정 시 아이콘 전환
    private void change_icon(String location) {
        String location_change = location;
        if (!location_change.equals("")) { //위치 설정 할 경우
            up_location_btn.setImageResource(R.drawable.location_marsala);
        } else { //위치 설정을 안했을 경우
            up_location_btn.setImageResource(R.drawable.location_grey);
        }
    }

    //키보드 아래로 내리기
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            imm = ((MainActivity) getActivity()).sendtoimm(getContext());
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //수정된 값들 서버로 보내기
    private void updateToAnswer(String up_answer, String email, String up_asopen, String location_str, String asnumber_str) {
        class InsertData extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String answer = (String) params[0];
                    String email = (String) params[1];
                    String asopen = (String) params[2];
                    String location_str = (String) params[3];
                    String asnumber_str = (String) params[4]; //서버로 전송되는 값들

                    String link = "http://hymanager.dothome.co.kr/hy_AnswerUpdate.php";
                    String data = URLEncoder.encode("answer", "UTF-8") + "=" + URLEncoder.encode(answer, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("asopen", "UTF-8") + "=" + URLEncoder.encode(asopen, "UTF-8");
                    data += "&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location_str, "UTF-8");
                    data += "&" + URLEncoder.encode("asnumber_str", "UTF-8") + "=" + URLEncoder.encode(asnumber_str, "UTF-8");

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
        task.execute(up_answer, email, up_asopen, location_str, asnumber_str);
    } //end updateToAnswer

    private void sendToasnumberandupdategeul(String asnumber) {
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
                    String asnumber = (String) params[0];

                    String link = "http://hymanager.dothome.co.kr/hy_MyAnswerView.php";
                    String data = URLEncoder.encode("asnumber", "UTF-8") + "=" + URLEncoder.encode(asnumber, "UTF-8");

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
        task.execute(asnumber);
    } //end sendToasnumberandupdategeul

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);

                question = c.getString(TAG_QUESTION);
                answer = c.getString(TAG_ANSWER);
                String date = c.getString(TAG_DATE);
                asopen = c.getString(TAG_ASOPEN);
                int asopen_int = Integer.parseInt(asopen);
                location_str = c.getString(TAG_LOCATION);

                if (!question.equals("")) {
                    ((TextView) rootView.findViewById(R.id.up_txt_writetitle)).setText(question);
                }

                if (!answer.equals("")) {
                    ((EditText) rootView.findViewById(R.id.up_edit_answer1)).setText(answer);
                }

                if (!date.equals("")) {
                    ((TextView) rootView.findViewById(R.id.up_txt_date)).setText(date);
                }

                if (asopen_int == 0) { //공개일 경우
                    up_lock_btn.setImageResource(R.drawable.lock_unchecked_cut);
                    up_lock_btn.setTag("공개");
                    up_tag = (String) up_lock_btn.getTag();
                }

                if (asopen_int == 1) { //비공개일 경우
                    up_lock_btn.setImageResource(R.drawable.lock_marsala);
                    up_lock_btn.setTag("비공개");
                    up_tag = (String) up_lock_btn.getTag();
                }

                if (!location_str.equals("")) { //위치가 설정되었을 경우
                    up_location = (TextView) rootView.findViewById(R.id.up_etLocation);
                    up_location.setText(location_str);
                    up_location_btn.setImageResource(R.drawable.location_marsala);
                }
            } //end for

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showList
} //end HY_update
