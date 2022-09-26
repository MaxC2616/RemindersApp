package uk.ac.abertay.cmp309.assessmentempty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ChooseReminderTypeActivity extends AppCompatActivity implements View.OnClickListener {

    RadioGroup radioGroup;
    int selectedRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_reminder_type);

        //Matching variables with UI elements and assigning listeners to all
        Button btncont = findViewById(R.id.buttonContinue);
        Button btncancel = findViewById(R.id.buttonCancelRadio);
        RadioButton onetime = findViewById(R.id.radioOneTime);
        RadioButton daily = findViewById(R.id.radioDaily);
        RadioButton weekly = findViewById(R.id.radioWeekly);
        radioGroup = findViewById(R.id.radioGroup);
        btncont.setOnClickListener(this);
        btncancel.setOnClickListener(this);
        onetime.setOnClickListener(this);
        daily.setOnClickListener(this);
        weekly.setOnClickListener(this);

        selectedRadio = 0;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.buttonCancelRadio:  //Go back to home
                Intent intent = new Intent(ChooseReminderTypeActivity.this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.buttonContinue:  //Continue to add reminder. Check that radio button is selected. Pass through extra "type"
                if (selectedRadio == 0) {
                    Toast.makeText(getApplicationContext(), "Please select a reminder type!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent reminder = new Intent(ChooseReminderTypeActivity.this, AddReminder.class);
                    reminder.putExtra("type", selectedRadio);
                    startActivity(reminder);
                }
                break;

            //Setting value when radio buttons are clicked
            case R.id.radioOneTime:
                selectedRadio = 1;
                break;

            case R.id.radioDaily:
                selectedRadio = 2;
                break;

            case R.id.radioWeekly:
                selectedRadio = 3;
                break;
        }
    }
}