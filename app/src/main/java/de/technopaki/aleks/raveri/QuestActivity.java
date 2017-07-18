package de.technopaki.aleks.raveri;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


/**
 * Created by aleks on 11.07.17.
 */

public class QuestActivity extends android.support.v4.app.Fragment implements QuestItemListener {

    ListView task_list;
    ProgressBar levelProgress;
    QuestListAdapter questAdapter;
    TextView level_textview;
    SwipeRefreshLayout refreshLayout;
    SQLiteDatabase task_database;
    ArrayList<String> questList;
    int currentLevel = 1;
    float maxExp = 0;
    float currentExp = 0;
    String myPref = "LEVEL_INFORMATION";
    CountDownTimer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quests_layout, container, false);
        task_list = (ListView) view.findViewById(R.id.task_list);
        level_textview = (TextView) view.findViewById(R.id.level_textview);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        levelProgress = (ProgressBar) view.findViewById(R.id.progressBar);
        questList = new ArrayList<>();

        questAdapter = new QuestListAdapter(view.getContext(), questList);
        task_list.setAdapter(questAdapter);
        questAdapter.setQuestItemListener(this);

        loadLevelInformationFromPref();
        level_textview.setText("Level " + currentLevel);

        readFromDatabase();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        readFromDatabase();
                    }
                }, 1000);
            }
        });

        return view;
    }

    public void loadLevelInformationFromPref() {
        SharedPreferences preferences = getContext().getSharedPreferences(myPref, Context.MODE_PRIVATE);

        currentLevel = preferences.getInt("level", 0);
        currentExp = preferences.getFloat("currentExp", 0);
        maxExp = preferences.getFloat("maxExp", 0);

        int progress = (int)((currentExp / maxExp) * 100);
        levelProgress.setProgress(progress);
    }

    public void writeLevelInformationToPref() {

        SharedPreferences preferences = getContext().getSharedPreferences(myPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("level", currentLevel);
        editor.putFloat("currentExp", currentExp);
        editor.putFloat("maxExp", maxExp);

        editor.apply();
    }

    @Override
    public void onButtonRecordClick(String value, final TextView time_output, Button recordButton) {
        final TasksDatabase database = new TasksDatabase(this.getContext());
        int timerCount = 1;

        try {
            task_database = database.getWritableDatabase();

            Cursor cursor = task_database.rawQuery("SELECT date_to FROM tasks WHERE name='" + value + "'", null);

            if (cursor.moveToFirst()) {
                String time = cursor.getString(cursor.getColumnIndex("date_to"));
                timerCount = Integer.parseInt(time);

            }
        } catch(SQLException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Disable button - no return :)
        recordButton.setEnabled(false);
        recordButton.setBackgroundColor(getResources().getColor(R.color.colorDark));

        // Start a countdown here
        timer = new CountDownTimer(timerCount * 3600 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                time_output.setText("" + l / 1000);
            }

            @Override
            public void onFinish() {
                currentExp =- maxExp * 0.5f;

                if(currentExp < 0) {
                    if(currentLevel > 0)
                        --currentLevel;

                    currentExp = 0;
                }

                levelProgress.setProgress((int)currentExp);
                level_textview.setText("Level " + currentLevel);
                time_output.setText("FAILED");
            }
        }.start();
    }

    @Override
    public void onButtonFinishClick(String value) {

        if(timer != null)
            timer.cancel();

        final TasksDatabase database = new TasksDatabase(this.getContext());

        try {
            task_database = database.getWritableDatabase();

            Cursor cursor = task_database.rawQuery("SELECT priority FROM tasks WHERE name='" + value + "'", null);

            if(cursor.moveToFirst()) {
                String priority = cursor.getString(cursor.getColumnIndex("priority"));

                switch(priority) {

                    case "high":
                        currentExp += 0.30f * maxExp;
                        break;

                    case "medium":
                        currentExp += 0.22f * maxExp;
                        break;

                    case "low":
                        currentExp += 0.15f * maxExp;
                        break;

                }

                if(currentExp > maxExp) {
                    ++currentLevel;
                    currentExp = currentExp - maxExp;
                    maxExp *= 2;
                    level_textview.setText("Level " + currentLevel);
                }

                int progress = (int)((currentExp / maxExp) * 100);
                levelProgress.setProgress(progress);
                writeLevelInformationToPref();

            }

            task_database.close();

        } catch(SQLException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    void readFromDatabase() {
        final TasksDatabase database = new TasksDatabase(this.getContext());
        task_database = database.getWritableDatabase();
        Cursor cursor = task_database.rawQuery("SELECT name FROM tasks;", null);
        questList.clear();

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    questAdapter.add(name);
                    questAdapter.notifyDataSetChanged();
                } while(cursor.moveToNext());
            }
        }

        task_database.close();
    }

}
