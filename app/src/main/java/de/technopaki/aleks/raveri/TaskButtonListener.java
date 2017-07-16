package de.technopaki.aleks.raveri;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by aleks on 15.07.17.
 */

public interface TaskButtonListener {
    void onButtonChangeListener(String value);
    void onButtonDeleteListener(int position, String value);
}
