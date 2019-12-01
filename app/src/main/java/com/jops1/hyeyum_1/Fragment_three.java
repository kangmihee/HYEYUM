package com.jops1.hyeyum_1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Fragment_three extends Fragment {

    private static final String TAG_RESULTS = "result";
    private static final String TAG_QUESTION = "question";
    private EditText answer_write;
    private ImageButton answer_save;
    private ImageButton location_btn;
    private String question;
    private String email;
    private String answer;
    private String asopen;
    private TextView maintitle;

    String myJSON;
    JSONArray peoples = null;
    ImageView lock_btn;
    String tag;
    TextView location;
    String location_str;
    View rootView;
    InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(R.layout.fragment_three, container, false);
        imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);

        //main에서 이메일 받아오기
        email = ((MainActivity) getActivity()).sendtoemail(getContext());
        TextView textDate = (TextView) rootView.findViewById(R.id.txt_date);
        lock_btn = (ImageView) rootView.findViewById(R.id.lock_btn);

        //잠금이미지 tag 설정하기 (공개 / 비공개)
        if (lock_btn.getTag() == null) {
            lock_btn.setTag("공개");
            tag = (String) lock_btn.getTag();
        }

        //오늘날짜 받아와서 화면에 나타내기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
        String strDate = dateFormat.format(date);
        textDate.setText(strDate);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        long day = Dday(year + "-01-01");
        int q = (int) (day % 183);
        String qnumber = "" + q;
        getData(qnumber);

        answer_save = (ImageButton) rootView.findViewById(R.id.btn_save);
        answer_save.setOnClickListener(clickListener);
        answer_write = (EditText) rootView.findViewById(R.id.edit_answer1);

        location = (TextView) rootView.findViewById(R.id.etLocation);
        location_btn = (ImageButton) rootView.findViewById(R.id.location_btn);

        //위치 설정하기
        location_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showPlacePickerDialog();
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        //잠금 아이콘 클릭 시 공개/비공개값을 tag값에 설정하는 메소드
        lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag.equals("공개")) {
                    lock_btn.setImageResource(R.drawable.lock_marsala);
                    lock_btn.setTag("비공개");
                    tag = (String) lock_btn.getTag();

                } else if (tag.equals("비공개")) {
                    lock_btn.setImageResource(R.drawable.lock_unchecked_cut);
                    lock_btn.setTag("공개");
                    tag = (String) lock_btn.getTag();
                } //end if
            } //end onClick
        });

        super.onActivityCreated(savedInstanceState);
    } //end onActivityCreated


    //위치 설정하기
    private void showPlacePickerDialog() {

        PlaceSearchDialog placeSearchDialog = new PlaceSearchDialog.Builder(getContext())
                .setLocationNameListener(new PlaceSearchDialog.LocationNameListener() {
                    @Override
                    public void locationName(String locationName) {

                        //설정된 위치 text에 나타내기
                        location.setText(locationName);
                        location_str = locationName;
                        change_icon(location_str);
                    }
                })
                .build();
        placeSearchDialog.getWindow().setGravity(Gravity.TOP);
        placeSearchDialog.show();

    }

    //위치를 설정하면 아이콘 변하게 하는 함수
    private void change_icon(String location) {
        String location_change = location;
        if (!location_change.equals("")) { //위치 설정을 했을 경우
            location_btn.setImageResource(R.drawable.location_marsala);
        } else { //위치 설정을 안했을 경우
            location_btn.setImageResource(R.drawable.location_grey);
        } //end if
    } //end change_icon


    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            answer = answer_write.getText().toString();
            question = maintitle.getText().toString();
            asopen = tag; //공개여부를 tag로 확인
            String location_null_check = location.getText().toString();

            //빈글은 제외시키고
            if (answer.equals("")) {
                Toast.makeText(getContext(), "글을 작성해주세요!", Toast.LENGTH_SHORT).show();
            } else {
                switch (v.getId()) {
                    case R.id.btn_save: //글 저장
                        hideKeyboard(); //키보드 내리기
                        insertToDatabase(answer, question, email, asopen, location_null_check);

                        answer_write.setText("");

                        ((MainActivity) getActivity()).pageReload(); //페이지 저장시 페이지 새로고침
                        //페이지 새로고침시 tab이 사라지기 때문에 tab 다시 호출 해주기
                        ((MainActivity) getActivity()).setTabLayoutIcon();

                        break;
                } //end switch
            } //end if
        } //end onClick
    };

    public long Dday(String mday) {
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

    //키보드 아래로 내리기
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            imm = ((MainActivity) getActivity()).sendtoimm(getContext());
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    protected void showList() {
        StringBuffer stringbuffer = new StringBuffer();
        try {

            //서버에서 받아온 값을 처리하기
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String question = c.getString(TAG_QUESTION);

                maintitle = (TextView) rootView.findViewById(R.id.txt_writetitle);
                maintitle.setText(stringbuffer.append(question));
            } //end for

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showList

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

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    } //end while

                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                } //end try
            } //end doInBackground
        }//end GetDataJSON
        GetDataJSON task = new GetDataJSON();
        task.execute(qnumber);
    } //end getData

    private void insertToDatabase(String answer, String question, String email, String asopen, String location_str) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                //서버에서 오는 값을 받아서 toast띄우기
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String answer = (String) params[0];
                    String question = (String) params[1];
                    String email = (String) params[2];

                    String asopen = (String) params[3];
                    String location_str = (String) params[4];

                    String link = "http://hymanager.dothome.co.kr/hy_answer_insert.php";
                    String data = URLEncoder.encode("answer", "UTF-8") + "=" + URLEncoder.encode(answer, "UTF-8");
                    data += "&" + URLEncoder.encode("question", "UTF-8") + "=" + URLEncoder.encode(question, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("asopen", "UTF-8") + "=" + URLEncoder.encode(asopen, "UTF-8");
                    data += "&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location_str, "UTF-8");

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
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                } //end try
            } //end doInBackground
        } //end InsertData
        InsertData task = new InsertData();
        task.execute(answer, question, email, asopen, location_str);
    } //end insertToDatabase
} //end Fragment_three