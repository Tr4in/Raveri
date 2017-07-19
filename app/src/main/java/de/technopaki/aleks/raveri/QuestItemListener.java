package de.technopaki.aleks.raveri;

import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by aleks on 16.07.17.
 */

interface QuestItemListener {
    void onButtonFinishClick(int position, String value);
    void onButtonRecordClick(int position, String value, TextView time_output, Button recordButton, CountDownTimer timer, Button finish_button);
}
