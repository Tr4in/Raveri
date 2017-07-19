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
    QuestItemListener questItemListener;

    static class ViewHolder {
        TextView quest;
        TextView time;
        Button finished;
        Button recordButton;
        CountDownTimer countDownTimer;
    }


    public QuestListAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.task_item, items);
        this.context = context;
        this.items = items;
    }


    public void setQuestItemListener(QuestItemListener listener) {
        this.questItemListener = listener;
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
            viewHolder.recordButton = (Button) convertView.findViewById(R.id.quest_record_button);
            viewHolder.finished = (Button) convertView.findViewById(R.id.quest_finish_button);
            viewHolder.countDownTimer = null;

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();


        final String name_of_task = getItem(position);
        viewHolder.quest.setText(name_of_task);

        viewHolder.recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questItemListener != null) {
                    questItemListener.onButtonRecordClick(position,viewHolder.quest.getText().toString(), viewHolder.time, viewHolder.recordButton, viewHolder.countDownTimer,  viewHolder.finished);
                }
            }
        });

        viewHolder.finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questItemListener != null) {
                    questItemListener.onButtonFinishClick(position, viewHolder.quest.getText().toString());
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
