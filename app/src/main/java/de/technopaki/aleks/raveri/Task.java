package de.technopaki.aleks.raveri;

import java.util.Date;

/**
 * Created by aleks on 14.07.17.
 */

public class Task {
    String name;
    String priority;
    String date_to;

    public Task(String name, String priority, String date_to) {
        this.name = name;
        this.priority = priority;
        this.date_to = date_to;
    }

}
