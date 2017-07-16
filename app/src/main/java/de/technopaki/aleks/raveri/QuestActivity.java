package de.technopaki.aleks.raveri;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by aleks on 11.07.17.
 */

public class QuestActivity extends android.support.v4.app.Fragment implements CustomButtonListener{

    ListView task_list;
    ProgressBar levelProgress;
    QuestListAdapter questAdapter;
    TextView level_textview;
    SwipeRefreshLayout refreshLayout;
    SQLiteDatabase task_database;
    ArrayList<String> questList;
    int level = 1;

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
        questAdapter.setCustomButtonListner(this);
        level_textview.setText("Level " + level);

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
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onButtonClickListener(int position, String value, TextView view) {
        Toast.makeText(getContext(),view.getText().toString(), Toast.LENGTH_LONG).show();
    }

    void readFromDatabase() {
        final TasksDatabase database = new TasksDatabase(this.getContext());
        task_database = database.getWritableDatabase();
        Cursor cursor = task_database.rawQuery("SELECT name FROM tasks WHERE priority='high'", null);
        questList.clear();

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    questList.add(name);
                    questAdapter.notifyDataSetChanged();
                } while(cursor.moveToNext());
            }
        }

        task_database.close();
    }

}
