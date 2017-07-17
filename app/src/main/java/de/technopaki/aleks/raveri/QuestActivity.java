package de.technopaki.aleks.raveri;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        loadLevelInformationFromFile();
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

    public void loadLevelInformationFromFile() {
        // Open file
        InputStream txtFileInputStream = getResources().openRawResource(R.raw.level_information);

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(txtFileInputStream));

            String line = bufferedReader.readLine();
            String[] level_information = line.split(";");

            currentLevel = Integer.parseInt(level_information[0]);
            currentExp = Float.parseFloat(level_information[1]);
            maxExp = Float.parseFloat(level_information[2]);

            int progress = (int)((currentExp / maxExp) * 100);
            levelProgress.setProgress(progress);

        } catch(IOException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void writeLevelInformationToFile() {

        try {

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Raveri";
            File level_information = new File(getContext().getExternalFilesDir(null) + File.separator + "level_information.txt");

            String information = currentLevel + ";" + currentExp + ";" + maxExp + ";";

            Files.write(information.getBytes(), level_information);

        } catch(FileNotFoundException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onButtonRecordClick(String value, final TextView time_output) {
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

        // Start a countdown here

        new CountDownTimer(timerCount * 1000, 1000) {
            @Override
            public void onTick(long l) {
                time_output.setText("" + l);
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
                writeLevelInformationToFile();
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
