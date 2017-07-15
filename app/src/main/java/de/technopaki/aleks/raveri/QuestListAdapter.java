package de.technopaki.aleks.raveri;

import android.content.Context;
import android.os.CountDownTimer;
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

public class QuestListAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> items;
    CustomButtonListener customListner;

    class ViewHolder {
        TextView quest;
        TextView time;
        Button accept;
        Button abort;
    }


    public QuestListAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.task_item, items);
        this.context = context;
        this.items = items;
    }


    public void setCustomButtonListner(CustomButtonListener listener) {
        this.customListner = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.quest_item, null);
            viewHolder = new ViewHolder();
            viewHolder.quest = (TextView) convertView.findViewById(R.id.quest_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.quest_time);
            viewHolder.accept = (Button) convertView.findViewById(R.id.questAcceptButton);
            viewHolder.abort = (Button) convertView.findViewById(R.id.questAbortButton);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String name_of_task = getItem(position);
        viewHolder.quest.setText(name_of_task);
        viewHolder.accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (customListner != null) {
                    customListner.onButtonClickListener(position,name_of_task, view);

                    new CountDownTimer(30000, 1000) {
                        @Override
                        public void onTick(long l) {
                            viewHolder.time.setText("" + l);
                        }

                        @Override
                        public void onFinish() {
                            viewHolder.time.setText("nice!");
                        }
                    }.start();
                }

            }
        });

        viewHolder.abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customListner != null) {
                    customListner.onButtonClickListener(position,name_of_task, view);
                    viewHolder.time.setText("0");
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
