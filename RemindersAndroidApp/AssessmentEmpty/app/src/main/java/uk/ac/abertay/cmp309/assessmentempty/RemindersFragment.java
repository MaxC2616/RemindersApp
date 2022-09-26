package uk.ac.abertay.cmp309.assessmentempty;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class RemindersFragment extends Fragment implements View.OnClickListener {

    private ListView list;
    private DBManager dbManager;

    public RemindersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders_list, container, false);

        dbManager = new DBManager(getContext());
        list = view.findViewById(R.id.listReminders);

        //Populate the list
        populateList();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //When an item in the list is clicked, open DeleteRecords activity with that records info

                TextView idView = view.findViewById(R.id.item_id);

                Cursor cursor = dbManager.retrieveSpecific(Integer.parseInt(idView.getText().toString()));

                String idf = cursor.getString(0);
                String type = cursor.getString(1);
                String info = cursor.getString(2);
                String date = cursor.getString(3);
                String time = cursor.getString(4);
                String location = cursor.getString(5);
                int notification = cursor.getInt(6);


                Intent intent = new Intent(getActivity(), DeleteRecord.class);
                intent.putExtra("id", idf);
                intent.putExtra("type", type);
                intent.putExtra("info", info);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("location", location);
                intent.putExtra("notification", notification);
                startActivity(intent);
            }
        });

        //Setting up floating button and listener
        FloatingActionButton btn = view.findViewById(R.id.button_addreminder);
        btn.setOnClickListener(this);
        return view;
    }

    //Populate our list. Makes use of RemindersAdapter
    private void populateList(){

        ArrayList<String[]> reminders = new ArrayList<>();

        //Creating a background thread to iterate through database and post the data to the list
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = dbManager.readAll();

                while (cursor.moveToNext()) {
                    reminders.add(new String[]{cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)});
                }

                list.post(new Runnable() {
                    @Override
                    public void run() {
                        RemindersAdapter adapter = new RemindersAdapter(getContext(), reminders);

                        list.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.button_addreminder:

                //Go to AddReminders activity

                Intent intent = new Intent(getActivity(), ChooseReminderTypeActivity.class);
                startActivity(intent);
                break;
        }
    }

}