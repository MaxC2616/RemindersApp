package uk.ac.abertay.cmp309.assessmentempty;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CalendarFragment extends Fragment {

    private ListView list;
    CalendarView calendarView;
    private SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
    private DBManager dbManager;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //OnCreating the view for a fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        // Inflate the layout for this fragment

        //New instance of database manager
        dbManager = new DBManager(getContext());

        //Setting up view variables
        list = view.findViewById(R.id.list_reminders_calendar);

        calendarView = view.findViewById(R.id.calendarView);

        String selecteddate = sdf.format(new Date(calendarView.getDate()));

        //Populate the list with any reminders from the current date
        populateList(selecteddate);

        //Whenever the date on calendar is changed
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayofmonth) {
                //Get the new date and then populate the list again

                int actualMonth = month + 1;

                String date = dayofmonth + "/" + (actualMonth) + "/" + year;

                populateList(date);


            }
        });

        //When clicking on an item that is in the list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                TextView idView = view.findViewById(R.id.item_id);  //Get the reminder ID from a hidden text view

                //Retrieve the full record of item clicked from the database
                Cursor cursor = dbManager.retrieveSpecific(Integer.parseInt(idView.getText().toString()));

                //Preparing for opening the record info and starting activity
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

        return view;
    }


    //Helper function to populate the list using arrays and RemindersAdapter class
    private void populateList(String date){
        ArrayList<String[]> reminders = new ArrayList<>();

        //Creating a background thread to iterate through database and post the data to the list
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = dbManager.readAllCalendar(date);

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
}