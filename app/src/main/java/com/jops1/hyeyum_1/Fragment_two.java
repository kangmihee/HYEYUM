package com.jops1.hyeyum_1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Fragment_two extends Fragment {

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ASNUMBER = "asnumber";
    private static final String TAG_ANSWER = "answer";
    private static final String TAG_SAVETIME = "savetime";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_COUNT = "count";

    String myJSON;
    JSONObject jsonObj;
    TextView talist_date;
    JSONArray peoples = null;
    JSONArray ShowAnswer = null;
    ArrayList<TaData> data;
    ListView listView;
    TaAdapter adatper;
    ImageButton calendar;
    static String email;
    static String calendardate;
    static String asnumber;
    static String count;
    static String question;
    View rootView;
    SwipeRefreshLayout ta_SwipeRefreshLayout;
    String strDate;

    FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_two, container, false);

        //main에서 이메일 받아오기
        email = ((MainActivity) getActivity()).sendtoemail(getContext());

        data = new ArrayList<TaData>();
        listView = (ListView) rootView.findViewById(R.id.ta_listView);
        talist_date = (TextView) rootView.findViewById(R.id.txt_talist_date);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
        strDate = dateFormat.format(date); //오늘날짜 가져오기
        talist_date.setText(strDate);

        //달력을 클릭해서 넘어온 값들 체크해서 화면 전환시켜주기
        if (getArguments() != null) { //선택한 날짜에 대한 내가 공감한 혜윰
            if (getArguments().getString("cal_like") != null) {
                calendardate = getArguments().getString("cal_like");
                selectasnswerlike(email, calendardate);
                talist_date.setText(calendardate);
                int year = Calendar.getInstance().get(Calendar.YEAR);
                long day = calDday(year + "/01/01");
                int q = (int) day % 183;
                String qnumber = "" + q;
                getData(qnumber);
            } else { //선택한 날짜에 대해서 공개된 글들
                calendardate = getArguments().getString("cal_answer");
                sendTodateandSelectmygeul(email, calendardate);
                talist_date.setText(calendardate);
                int year = Calendar.getInstance().get(Calendar.YEAR);
                long day = calDday(year + "/01/01");
                int q = (int) day % 183;
                String qnumber = "" + q;
                getData(qnumber);
            } //end if
        } else { //오늘날짜에 대한 질문 가져오기
            sendToemailandSelectmygeul(email, strDate);
            talist_date.setText(strDate);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            long day = Dday(year + "/01/01");
            int q = (int) day % 183;
            String qnumber = "" + q;
            getData(qnumber);
        } //end if
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                asnumber = data.get(position).masnumber;
                Bundle bundle = new Bundle();

                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_talist);
                HY_ta_answer_view ta_answer = new HY_ta_answer_view();

                if (curFragment.getTag() == "ta_list") { //타인 리스트에서 눌렀을 경우
                    fragmentTransaction.replace(R.id.fragment_talist, ta_answer, "ta_answer").commit();
                    asnumber = data.get(position).masnumber;

                    //화면 전환에 필요한 값 Bundle에 넣어서 넘겨주기
                    bundle.putString("asnumber", asnumber);
                    ta_answer.setArguments(bundle);
                } else if (curFragment.getTag() == "ta_answer_cal") { //공개된 답변을 보다가 눌렀을 경우(달력에서)
                    fragmentTransaction.replace(R.id.fragment_talist, ta_answer, "ta_answer_cal").commit();
                    asnumber = data.get(position).masnumber;
                    bundle.putString("asnumber", asnumber);
                    bundle.putString("date", calendardate); //해당날짜의 답변을 가지고와야해서
                    ta_answer.setArguments(bundle);
                } else if (curFragment.getTag() == "ta_like_cal") { //사용자가 공감한 혜윰을 눌렀을 경우 (달력에서)
                    fragmentTransaction.replace(R.id.fragment_talist, ta_answer, "ta_like_cal").commit();
                    asnumber = data.get(position).masnumber;
                    bundle.putString("asnumber", asnumber);
                    bundle.putString("date", calendardate);

                    ta_answer.setArguments(bundle);
                } //end if
            } //onItemClick
        });

        //달력을 눌렀을 때
        calendar = (ImageButton) getView().findViewById(R.id.imageButton3);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.fragment_talist);
                CalendarActivity calendarActivity = new CalendarActivity();

                if (curFragment.getTag() == "ta_list" || curFragment.getTag() == "ta_answer_cal" || curFragment.getTag() == "ta_like_cal") {
                    fragmentTransaction.replace(R.id.fragment_talist, calendarActivity, "calendar").commit();
                    bundle.putString("question", question);
                    calendarActivity.setArguments(bundle);

                } //end if
            } //end onClick
        });

        //오늘날짜만 새로고침시키기
        if (strDate.equals(talist_date.getText().toString())) {
            ta_SwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_ta_layout);
            ta_SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    data.clear();
                    sendToemailandSelectmygeul(email, strDate);
                    ta_SwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            ta_SwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_ta_layout);
            ta_SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ta_SwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "새로운 글이 없습니다!", Toast.LENGTH_SHORT).show();
                }
            });
        } //end if

        super.onActivityCreated(savedInstanceState);
    } //end onActivityCreated

    //Dday계산하는 함수
    public static long Dday(String mday) {
        if (mday == null)
            return 0;
        mday = mday.trim();
        int first = mday.indexOf("/");
        int last = mday.lastIndexOf("/");
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


    //선택한 날짜까지의 Dday계산하는 함수
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
                show_qa();

                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String qnumber = (String) params[0]; //서버로 보내는 값

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
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                } //end try
            } //end doInBackground
        } //end GetDataJSON
        GetDataJSON task = new GetDataJSON();
        task.execute(qnumber);
    } //end getData

    protected void show_qa() {
        StringBuffer stringbuffer = new StringBuffer();
        try {

            //서버에서 넘긴 JSON값을 받기
            jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                question = c.getString(TAG_QUESTION);
                TextView maintitle = (TextView) rootView.findViewById(R.id.txt_talist_quation);
                maintitle.setText(stringbuffer.append(question));
            } //end for
        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end show_qa

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);

                asnumber = c.getString(TAG_ASNUMBER);
                String answer = c.getString(TAG_ANSWER);
                String savetime = c.getString(TAG_SAVETIME);
                String location = c.getString(TAG_LOCATION);
                count = c.getString(TAG_COUNT);

                //DB에 있는 값들을 받아와서 값들을 화면에 뿌려주기
                if (!location.equals("") && answer != null && savetime != null) {
                    data.add(new TaData(asnumber, answer, savetime, R.drawable.location_full, count));
                } else if (location.equals("") && answer != null && savetime != null) {
                    data.add(new TaData(asnumber, answer, savetime, R.drawable.location_line, count));
                } //end if

            } //end for
            adatper = new TaAdapter(getContext(), R.layout.hy_ta_answer_list, data);
            listView.setAdapter(adatper);

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showList


    private void sendToemailandSelectmygeul(String email, String strDate) {
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
                    String strDate = (String) params[1];

                    String link = "http://hymanager.dothome.co.kr/hy_ta_answerlistgetdata.php";
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
        } //end InsertData
        InsertData task = new InsertData();
        task.execute(email, strDate);
    } //end sendToemailandSelectmygeul

    private void sendTodateandSelectmygeul(String email, String calendardate) {
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
                    String calendardate = (String) params[1];

                    String link = "http://hymanager.dothome.co.kr/hy_ta_answerlistgetdata_1.php";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("calendardate", "UTF-8") + "=" + URLEncoder.encode(calendardate, "UTF-8");

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
        task.execute(email, calendardate);
    } //end sendTodateandSelectmygeul


    private void selectasnswerlike(String email, String calendardate) {
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
                    String calendardate = (String) params[1];

                    String link = "http://hymanager.dothome.co.kr/hy_ta_answerlistgetdata_2.php";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    data += "&" + URLEncoder.encode("calendardate", "UTF-8") + "=" + URLEncoder.encode(calendardate, "UTF-8");

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
        task.execute(email, calendardate);
    } //end selectasnswerlike

} //end Fragment_two

