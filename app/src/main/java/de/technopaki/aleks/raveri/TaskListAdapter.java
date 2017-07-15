package de.technopaki.aleks.raveri;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aleks on 14.07.17.
 */

public class TaskListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> items;
    TaskButtonListener customListner;

    class ViewHolder {
        TextView text;
        Button button;
    }


    public TaskListAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.task_item, items);
        this.context = context;
        this.items = items;
    }


    public void setCustomButtonListner(TaskButtonListener listener) {
        this.customListner = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.task_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.name_of_task);
            viewHolder.button = (Button) convertView.findViewById(R.id.deleteButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String name_of_task = getItem(position);
        viewHolder.text.setText(name_of_task);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (customListner != null) {
                    //customListner.onButtonClickListener(position,name_of_task, viewHolder.);
                }

            }
        });

        return convertView;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
