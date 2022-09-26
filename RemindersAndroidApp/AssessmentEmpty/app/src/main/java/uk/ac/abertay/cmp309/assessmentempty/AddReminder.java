package uk.ac.abertay.cmp309.assessmentempty;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddReminder extends AppCompatActivity implements View.OnClickListener {

    private TextView dateView, timeView, locationView, typeView;
    private int year, month, day;
    private int hour, minute;
    private int type;
    private EditText info;
    private CheckBox notificationBox;
    private int notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        //STORAGE PERMISSION CHECKS
        int check1 = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int check2 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean needToAsk = !((check1 == PackageManager.PERMISSION_GRANTED) & (check2 == PackageManager.PERMISSION_GRANTED));

        if (needToAsk) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 0);
        }


        //SETTING VARIABLES
        typeView = findViewById(R.id.textViewType);
        locationView = findViewById(R.id.textViewLocation);
        dateView = findViewById(R.id.textViewDate);
        timeView = findViewById(R.id.textViewTime);

        notificationBox = findViewById(R.id.notificationBox);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        //GETTING EXTRAS - IF THEY EXIST. This is mainly for when the user returns from choosing a location, so that information is saved

        Bundle extras = getIntent().getExtras();
        type = extras.getInt("type");
        if (type == 1)
        {
            typeView.setText("The selected reminder type is - One Time");
        }
        else if (type == 2)
        {
            typeView.setText("The selected reminder type is - Daily");
        }
        else if (type == 3)
        {
            typeView.setText("The selected reminder type is - Weekly");
        }
        else if (type == 4)
        {
            typeView.setText("The selected reminder type is - Monthly");
        }
        else if (type == 5)
        {
            typeView.setText("The selected reminder type is - Yearly");
        }

        //Fill info box with extra
        info = (EditText) findViewById(R.id.editTextInfo);
        info.setText(extras.getString("info"));

        //If there is an extra for date, set it to that. Otherwise, set it to current date
        if (extras.getString("date") == null) {
            showDate(year, month+1, day);
        }
        else {
            dateView.setText(extras.getString("date"));
        }

        //If there is an extra for time, set it to that. Otherwise set it to current time
        if (extras.getString("time") == null) {
            showTime(hour, minute);
        } else {
            timeView.setText(extras.getString("time"));
        }

        //If there is an extra for location, set it to that. Otherwise, set it to no location currently selected
        if (extras.getString("location") == null) {
            locationView.setText("No location is selected");
        } else
        {
            locationView.setText(extras.getString("location"));
        }

        //Setting listeners on our buttons
        Button btnDate = findViewById(R.id.buttonchooseDate);
        btnDate.setOnClickListener(this);

        Button btnTime = findViewById(R.id.buttonsetTime);
        btnTime.setOnClickListener(this);

        Button btnLocation = findViewById(R.id.buttonChooseLocation);
        btnLocation.setOnClickListener(this);

        Button btnCancel = findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(this);

        Button btnConfirm = findViewById(R.id.buttonConfirmReminder);
        btnConfirm.setOnClickListener(this);
    }

    //Reference
    //TutorialsPoint (no date) Android Date Picker. Available at: https://www.tutorialspoint.com/android/android_datepicker_control.htm


    //Used for Date Dialog
    public void setDate(View view)
    {
        showDialog(999);
    }

    //Creating the date dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999)
        {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    //Listener for when the date is changed
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            //Update date in text box to show the new chosen date
            showDate(year, month+1, day);
        }
    };

    //Updating the date into the text view
    private void showDate(int year, int month, int day)
    {
        dateView.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
    }

    //onClick Listener actions
    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.buttonchooseDate:
                //Open date dialog
                setDate(view);
                break;
            case R.id.buttonsetTime:
                //Open time dialog
                showTimePicker();
                break;
            case R.id.buttonChooseLocation:
                //Open MarkMapActivity and pass through the current entered info so we can return it upon returning to AddReminderActivity
                Intent location = new Intent(AddReminder.this, MarkMapActivity.class);
                location.putExtra("type", type);
                location.putExtra("info", info.getText().toString());
                location.putExtra("date", dateView.getText().toString());
                location.putExtra("time", timeView.getText().toString());
                startActivity(location);
                break;
            case R.id.buttonCancel:
                //Go back to choosing reminder type activity
                Intent goback = new Intent(AddReminder.this, ChooseReminderTypeActivity.class);
                startActivity(goback);
                break;

            case R.id.buttonConfirmReminder:

                //Check if the user wants notifications or not and set variable accordingly
                if (notificationBox.isChecked())
                {
                    notification = 1;
                }
                else
                {
                    notification = 0;
                }

                if (!info.getText().toString().equals("")) {

                    //Save the data to the SQLite database
                    saveToDB(type, info.getText().toString(), dateView.getText().toString(), timeView.getText().toString(), locationView.getText().toString(), notification);

                    if (notification == 1)   //If the user does want notifications, call the function to set this up
                    {
                        //If user wants notifications, set up an alarm
                        setAlarm(info.getText().toString(), dateView.getText().toString(), timeView.getText().toString());
                    }

                    //Return to Home
                    Intent complete = new Intent(AddReminder.this, MainActivity.class);
                    startActivity(complete);
                    break;
                }
                else {
                    Toast.makeText(this, "You cannot have an empty info box", Toast.LENGTH_SHORT).show();
                }

        }
    }

    //Reference
    // TutorialsPoint (no date) Android - Time Picker. Available at: https://www.tutorialspoint.com/android/android_timepicker_control.htm

    //Show the time picker dialog
    public void showTimePicker() {
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int i, int i1) {
                if (view.isShown()) {
                    //Call function to set text view to selected time
                    showTime(i, i1);
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose time:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    //Showing the time in text view
    public void showTime(int hour, int minute) {
        String zero;
        //If the minutes don't have double digits, add one in to suit readability/formatting
        if (minute < 10)
        {
            zero = "0";
        }
        else
        {
            zero = "";
        }
        timeView.setText(new StringBuilder().append(hour).append(":").append(zero).append(minute).append(":00"));
    }

    //Save the data to the database
    public void saveToDB(int type, String info, String date, String time, String location, int notification){
        DBManager dbManager = new DBManager(this);
        String result = dbManager.insert(type, info, date, time, location, notification);
    }

    //Setting up the notifications
    private void setAlarm(String info, String date, String time){
        //Reference:
        //Stack Overflow (2010) Alarm Manager example. Available at: https://stackoverflow.com/questions/4459058/alarm-manager-example
        //also
        //Stack Overflow (2014) Get all PendingIntents set with AlarmManager. Available at: https://stackoverflow.com/questions/4315611/android-get-all-pendingintents-set-with-alarmmanager

        //Retrieving the ID of the record we've just added to the database
        Cursor cursor = new DBManager(getApplicationContext()).retrieveLatest();

        String id = cursor.getString(0);

        //Setting up instance of alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Creating intent to go to our TimeManager class. This will set up the notification for once the alarm is triggered
        Intent intent = new Intent(getApplicationContext(), TimeManager.class);

        String stype = String.valueOf(type);

        //Inserting extras to intent
        intent.putExtra("id", id);
        intent.putExtra("type", stype);
        intent.putExtra("info", info);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("location", locationView.getText().toString());
        intent.putExtra("notification", notification);

        //Creating a Pending Intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Making sure our date and time go to the right format for setting an alarm
        String dateandtime = date + " " + time;
        DateFormat formatter = new SimpleDateFormat("d/M/yyyy HH:mm:ss", Locale.ROOT);

        try {
            Date date1 = formatter.parse(dateandtime);

            //Depending on the type of reminder chosen, setting an alarm
            if (type == 1) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
            }
            else if (type == 2)
            {
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, date1.getTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            else
            {
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, date1.getTime(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}