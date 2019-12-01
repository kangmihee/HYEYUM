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
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;

public class Fragment_four extends Fragment {
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ASNUMBER = "asnumber";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_DATE = "date";
    private static final String TAG_ASOPEN = "asopen";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_COUNT = "count";

    String myJSON;
    JSONArray ShowAnswer = null;
    ArrayList<MyData> data;
    ListView listView;
    MyAdapter adatper;
    static String email;
    static String count;
    int asnumber;
    FragmentManager fragmentManager;
    SwipeRefreshLayout my_SwipeRefreshLayout;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = (View) inflater.inflate(R.layout.frament_four, container, false);
        fragmentManager = getFragmentManager();

        //main에서 이메일 받아오기
        email = ((MainActivity) getActivity()).sendtoemail(getContext());
        data = new ArrayList<MyData>();
        listView = (ListView) rootView.findViewById(R.id.listView);

        //로그인된 이메일로 자신이 쓴 글 가져오기
        sendToemailandSelectmygeul(email);

        return rootView; //화면전개
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
                Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
                HY_my_answer_view my_answer = new HY_my_answer_view();

                //list에서 선택한 아이템의 값으로 tag값을 이용하여 화면 전환시키기
                if (curFragment.getTag() == "my_list") {
                    fragmentTransaction.replace(R.id.frgment_mylist, my_answer, "my_answer").commit();
                    asnumber = data.get(position).masnumber;
                    bundle.putInt("asnumber", asnumber);
                    my_answer.setArguments(bundle);
                } //end if
            } //end onItemClick
        });

        //새로고침
        my_SwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_my_layout);
        my_SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data.clear(); //data지우기
                sendToemailandSelectmygeul(email); //data 새로받아오기
                my_SwipeRefreshLayout.setRefreshing(false);
            } //end onRefresh
        }); //end OnRefreshListener

        super.onActivityCreated(savedInstanceState);
    } //end onActivityCreated


    protected void showList() {
        try {
            //서버에서 온 json형식의 값을 받아서 처리하기
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);

                asnumber = c.getInt(TAG_ASNUMBER);
                String question = c.getString(TAG_QUESTION);
                String date = c.getString(TAG_DATE);
                int asopen = c.getInt(TAG_ASOPEN);
                String location = c.getString(TAG_LOCATION);
                count = c.getString(TAG_COUNT);

                //값들에 따라서 list아이콘 설ㅈ어해서 화면에 나타내기
                if (!location.equals("") && asopen == 1 && question != null && date != null) {
                    data.add(new MyData(asnumber, question, date, R.drawable.lock, R.drawable.location_full, count));
                } else if (!location.equals("") && asopen == 0 && question != null && date != null) {
                    data.add(new MyData(asnumber, question, date, R.drawable.unlock, R.drawable.location_full, count));
                } else if (location.equals("") && asopen == 0 && question != null && date != null) {
                    data.add(new MyData(asnumber, question, date, R.drawable.unlock, R.drawable.location_line, count));
                } else if (location.equals("") && asopen == 1 && question != null && date != null) {
                    data.add(new MyData(asnumber, question, date, R.drawable.lock, R.drawable.location_line, count));
                } //end if
            } //end for
            adatper = new MyAdapter(getContext(), R.layout.hy_my_answer_list, data);
            listView.setAdapter(adatper);

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showList


    private void sendToemailandSelectmygeul(String email) {
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
                    String email = (String) params[0]; //서버로 보내는 값

                    String link = "http://hymanager.dothome.co.kr/hy_answerlistgetdata.php";
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

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
                } //end try

            } //end doInBackground

        } //end InsertData

        InsertData task = new InsertData();
        task.execute(email);
    } //end sendToemailandSelectmygeul

} //end Fragment_four

class MyAdapter extends BaseAdapter {

    Context mContext;
    int mLayout;
    ArrayList<MyData> mData;

    MyAdapter(Context context, int layout, ArrayList<MyData> data) {
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
        if (convertView == null) { //view 재활용
            myView = View.inflate(mContext, mLayout, null);
        } else {
            myView = convertView;
        } //end if

        TextView Text_question = (TextView) myView.findViewById(R.id.txt_mylist_question);
        TextView Text_date = (TextView) myView.findViewById(R.id.relative_date);
        ImageView imageView_lock = (ImageView) myView.findViewById(R.id.img_mylist_lock);
        ImageView imageView_loc = (ImageView) myView.findViewById(R.id.img_mylist_location);

        Text_question.setText(mData.get(position).mquestion);
        Text_date.setText(mData.get(position).mdate);
        imageView_lock.setImageResource(mData.get(position).masopen);
        imageView_loc.setImageResource(mData.get(position).mlocation);

        TextView heart_count = (TextView) myView.findViewById(R.id.txt_mylist_count);
        heart_count.setText(mData.get(position).mcount);

        return myView;
    }
}

class MyData {
    int masnumber;
    String mquestion;
    String mdate;
    int masopen;
    int mlocation;
    String mcount;


    MyData(int asnumber, String question, String date, int asopen, int location, String count) {
        masnumber = asnumber;
        mquestion = question;
        mdate = date;
        masopen = asopen;
        mlocation = location;
        mcount = count;
    }
}
