package uk.ac.abertay.cmp309.assessmentempty;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DeleteRecord extends AppCompatActivity implements View.OnClickListener {

    TextView infotv, datetv, timetv, locationtv, typetv;
    String date;
    String time;
    String sid;
    int id;
    int type;
    int notification;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_record);

        //Populating the view with the selected records details (or from the notification)
        sid = getIntent().getExtras().getString("id");
        id = Integer.parseInt(sid);

        String stype = getIntent().getExtras().getString("type");
        type = Integer.parseInt(stype);
        String info = getIntent().getExtras().getString("info");
        date = getIntent().getExtras().getString("date");
        time = getIntent().getExtras().getString("time");
        String location = getIntent().getExtras().getString("location");
        notification = getIntent().getExtras().getInt("notification");

        infotv = findViewById(R.id.textViewInfoDelete);
        datetv= findViewById(R.id.textViewDateDelete);
        timetv = findViewById(R.id.textViewTimeDelete);
        locationtv = findViewById(R.id.textViewLocationDelete);
        typetv = findViewById(R.id.textViewTypeDelete);

        Switch notificationBox = findViewById(R.id.switchNotification);

        notificationBox.setChecked(notification == 1);

        //Listener on notification switch
        notificationBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //If the notification switch is checked, set notif to 1 and update the database with this change
                    notification = 1;
                    update();

                    //Then set up a new alarm with the data from this record
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(getApplicationContext(), TimeManager.class);

                    intent.putExtra("id", sid);
                    intent.putExtra("type", stype);
                    intent.putExtra("info", info);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("location", locationtv.getText().toString());
                    intent.putExtra("notification", notification);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    String dateandtime = date + " " + time + ":00";
                    DateFormat formatter = new SimpleDateFormat("d/M/yyyy HH:mm:ss", Locale.ROOT);

                    try {
                        Date currentdate = Calendar.getInstance().getTime();
                        Date date1 = formatter.parse(dateandtime);

                        //If the date/time has already passed, don't set up a new alarm
                        if (date1.getTime() < currentdate.getTime() && type == 1)
                        {
                            Toast.makeText(getApplicationContext(), "You cannot reactivate alarm for this notification, the time has already passed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (type == 1) {
                                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
                            } else if (type == 2) {
                                am.setRepeating(AlarmManager.RTC_WAKEUP, date1.getTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                //am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

                                /*for (int i = 0; i < 365; i++) {
                                    String[] sdate = date.split("/");
                                    int day = Integer.parseInt(sdate[0]);
                                    day = day + 1;

                                    date = day + "/" + sdate[1] + "/" + sdate[2];

                                    dateandtime = date + "" + time;

                                    try {
                                        date1 = formatter.parse(dateandtime);

                                        //Creating a Pending Intent
                                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

                                        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }*/

                            }
                            else{
                                am.setRepeating(AlarmManager.RTC_WAKEUP, date1.getTime(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                                //am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

                                /*for (int i = 0; i < 52; i++) {
                                    String[] sdate = date.split("/");
                                    int week = Integer.parseInt(sdate[0]);
                                    week = week + 7;

                                    date = week + "/" + sdate[1] + "/" + sdate[2];

                                    dateandtime = date + "" + time;

                                    try {
                                        date1 = formatter.parse(dateandtime);

                                        //Creating a Pending Intent
                                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

                                        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }*/

                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    //If notification switch was switched off, set notif to 0 and update database accordingly
                    notification = 0;
                    update();

                    //Set up alarm manager instance to delete the alarm that was already set
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    Intent intent = new Intent(getApplicationContext(), TimeManager.class);
                    time = time + ":00";

                    intent.putExtra("id", sid);
                    intent.putExtra("type", stype);
                    intent.putExtra("info", info);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("location", locationtv.getText().toString());
                    intent.putExtra("notification", notification);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    //Cancel the alarm
                    am.cancel(pendingIntent);
                }
            }
        });

        //Fill in text views with record info
        infotv.setText(info);
        datetv.setText(date);
        timetv.setText(time);
        locationtv.setText(location);

        if (type == 1)
        {
            typetv.setText("One Time");
        }
        else if (type == 2)
        {
            typetv.setText("Daily");
        }
        else{
            typetv.setText("Weekly");
        }



        //Setting up button listeners
        Button btn = findViewById(R.id.buttonDeleteRecord);
        btn.setOnClickListener(this);

        Button btnBack = findViewById(R.id.buttonGoBack);
        btnBack.setOnClickListener(this);
    }

    //Delete the record
    public void deleteReminder() {
        dbManager = new DBManager(this);
        dbManager.delete(id);
    }

    public void update() {
        dbManager = new DBManager(this);
        dbManager.updateNotifications(id, notification);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()) {
            //Delete record or go back
            case R.id.buttonDeleteRecord:
                deleteReminder();
                startActivity(new Intent(DeleteRecord.this, MainActivity.class));
                break;
            case R.id.buttonGoBack:
                startActivity(new Intent(DeleteRecord.this, MainActivity.class));
                break;
        }
    }
}