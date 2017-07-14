package de.technopaki.aleks.raveri;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by aleks on 11.07.17.
 */

public class QuestActivity extends android.support.v4.app.Fragment {

    ListView task_list;
    ProgressBar levelProgress;
    ArrayAdapter<String> tasks;
    TextView level;
    SwipeRefreshLayout refreshLayout;
    SQLiteDatabase task_database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quests_layout, container, false);
        task_list = (ListView) view.findViewById(R.id.task_list);
        level = (TextView) view.findViewById(R.id.level);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        tasks = new ArrayAdapter<String>(view.getContext(), R.layout.quest_item);
        task_list.setAdapter(tasks);

        readFromDatabase();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        return view;
    }

    void readFromDatabase() {
        final TasksDatabase database = new TasksDatabase(this.getContext());
        task_database = database.getWritableDatabase();
        Cursor cursor = task_database.rawQuery("SELECT name FROM tasks", null);

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    tasks.add(name);
                    tasks.notifyDataSetChanged();
                } while(cursor.moveToNext());
            }
        }

        task_database.close();
    }

}
