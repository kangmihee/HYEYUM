package com.jops1.hyeyum_1;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

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

public class HY_my_answer_view extends Fragment {
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ANSWER = "answer";
    private static final String TAG_QUESTION = "question";
    private static final String TAG_DATE = "date";
    private static final String TAG_ASOPEN = "asopen";
    private static final String TAG_COUNT = "count";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_SAVETIME = "savetime";
    private ImageButton menu_btn;
    private ImageButton back_list_btn;

    JSONArray ShowAnswer = null;
    String myJSON;
    String email;
    String strDate;
    String asnumber_str;
    Integer asnumber;
    String answercount;
    int pastanswercount;
    View rootView;
    FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(R.layout.activity_hy_my_answer_view, container, false);

        //main에서 이메일 받아오기
        email = ((MainActivity) getActivity()).sendtoemail(getActivity());
        asnumber = getArguments().getInt("asnumber"); //전 화면에서 넘어온 값
        asnumber_str = Integer.toString(asnumber);
        sendToasnumberandSelectgeul(asnumber_str);
        pastcount(asnumber_str, email); //과거 답 갯수 확인 함수

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault());
        strDate = dateFormat.format(date);

        TextView mytextview = (TextView) rootView.findViewById(R.id.txt_my_answer_view);
        mytextview.setMovementMethod(ScrollingMovementMethod.getInstance());
        mytextview.setVerticalScrollBarEnabled(false); //글이 길어졌을 경우, 스크롤 하면서 볼 수 있게

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        back_list_btn = (ImageButton) getView().findViewById(R.id.btn_back_my_answer_view);
        fragmentManager = getFragmentManager();
        Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
        Fragment three_curFragment = fragmentManager.findFragmentById(R.id.frgmentlist_three);

        String tag_three = three_curFragment.getTag();

        //list에서 클릭하여 들어온 경우에만 Back버튼 화면에 보여지게
        if (tag_three.equals("three_my_answer") && curFragment == null || !(curFragment.getTag()).equals("my_answer")) {
            back_list_btn.setVisibility(View.INVISIBLE);
        }

        back_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
                Fragment_four four = new Fragment_four();

                if (curFragment.getTag() == "my_answer") { //네번째화면에서 들어온 경우
                    fragmentTransaction.replace(R.id.frgment_mylist, four, "my_list").commit();
                } //end if
            } //end onClick
        });
        super.onActivityCreated(savedInstanceState);

        // 메뉴 버튼 클릭시 수정, 삭제할 수 있는 커스텀 다이얼로그 띄우기
        menu_btn = (ImageButton) rootView.findViewById(R.id.btn_menu);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dlg = new Dialog(getContext()); //다이얼로그 호출
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(R.layout.custom_dialog);
                dlg.show();

                Button update_btn = (Button) dlg.findViewById(R.id.updateButton);
                Button delete_btn = (Button) dlg.findViewById(R.id.deleteButton);

                //수정을 눌렀을 경우
                update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
                        Fragment three_curFragment = fragmentManager.findFragmentById(R.id.frgmentlist_three);
                        HY_update update = new HY_update();

                        if (curFragment.getTag() == "my_answer") { //네번째 화면일 경우
                            bundle.putInt("asnumber", asnumber);
                            update.setArguments(bundle);
                            fragmentTransaction.replace(R.id.frgment_mylist, update, "my_update").commit();
                        } else if (three_curFragment.getTag() == "three_my_answer") { //세번째 화면일 경우
                            bundle.putInt("asnumber", asnumber);
                            update.setArguments(bundle);
                            fragmentTransaction.replace(R.id.frgmentlist_three, update, "update").commit();
                        } //end if
                        dlg.dismiss();
                    } //end update_onClick
                });

                //삭제를 눌렀을 경우
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dlgdel = new Dialog(getContext());
                        dlgdel.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dlgdel.setContentView(R.layout.custom_dialog_delete);
                        dlgdel.show();

                        Button ok_btn = (Button) dlgdel.findViewById(R.id.okButton);
                        Button no_btn = (Button) dlgdel.findViewById(R.id.noButton);

                        ok_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { //확인버튼을 눌렀을 때
                                Answerdelete(asnumber_str);
                                //((MainActivity)getActivity()).pageReload();
                                dlgdel.dismiss();
                            }
                        });

                        no_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { //취소일 경우
                                dlgdel.dismiss();
                            }
                        });
                        dlg.dismiss();
                    } //end delete_onClick
                });
            } //end onClick
        });
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
                String date = c.getString(TAG_DATE);
                int asopen = c.getInt(TAG_ASOPEN);
                String location = c.getString(TAG_LOCATION);
                String php_email = c.getString(TAG_EMAIL);
                String count = c.getString(TAG_COUNT);
                String savetime = c.getString(TAG_SAVETIME);

                if (!question.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_questiontitle_my_answer_view)).setText(question);
                }

                if (!answer.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_my_answer_view)).setText(answer);
                }

                if (!date.equals("")) {
                    ((TextView) rootView.findViewById(R.id.txt_date_my_answer_view)).setText(date);
                }

                if (!location.equals("")) {

                    ((TextView) rootView.findViewById(R.id.txt_loc_my_answer_view)).setText(location);
                }

                if (php_email.equals(email) && (strDate.equals(date))) {
                    menu_btn.setVisibility(View.VISIBLE);
                }

                ((TextView) rootView.findViewById(R.id.txt_heart_my_answer_view)).setText(count);
                ((TextView) rootView.findViewById(R.id.relative_my_answer_savetime)).setText(savetime);

            } //end for

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showList

    //리스트에서 넘어온 기본키로 화면에 필요한 것들 가져오는 함수
    private void sendToasnumberandSelectgeul(String asnumber) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s; //서버에서 온 값 저장
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
    } //end sendToasnumberandSelectgeul

    private void Answerdelete(String asnumber) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {

                myJSON = s;
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);
                Fragment_four list_four = new Fragment_four();
                Fragment_three three = new Fragment_three();

                //삭제 후에 화면 전환
                Fragment three_curFragment = fragmentManager.findFragmentById(R.id.frgmentlist_three);
                if (curFragment.getTag() == "my_answer") {
                    fragmentTransaction.replace(R.id.frgment_mylist, list_four, "my_list").commit();
                } else if (three_curFragment.getTag() == "three_my_answer") {
                    fragmentTransaction.replace(R.id.frgmentlist_three, three, "three").commit();
                }

                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show(); //삭제되었다고 toast로 알려주기
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String asnumber = (String) params[0];

                    String link = "http://hymanager.dothome.co.kr/hy_AnswerDelete.php";
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
    } //end Answerdelete

    //과거답변 여부 체크하여 아이콘을 하단에 표시하고 터치모션 추가하는 함수
    private void pastcount(String asnumber, String email) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                myJSON = s;
                showcount();
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String asnumber = (String) params[0];
                    String email = (String) params[1];

                    String link = "http://hymanager.dothome.co.kr/hy_MyPastAnswer_Count.php";
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
    } //end pastcount

    protected void showcount() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            ShowAnswer = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < ShowAnswer.length(); i++) {
                JSONObject c = ShowAnswer.getJSONObject(i);

                answercount = c.getString(TAG_ANSWER);
                pastanswercount = Integer.parseInt(answercount);

                //과거 답변이 체크되어있는지를 확인
                Boolean ch_tr = ((MainActivity) getActivity()).past_alarm(getContext());

                if (ch_tr == true && pastanswercount >= 2) { //과거답변 알람이 켜져있으면서 과거 답이 2개 이상일 경우

                    ImageView down_1 = (ImageView) getActivity().findViewById(R.id.down_gif);
                    GlideDrawableImageViewTarget gifimage = new GlideDrawableImageViewTarget(down_1);
                    Glide.with(this).load(R.drawable.down_1).into(gifimage);

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
                                        //화면을 아래에서 위로 스와이프 해서 과거 답변 보기
                                        if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                            Bundle bundle = new Bundle();
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                            HY_my_past_answer_list my_past_answer = new HY_my_past_answer_list();
                                            Fragment three_curFragment = fragmentManager.findFragmentById(R.id.frgmentlist_three);
                                            Fragment curFragment = fragmentManager.findFragmentById(R.id.frgment_mylist);

                                            if (three_curFragment.getTag() == "three_my_answer") {
                                                fragmentTransaction
                                                        .setCustomAnimations(R.anim.slide_up, R.anim.slide_out_up)
                                                        .replace(R.id.frgmentlist_three, my_past_answer, "three_my_past_answer")
                                                        .commit();
                                                bundle.putInt("asnumber", asnumber);
                                                my_past_answer.setArguments(bundle);
                                            } else if (curFragment.getTag() == "my_answer") {
                                                fragmentTransaction
                                                        .setCustomAnimations(R.anim.slide_up, R.anim.slide_out_up)
                                                        .replace(R.id.frgment_mylist, my_past_answer, "four_my_past_answer")
                                                        .commit();
                                                bundle.putInt("asnumber", asnumber);
                                                my_past_answer.setArguments(bundle);
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
                } //end if
            } //end for

        } catch (JSONException e) {
            e.printStackTrace();
        } //end try
    } //end showcount
} //end HY_my_answer_view