package de.technopaki.aleks.raveri;

import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by aleks on 11.07.17.
 */

public class TaskActivity extends android.support.v4.app.Fragment {

    EditText input_text_view;
    ListView task_list;
    Button add_button;
    ArrayList<String> tasks;
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_layout, container, false);
        input_text_view = (EditText) view.findViewById(R.id.activity_input_field);
        task_list = (ListView) view.findViewById(R.id.task_list);
        add_button = (Button) view.findViewById(R.id.addButton);
        tasks = new ArrayList<>();

        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.task_item, tasks);
        adapter.notifyDataSetChanged();
        task_list.setAdapter(adapter);

        // Set onClick for the add_button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()) {
                    writeIntoXMLFile();
                    tasks.add(input_text_view.getText().toString());
                }
            }
        });

        return view;

    }

    public Boolean isInputValid() {
        String input_text = input_text_view.getText().toString();
        return !input_text.isEmpty() && !input_text.matches(" ");
    }

    public void writeIntoXMLFile() {

        try {
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            // Set output writer
            xmlSerializer.setOutput(writer);

            // Start Document
            xmlSerializer.startDocument("UTF-8", true);

            // Open tag task
            xmlSerializer.startTag("", "task");

            /****************** Task name ********************************/
            xmlSerializer.startTag("", "name");
            xmlSerializer.text(input_text_view.getText().toString());
            xmlSerializer.endTag("", "name");
            /*************************************************************/

            /***************** Pryority Tag *****************************/
            xmlSerializer.startTag("", "pryority");
            xmlSerializer.text("high");
            xmlSerializer.endTag("", "pryority");
            /***************************************************/

            /******************** Date to ***********************/
            xmlSerializer.startTag("", "date_to");
            xmlSerializer.text("2015-12-19");
            xmlSerializer.endTag("", "date_to");
            /****************************************************/

            xmlSerializer.endTag("", "task");
            xmlSerializer.endDocument();

            // After adding creatig a node
            File file = new File(getContext().getFilesDir() + File.separator + "saved_tasks.xml");

            if(file.exists()) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                fileOutputStream.write(writer.toString().getBytes());

                fileOutputStream.close();
                input_text_view.setText("Success");
            }

        }

        catch(FileNotFoundException ex) {
            ex.printStackTrace();
        }

        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
