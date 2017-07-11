package de.technopaki.aleks.raveri;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleks on 11.07.17.
 */

public class QuestAdapter extends BaseAdapter {
    ArrayList<String> quests;

    QuestAdapter() {
        quests = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }
}
