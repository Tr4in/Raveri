package de.technopaki.aleks.raveri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by aleks on 11.07.17.
 */

public class TaskActivity extends android.support.v4.app.Fragment {

    EditText input_text_view;
    ListView task_list;
    Button add_button;
    ArrayList<String> tasks;
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_layout, container, false);
        input_text_view = (EditText) view.findViewById(R.id.activity_input_field);
        task_list = (ListView) view.findViewById(R.id.task_list);
        add_button = (Button) view.findViewById(R.id.addButton);
        tasks = new ArrayList<>();

        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.task_item, tasks);
        adapter.notifyDataSetChanged();
        task_list.setAdapter(adapter);

        // Set onClick for the add_button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasks.add(input_text_view.getText().toString());
            }
        });

        return view;
    }
}
