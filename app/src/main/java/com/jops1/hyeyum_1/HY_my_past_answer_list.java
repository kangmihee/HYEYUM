package com.jops1.hyeyum_1;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;

public class HY_my_past_answer_list extends Fragment {
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ANSWER = "answer";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_DATE = "date";
    private static final String TAG_ASOPEN = "asopen";
    private static final String TAG_COUNT = "count";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_EMAIL = "email";

    JSONArray ShowAnswer = null;
    String myJSON;
    String email;
    String strDate;
    String asnumber_str;
    Integer asnumber;
    String date;
    View rootView;
    FragmentManager fragmentManager;
    ArrayList<Past_MyData> data;
    ListView listView;
    MyPastAdapter adatper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(R.layout.hy_my_past_answer_list, container, false);

        //main에서 이메일 받아오기
        email = ((MainActivity) getActivity()).sendtoemail(getActivity());
        asnumber = getArguments().getInt("asnumber");
        asnumber_str = Integer.toString(asnumber);
        sendToasnumberandSelectgeul(asnumber_str, email);
        listView = (ListView) rootView.findViewById(R.id.past_listView);
        data = new ArrayList<Past_MyData>();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
        strDate = dateFormat.format(date);

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            // 과거 답변 list에서 화면을 위에서 아래로 스와이프 시 화면 전환시키기
                            if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                Bundle bundle = new Bundle();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                HY_my_answer_view my_answer = new HY_my_answer_view();
                                Fragment three_curFragment = fragmentManager.findFragmentById(R.id.frgmentlist_three);

                                Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);

                                if (three_curFragment.getTag() == "three_my_past_answer") {
                                    fragmentTransaction
                                            .setCustomAnimations(R.anim.slide_down, R.anim.slide_out_down)
                                            .replace(R.id.frgmentlist_three, my_answer, "three_my_answer")
                                            .commit();
                                    bundle.putInt("asnumber", asnumber);
                                    my_answer.setArguments(bundle);
                                } else if (curFragment.getTag() == "four_my_past_answer") {
                                    fragmentTransaction
                                            .setCustomAnimations(R.anim.slide_down, R.anim.slide_out_down)
                                            .replace(R.id.frgment_mylist, my_answer, "my_answer")
                                            .commit();
                                    bundle.putInt("asnumber", asnumber);
                                    my_answer.setArguments(bundle);
                                } //end if
                            } //end if
                        } catch (Exception e) {
                        } //end try
                        return super.onFling(e1, e2, velocityX, velocityY);
                    } //end onFling
                });
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
        return rootView;
    } //end onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);

                String question = c.getString(TAG_QUESTION);
                String answer = c.getString(TAG_ANSWER);
                date = c.getString(TAG_DATE);
                int asopen = c.getInt(TAG_ASOPEN);
                String location = c.getString(TAG_LOCATION);
                String php_email = c.getString(TAG_EMAIL);
                String count = c.getString(TAG_COUNT);

                if (!question.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_questiontitle_my_past_answer_view)).setText(question);
                }

                if (!location.equals("") && asopen == 1 && !answer.equals("") && !date.equals("")) {
                    data.add(new Past_MyData(answer, date, R.drawable.lock, R.drawable.location_full, location, count));
                } else if (!location.equals("") && asopen == 0 && !answer.equals("") && !date.equals("")) {
                    data.add(new Past_MyData(answer, date, R.drawable.unlock, R.drawable.location_full, location, count));
                } else if (location.equals("") && asopen == 0 && !answer.equals("") && !date.equals("")) {
                    data.add(new Past_MyData(answer, date, R.drawable.unlock, R.drawable.location_line, location, count));
                } else if (location.equals("") && asopen == 1 && !answer.equals("") && !date.equals("")) {
                    data.add(new Past_MyData(answer, date, R.drawable.lock, R.drawable.location_line, location, count));
                }
            }

            adatper = new MyPastAdapter(getContext(), R.layout.hy_my_past_answer_listitem, data);
            listView.setAdapter(adatper);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendToasnumberandSelectgeul(String asnumber, String email) {
        class InsertData extends AsyncTask<String, Void, String> {

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
                    String asnumber = (String) params[0];
                    String email = (String) params[1]; //서버에 전송하는 값

                    String link = "http://hymanager.dothome.co.kr/hy_MyPast_AnswerView.php";
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
}//end HY_my_past_answer_list


class MyPastAdapter extends BaseAdapter {

    Context mContext;
    int mLayout;
    ArrayList<Past_MyData> mData;

    MyPastAdapter(Context context, int layout, ArrayList<Past_MyData> data) {
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
        } else {
            myView = convertView; //화면 재사용
        }

        TextView past_answer = (TextView) myView.findViewById(R.id.txt_my_past_answer_view);
        past_answer.setText(mData.get(position).manswer);

        TextView Text_date = (TextView) myView.findViewById(R.id.txt_date_my_past_answer_view);
        Text_date.setText(mData.get(position).mdate);

        TextView Text_loc = (TextView) myView.findViewById(R.id.txt_loc_my_past_answer_view);
        Text_loc.setText(mData.get(position).mlocation);

        TextView heart_count = (TextView) myView.findViewById(R.id.txt_heart_my_past_answer_view);
        heart_count.setText(mData.get(position).mcount);

        return myView;
    }
}

class Past_MyData {
    String manswer;
    String mdate;
    int masopen;
    int mimg_location;
    String mlocation;
    String mcount;

    Past_MyData(String answer, String date, int asopen, int img_location, String location, String count) {
        mlocation = location;
        manswer = answer;
        mdate = date;
        masopen = asopen;
        mimg_location = img_location;
        mcount = count;
    }
}
