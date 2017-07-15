package de.technopaki.aleks.raveri;

import android.content.Context;
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

public class TaskActivity extends android.support.v4.app.Fragment implements CustomButtonListener {

    EditText input_text_view;
    ListView task_list;
    Button add_button;
    SQLiteDatabase task_database;
    TaskListAdapter tasks;
    ArrayList<String> task_names;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_layout, container, false);
        task_list = (ListView) view.findViewById(R.id.task_list);
        add_button = (Button) view.findViewById(R.id.addButton);
        task_names = new ArrayList<>();

        tasks = new TaskListAdapter(getContext(),task_names);
        tasks.setCustomButtonListner(this);
        task_list.setAdapter(tasks);

        final TasksDatabase database = new TasksDatabase(this.getContext());
        readFromDatabase();

        // Set onClick for the add_button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = new Task();
                task.name = "Hallo";
                task.priority = "high";
                task.date_to = "2015-05-26";
                database.addNewTask(task);
                readFromDatabase();
            }
        });

        return view;
    }

    @Override
    public void onButtonClickListener(int position, String value, View view) {
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

}