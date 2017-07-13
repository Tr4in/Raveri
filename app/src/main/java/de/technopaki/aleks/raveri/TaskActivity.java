package de.technopaki.aleks.raveri;

import android.content.Context;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

        readFromXmlFile();

        // Set onClick for the add_button
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid()) {
                    writeToXMLFile();
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

    public void writeToXMLFile() {

        try {
            Document xmlDoc = getDocument();

            if (xmlDoc == null)
                return;

            Element tasks = xmlDoc.getDocumentElement();
            Element newTask = xmlDoc.createElement("Task");

            /*********************** Child nodes *********************************/
            Element newName = xmlDoc.createElement("Name");
            Element newPryority = xmlDoc.createElement("Pryority");
            Element newDateTo = xmlDoc.createElement("Date_to");

            newName.appendChild(xmlDoc.createTextNode(input_text_view.getText().toString()));
            newPryority.appendChild(xmlDoc.createTextNode("high"));
            newDateTo.appendChild(xmlDoc.createTextNode("2015-12-16"));
            /***********************************************************************/

            newTask.appendChild(newName);
            newTask.appendChild(newPryority);
            newTask.appendChild(newDateTo);

            tasks.appendChild(newTask);

            DOMSource source = new DOMSource(xmlDoc);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(getContext().openFileOutput("saved_tasks.xml", Context.MODE_PRIVATE));
            transformer.transform(source, result);
        }

        catch (TransformerConfigurationException ex) {
            input_text_view.setText(ex.getMessage());
        }

        catch(TransformerException ex) {
            input_text_view.setText(ex.getMessage());
        }

        catch (FileNotFoundException ex) {
            input_text_view.setText(ex.getMessage());
        }

    }

    private Document getDocument() {
        Document document = null;

        try {

            FileInputStream fInput = getContext().openFileInput("saved_tasks.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();

            document = documentBuilder.parse(fInput);
        }


        catch(ParserConfigurationException ex) {
            input_text_view.setText(ex.getMessage());
        }

        catch(SAXException ex) {
            input_text_view.setText(ex.getMessage());
        }

        catch(IOException ex) {
            input_text_view.setText(ex.getMessage());
        }

        return document;

    }

    public void readFromXmlFile() {
        tasks.clear();

        try {
            Document doc = getDocument();

            if(doc == null) {
                input_text_view.setText("Document null");
                return;
            }

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getChildNodes();

            for (int task = 0; task < nList.getLength(); task++) {
                Node node = nList.item(task);
                tasks.add(node.getChildNodes().item(0).getTextContent());
            }

        }

        catch (Exception ex)
        {
            input_text_view.setText(ex.getMessage());
        }


    }
}
