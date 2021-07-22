package com.example.todonote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class Add_Note extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Add_Note";
    TextInputLayout e_Title, e_Date, e_Time;
    TextInputEditText text_date, text_time, text_title;
    Button save_button;

    //For Notification Channel Creation
    private String CHANNEL_NAME = "todo_note";
    private String CHANNEL_ID = "com.example.notification";
    private String description = "Channel for Todo App";
    private NotificationManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__note);

        //to match the status bar color with layout color
        Window window = Add_Note.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(Add_Note.this, R.color.purple_500));

        e_Title = findViewById(R.id.textInputLayout_title);
        e_Date = findViewById(R.id.textInputLayout_date);
        e_Time = findViewById(R.id.textInputLayout_time);

        text_title = findViewById(R.id.title);
        text_date = findViewById(R.id.date);
        text_time = findViewById(R.id.time);
        text_date.setOnClickListener(this);
        text_time.setOnClickListener(this);
        save_button = findViewById(R.id.add_btn);

        createNotificationChannel();

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeData();
            }
        });
    }

    //Date and Time Pickers
    @Override
    public void onClick(View v) {

        if (v == text_date) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            text_date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == text_time) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            text_time.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    private boolean validateTitle() {
        String title = e_Title.getEditText().getText().toString().trim();
        if (title.isEmpty()) {
            e_Title.setError("Please Enter Title..!");
            return false;
        } else {
            e_Title.setError(null);
            return true;
        }
    }

    private boolean validateDate() {
        String date = e_Date.getEditText().getText().toString().trim();
        if (date.isEmpty()) {
            e_Date.setError("Please Select Date..!");
            return false;
        } else {
            e_Date.setError(null);
            return true;
        }
    }

    private boolean validateTime() {
        String time = e_Time.getEditText().getText().toString().trim();
        if (time.isEmpty()) {
            e_Time.setError("Please Select Time..!");
            return false;
        } else {
            e_Time.setError(null);
            return true;
        }
    }

    //Store data in to fireStore
    private void storeData() {

        String title = e_Title.getEditText().getText().toString().trim();
        String date = e_Date.getEditText().getText().toString().trim();
        String time = e_Time.getEditText().getText().toString().trim();
        Log.d(TAG,"correct date:"+date);
        String[] dateParts = date.split("-");
        String month = dateParts[1];
        //check field validations
        if (!validateTitle() | !validateDate() | !validateTime()) {
           return;
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Note note = new Note(title, date, time, new Timestamp(new Date()), userId,month);
        FirebaseFirestore.getInstance()
                .collection("TodoList")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "successfully Add note");
                        finish();
                        //Toast.makeText(Add_Note.this, "Notes Added..!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Toast.makeText(Add_Note.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: "+ e );
                    }
                });
        mergeDT(date, time);//to pass the date and time in the method.
    }

    //Merging Date and Time Ofter to use alarm manager.
    private void mergeDT(String myDate, String myTime) {
        String toParse = myDate + " " + myTime;
        //Toast.makeText(this, "Date And Time:" + toParse, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Date And Time:" + toParse);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            Date date = dateFormat.parse(toParse);
            Log.d(TAG, "Date Alarm : " + date);
            long millis;
            millis = date.getTime();
            Log.d(TAG, "Millisecond Alarm Time : " + millis);
            setAlarm(millis);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    private void setAlarm(long dt) {
        long milliseconds = new GregorianCalendar().getTimeInMillis();
        Log.d(TAG, "Milliseconds : " + milliseconds);
        long interval = dt - milliseconds;
        Log.d(TAG, "Interval : " + interval);
        long time = new GregorianCalendar().getTimeInMillis() + interval;
        Log.d(TAG, "Alarm time final : " + time);
        //get title to set notification body.
        String title = text_title.getText().toString().trim();
        Intent intent = new Intent(this, NotificationHelper.class);
        intent.putExtra("message", title);
        //startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Add_Note.this, 100, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        // Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            mManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mManager.createNotificationChannel(channel);
        }
    }
}