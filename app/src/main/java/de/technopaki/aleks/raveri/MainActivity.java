package de.technopaki.aleks.raveri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quests_layout);
/*
        GraphView money_situation_graph = (GraphView) findViewById(R.id.money_situation_graph);
        money_situation_graph.setBackgroundColor(getResources().getColor(R.color.colorDark));

        // Init the points!
        LineGraphSeries<DataPoint> money_states = new LineGraphSeries<>();
        money_states.setBackgroundColor(getResources().getColor(R.color.white));


        // Initialize the DataPoints
        for(int i = 1; i < 13; i++)
            money_states.appendData(new DataPoint(i, i*2), true, 12);

        money_situation_graph.addSeries(money_states);*/

        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        QuestAdapter questAdapter = new QuestAdapter();

        ListView questListView = (ListView) findViewById(R.id.listView);
        questListView.setAdapter(questAdapter);

        bar.setMax(100);
        bar.setProgress(50);

    }
}
