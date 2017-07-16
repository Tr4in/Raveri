package de.technopaki.aleks.raveri;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by aleks on 11.07.17.
 */

public class TaskActivity extends android.support.v4.app.Fragment implements TaskButtonListener {

    ListView task_list;
    Button add_button;
    SQLiteDatabase task_database;
    TaskListAdapter tasks;
    ArrayList<String> task_names;
    LayoutInflater layoutInflater;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_layout, container, false);
        task_list = (ListView) view.findViewById(R.id.task_list);
        add_button = (Button) view.findViewById(R.id.addButton);
        task_names = new ArrayList<>();
        layoutInflater = inflater;

        tasks = new TaskListAdapter(getContext(),task_names);
        tasks.setCustomButtonListner(this);
        task_list.setAdapter(tasks);

        readFromDatabase();

        // Set onClick for the add_button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View dialog_view = layoutInflater.inflate(R.layout.add_new_task_dialog, null);

                builder.setView(dialog_view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                EditText dialog_text_input = (EditText) dialog_view.findViewById(R.id.dialog_task_input);
                                String task_name = dialog_text_input.getText().toString();

                                RadioGroup dialog_radio_group = (RadioGroup) dialog_view.findViewById(R.id.dialog_priority_radio_group);
                                RadioButton checkedRadioButton = (RadioButton) dialog_view.findViewById(dialog_radio_group.getCheckedRadioButtonId());

                                EditText dialog_date_input = (EditText) dialog_view.findViewById(R.id.dialog_date_input);
                                String date_text = dialog_date_input.getText().toString();

                                // Create a new task object
                                Task task = new Task(task_name, checkedRadioButton.getText().toString(), date_text);
                                writeToDatabase(task);

                                readFromDatabase();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    public void writeToDatabase(Task task) {

        final TasksDatabase database = new TasksDatabase(this.getContext());
        SQLiteDatabase task_database = database.getWritableDatabase();

        try {
            task_database.execSQL("INSERT INTO tasks(name, priority, date_to) VALUES('" +
                    task.name + "','" + task.priority + "','" + task.date_to + "');");
        }
        catch (SQLException ex) {
           Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        task_database.close();
    }

    @Override
    public void onButtonDeleteListener(int position, String value) {
        final TasksDatabase database = new TasksDatabase(this.getContext());

        try {
            task_database = database.getWritableDatabase();
            task_database.execSQL("DELETE FROM tasks WHERE name='" + value + "'");
            tasks.remove(value);
            tasks.notifyDataSetChanged();

            task_database.close();
        }
        catch(SQLException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onButtonChangeListener(String value) {

        changeDatasetDialog(value);

        final TasksDatabase database = new TasksDatabase(this.getContext());

        try {
            task_database = database.getWritableDatabase();
            task_database.execSQL("UPDATE FROM tasks(name, priority, date_to) WHERE name='" + value + "'");
            tasks.remove(value);
            tasks.notifyDataSetChanged();

            task_database.close();
        }
        catch(SQLException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void readFromDatabase() {
        final TasksDatabase database = new TasksDatabase(this.getContext());
        task_database = database.getWritableDatabase();
        Cursor cursor = task_database.rawQuery("SELECT name FROM tasks", null);
        task_names.clear();

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    task_names.add(name);
                    //tasks.notifyDataSetChanged();
                } while(cursor.moveToNext());
            }
        }

        task_database.close();
    }

    void changeDatasetDialog(String sqlSelectValue) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View dialog_view = layoutInflater.inflate(R.layout.add_new_task_dialog, null);

        builder.setView(dialog_view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditText dialog_text_input = (EditText) dialog_view.findViewById(R.id.dialog_task_input);
                        String task_name = dialog_text_input.getText().toString();

                        RadioGroup dialog_radio_group = (RadioGroup) dialog_view.findViewById(R.id.dialog_priority_radio_group);
                        RadioButton checkedRadioButton = (RadioButton) dialog_view.findViewById(dialog_radio_group.getCheckedRadioButtonId());

                        EditText dialog_date_input = (EditText) dialog_view.findViewById(R.id.dialog_date_input);
                        String date_text = dialog_date_input.getText().toString();

                        // Create a new task object
                        Task task = new Task(task_name, checkedRadioButton.getText().toString(), date_text);
                        writeToDatabase(task);

                        readFromDatabase();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}