package de.technopaki.aleks.raveri;

import android.widget.TextView;

/**
 * Created by aleks on 16.07.17.
 */

interface QuestItemListener {
    void onButtonFinishClick(String value);
    void onButtonRecordClick(String value, TextView time_output);
}
