package de.technopaki.aleks.raveri;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by aleks on 11.07.17.
 */

public class QuestActivity extends android.support.v4.app.Fragment {

    ListView task_list;
    ProgressBar levelProgress;
    ArrayAdapter<String> tasks;
    TextView level;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quests_layout, container, false);
        task_list = (ListView) view.findViewById(R.id.task_list);
        level = (TextView) view.findViewById(R.id.level);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        tasks = new ArrayAdapter<String>(view.getContext(), R.layout.task_item);
        tasks.notifyDataSetChanged();
        task_list.setAdapter(tasks);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                readFromXmlFile();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        return view;
    }

    public void readFromXmlFile() {
        tasks.clear();

        try {
            Document doc = getDocument();

            if(doc == null) {
                level.setText("Document null");
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
            level.setText(ex.getMessage());
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
            level.setText(ex.getMessage());
        }

        catch(SAXException ex) {
            level.setText(ex.getMessage());
        }

        catch(IOException ex) {
            level.setText(ex.getMessage());
        }

        return document;

    }
}
