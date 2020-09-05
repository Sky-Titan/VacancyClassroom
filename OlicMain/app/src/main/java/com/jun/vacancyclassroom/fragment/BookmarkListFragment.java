package com.jun.vacancyclassroom.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.activity.TimeTableActivity;
import com.jun.vacancyclassroom.adapter.BookmarkListAdapter;
import com.jun.vacancyclassroom.database.DatabaseLibrary;
import com.jun.vacancyclassroom.item.BookMarkedRoom;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;


public class BookmarkListFragment extends Fragment {

    private LinearLayout layout_time;
    private Button currentTime,visibility_time;

    private BookmarkListAdapter adapter;
    private ListView listView;

    private ArrayList<String> checkedlist = new ArrayList<>();

    private AdView mAdView;

    private View view;

    private TimePicker timePicker;
    private NumberPicker dayPicker;

    private DatabaseLibrary databaseLibrary;
    private MainViewModel viewModel;

    public BookmarkListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();

        //화면 돌아올 때 북마크 리스트 새로 구성
  /*      if(listView!=null)
        {
            adapter=new BookmarkListAdapter();

            listView.setAdapter(adapter);
            checkedlist=new ArrayList<String>();
            loadList();
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bookmarklist,container,false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        /*  databaseLibrary = DatabaseLibrary.getInstance(null);

        Calendar now = setTimePicker();

        setDayPicker(now);

        setCurrentTime();

        layout_time = (LinearLayout) view.findViewById(R.id.linearLayout_time);

        setVisibilityTime();

        setAdView();

        setListView();

        loadList();*/
        return view;
    }

 /*   private void setListView() {
        adapter=new BookmarkListAdapter();
        listView=(ListView)view.findViewById(R.id.searchlist_b);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            BookMarkedRoom item=(BookMarkedRoom)adapter.getItem(i);

            Intent intent = new Intent(getContext(), TimeTableActivity.class);
            intent.putExtra("classroom",item.getLecture_room());
            intent.putExtra("isBuilding",false);
            startActivity(intent);
        });
    }

    private void setVisibilityTime() {
        visibility_time = (Button) view.findViewById(R.id.visible_btn);
        visibility_time.setOnClickListener(view -> {

            if(layout_time.getVisibility() == View.VISIBLE)//끄기
            {
                layout_time.setVisibility(View.GONE);
                visibility_time.setText("시간 설정 보이기");
                visibility_time.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.ic_arrow_drop_down_black_36dp),null);
            }
            else//켜기
            {
                layout_time.setVisibility(View.VISIBLE);
                visibility_time.setText("시간 설정 숨기기");
                visibility_time.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.ic_arrow_drop_up_black_36dp),null);
            }
        });
    }

    private void setAdView() {
        mAdView = (AdView) view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void setCurrentTime() {
        currentTime = (Button) view.findViewById(R.id.currentTime_btn);
        currentTime.setOnClickListener(view -> {

                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                Calendar now = Calendar.getInstance(timeZone);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
                dayPicker.setValue(now.get(Calendar.DAY_OF_WEEK));

                adapter=new BookmarkListAdapter();
                listView.setAdapter(adapter);
                loadList();
        });
    }

    private void setDayPicker(Calendar now) {
        dayPicker = (NumberPicker) view.findViewById(R.id.dayPicker);
        dayPicker.setDisplayedValues(new String[]{"일","월","화","수","목","금","토"});
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(7);
        dayPicker.setValue(now.get(Calendar.DAY_OF_WEEK));
        dayPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                adapter=new BookmarkListAdapter();
                listView.setAdapter(adapter);
                loadList();
        });
    }

    private Calendar setTimePicker() {
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);//타임픽커 현재 시간으로 설정
        timePicker.setIs24HourView(true);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar now = Calendar.getInstance(timeZone);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        timePicker.setHour(hour);
        timePicker.setMinute(minute);

        timePicker.setOnTimeChangedListener((timePicker, i, i1) -> {
                adapter=new BookmarkListAdapter();
                listView.setAdapter(adapter);
                loadList();
        });

        return now;
    }

    public void loadList(){

        new AsyncTask<Void, Object, Void>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                checkedlist.clear();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor c = databaseLibrary.selectBookmarkList();

                while (c.moveToNext()){
                    String classroom=c.getString(0);
                    checkedlist.add(classroom);
                }
                c.close();

                int count=0;

                c = databaseLibrary.selectLectureRoomList();

                while(c.moveToNext())
                {
                    String classroom=c.getString(0);
                    String time=c.getString(1);

                    for(int i=0;i<checkedlist.size();i++)
                    {
                        //즐겨찾기에 추가 되어잇으면 추가
                        if(checkedlist.get(i).equals(classroom))
                        {
                            publishProgress(classroom, time, Color.RED);
                            count++;
                            break;
                        }
                    }

                    if(count==checkedlist.size())
                        break;
                }

                c.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);
                adapter.addItem((String)values[0], (String)values[1], (int) Color.RED);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                int day = dayPicker.getValue();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                for(int i=0;i<checkedlist.size();i++)
                {
                    BookMarkedRoom item=(BookMarkedRoom)adapter.getItem(i);
                    listView.setItemChecked(i,true);//전부 체크 시켜주기

                    //이용가능시 초록색
                    if(classification(item.getLecture_room(),day,hour,minute)==true)
                        item.setButton_color(Color.GREEN);
                        //이용불가시 빨간색
                    else
                        item.setButton_color(Color.RED);
                }
            }
        }.execute();
    }

    //해당 교실이 현재 시간에 이용가능한지 판단
    private boolean classification(String lectureRoom, int day_n, int hour_n, int minute_n)
    {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                boolean isPossible = true;

                Cursor c = databaseLibrary.selectLectureRoomList(lectureRoom);

                String day1=dayToKorean(day_n);

                String hour=String.valueOf(hour_n);

                String minute=String.valueOf(minute_n);
                System.out.println(day1+hour+":"+minute);

                String day_today = day1;//현재요일
                String hour_today = hour;//현재시간
                String minute_today = minute;//현재분

                while (c.moveToNext()) {//지정된 classroom 에 왔다고 가정

                    String classroom = c.getString(0);
                    String time = c.getString(1);

                    StringTokenizer tokens = new StringTokenizer(time, " ");

                    String[] times = new String[tokens.countTokens()];

                    String before_hour = "", before_minute = "";
                    String day = "";
                    String after_hour = "", after_minute = "";

                    for (int i = 0; i < times.length; i++)
                    {
                        if (i == 0)
                        {
                            times[i] = tokens.nextToken();//ex)화16:00

                            day = times[i].substring(0, 1);
                            before_hour = times[i].substring(1, 3);
                            before_minute = times[i].substring(4, 6);
                        }
                        else if (i % 3 == 0)//새로운 시간대 beforetime(시작시간)이랑 요일구하기
                        {
                            times[i] = tokens.nextToken();//ex)화16:00

                            day = times[i].substring(0, 1);
                            before_hour = times[i].substring(1, 3);
                            before_minute = times[i].substring(4, 6);
                        }
                        else if (i % 3 == 2)//aftertime(종료시간) 구하기
                        {
                            times[i] = tokens.nextToken();// ex)16:00
                            after_hour = times[i].substring(0, 2);
                            after_minute = times[i].substring(3, 5);

                            //숫자로변경
                            int before_hour_num = Integer.parseInt(before_hour);
                            int hour_today_num = Integer.parseInt(hour_today);
                            int after_hour_num = Integer.parseInt(after_hour);
                            int before_minute_num = Integer.parseInt(before_minute);
                            int minute_today_num = Integer.parseInt(minute_today);
                            int after_minute_num = Integer.parseInt(after_minute);

                            //현재 강의실 이용가능 한지 구분 시작
                            //요일이 같으면 그다음 단계
                            if (day.equals(day_today))
                            {
                                if (before_hour_num < hour_today_num && hour_today_num < after_hour_num)//현재시간이 사이에 있다면 이용불가
                                    isPossible = false;
                                else if (before_hour_num > hour_today_num && hour_today_num > after_hour_num)//현재시간이 밖에 있다면 이용가능
                                    isPossible = true;
                                else if (before_hour_num == hour_today_num)//before 시간과 같은 경우
                                {
                                    //before 분과 비교
                                    if (before_minute_num <= minute_today_num)//before minute보다 같거나 크면 이용불가
                                        isPossible = false;
                                        //작다면 이용가능
                                    else
                                        isPossible = true;

                                }
                                else if (after_hour_num == hour_today_num)//after 시간과 같은 경우
                                {
                                    if (after_minute_num >= minute_today_num)//after minute보다 같거나 작으면 이용불가
                                        isPossible = false;
                                    else
                                        isPossible = true;
                                }
                            }
                            else//요일다르면 그냥 다음단계로 넘어감
                                isPossible = true;

                        }
                        else if (i % 3 == 1)
                        {//~
                            times[i] = tokens.nextToken();
                        }

                        if (isPossible == false)//하나라도 이용불가라면 종료
                        {
                            c.close();
                            return isPossible;
                        }
                    }
                }
                c.close();

                return isPossible;
            }
        }.execute();

        boolean isPossible = true;

        try {
            isPossible= asyncTask.get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return isPossible;
    }

    public String dayToKorean(int day) {

        if (day==2)
            return "월";
        else if (day==3)
            return "화";
        else if (day==4)
            return "수";
        else if (day==5)
            return "목";
        else if (day==6)
            return "금";
        else if (day==7)
            return "토";
        else if (day==1)
            return "일";
        else
            return "";
    }*/
}