class TaAdapter extends BaseAdapter {

    Context mContext;
    int mLayout;
    ArrayList<TaData> mData;

    TaAdapter(Context context, int layout, ArrayList<TaData> data) {
        mContext = context;
        mLayout = layout;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView = null;
        if (convertView == null) {
            myView = View.inflate(mContext, mLayout, null);
        } else {  //view 재활용
            myView = convertView;
        }

        TextView Text_question = (TextView) myView.findViewById(R.id.txt_talist_answer);
        TextView Text_date = (TextView) myView.findViewById(R.id.relative_savetime);
        ImageView imageView_loc = (ImageView) myView.findViewById(R.id.img_talist_location);

        //각각의 위치에 Data추가하기
        Text_question.setText(mData.get(position).manswer);
        Text_date.setText(mData.get(position).msavetime);
        imageView_loc.setImageResource(mData.get(position).mlocation);

        TextView heart_count = (TextView) myView.findViewById(R.id.txt_talist_count);
        heart_count.setText(mData.get(position).mcount);

        return myView;
    }
}

class TaData {
    String masnumber;
    String manswer;
    String msavetime;
    int mlocation;
    String mcount;

    TaData(String asnumber, String answer, String savetime, int location, String count) {
        masnumber = asnumber;
        manswer = answer;
        msavetime = savetime;
        mlocation = location;
        mcount = count;
    }
}