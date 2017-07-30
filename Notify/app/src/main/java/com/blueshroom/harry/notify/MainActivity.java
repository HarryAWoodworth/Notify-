package com.blueshroom.harry.notify;

/*
 * Made by Harry Woodworth on 7/24/2017
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import static com.blueshroom.harry.notify.R.id.parent;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // The EditTexts
    private EditText mTitleEditText;
    private EditText mTextEditText;
    private EditText mMinutes;
    private EditText mSeconds;
    private EditText mProgressMessage;

    // Boolean for if the notification has a progress bar
    boolean hasProgressBar;

    // The LinearLayout for global use in the switch listener
    private LinearLayout linearLayout;

    // Color ID of the Notification
    public int colorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // OnCreate stuff that sets mainActivity as the view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize this app to use Ads using the ID
        MobileAds.initialize(this, getString(R.string.appId));

        // Set the UI
        setUI();
    }

    // Set the UI
    public void setUI() {
        // The EditTexts concerning the Title and Text of the Notification,
        // and the Minutes and Seconds until being notified. And the ProgressBar
        // Message displayed after loading
        mTitleEditText = (EditText)findViewById(R.id.title_edit_text);
        mTextEditText = (EditText)findViewById(R.id.text_edit_text);
        mMinutes = (EditText)findViewById(R.id.minutes_edit_text);
        mProgressMessage = (EditText)findViewById(R.id.progress_message_edit_text);
        mMinutes.setText("0");
        mSeconds = (EditText)findViewById(R.id.seconds_edit_text);
        mSeconds.setText("0");

        // Color Spinner
        Spinner spinner = (Spinner)findViewById(R.id.color_spinner);
        // Connect a listener
        spinner.setOnItemSelectedListener(this);
        // Color Selections, defaults to Color.Black
        List<String> colors = new ArrayList<>();
        colors.add("Black");colors.add("Light Gray");colors.add("Blue");colors.add("Cyan");colors.add("Dark Gray");colors.add("Gray");
        colors.add("Green");colors.add("Yellow");colors.add("Magenta"); colors.add("Red");colors.add("White");
        // Create an Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        // Dropdown layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the adapter to the spinner
        spinner.setAdapter(adapter);

        // AdView Banner Advertisement from AdMob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // The Switch and Switch Listener for if the notification has a Progress Bar
        hasProgressBar = false;
        Switch mProgressSwitch = (Switch) findViewById(R.id.progressBarSwitch);
        linearLayout = (LinearLayout)findViewById(R.id.layout_progress_extra);
        mProgressSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // isChecked will be true if the switch is in the On position
                // If True: Make the linearLayout containing the editText visible and set the boolean
                // If False: Make the layout invisible and set the boolean, as well as clear the editText
                if(isChecked) {
                    hasProgressBar = true;
                    linearLayout.setVisibility(View.VISIBLE);
                    mProgressMessage.setClickable(true);
                } else {
                    hasProgressBar = false;
                    linearLayout.setVisibility(View.INVISIBLE);
                    mProgressMessage.setClickable(false);
                    mProgressMessage.setText("");
                }
            }
        });
    }

    // Create the Notification from the user's input and display a toast when it was completed
    public void createNotification(View view) {
        // Create an intent to the NotificationBroadcaster class
        Intent notificationIntent = new Intent(this, NotificationBroadcaster.class);

        // Add the extra information being passed over (Title text, small text, Color)
        notificationIntent.putExtra("mTitleEditText",mTitleEditText.getText().toString());
        notificationIntent.putExtra("mTextEditText",mTextEditText.getText().toString());
        notificationIntent.putExtra("color",colorID);

        // Send the boolean for progressBar, if it does have one also send the end message
        notificationIntent.putExtra(NotificationBroadcaster.PROGRESS, hasProgressBar);
        if(hasProgressBar) {
            // Get the end Message
            String progressEndText = mProgressMessage.getText().toString();
            notificationIntent.putExtra(NotificationBroadcaster.PROGRESS_END, progressEndText);
        }

        // Create the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate the time in milliseconds until the notification
        int minutes = Integer.parseInt(mMinutes.getText().toString());
        int seconds = Integer.parseInt(mSeconds.getText().toString());
        int time_till_notify = (minutes*60000)+(seconds*1000);

        // Start an alarmManager with the pendingIntent
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time_till_notify, pendingIntent);

        // Make Dialog Box to notify the user
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your notification will appear in " + minutes + " minutes and " + seconds + " seconds!")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do Nothing
                    }
                });
        // Create the AlertDialog object and return it
        alertDialog = builder.create();
        alertDialog.show();
    }

    // Spinner selection method, default to Light Gray
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String colorStr = parent.getItemAtPosition(position).toString();

        // Set the colorID based on the String from the Spinner, default to Color.BLACK
        switch(colorStr){
            case "Black": colorID = Color.BLACK; break;
            case "Blue": colorID = Color.BLUE; break;
            case "Cyan": colorID = Color.CYAN; break;
            case "Dark Gray": colorID = Color.DKGRAY; break;
            case "Gray": colorID = Color.GRAY; break;
            case "Green": colorID = Color.GREEN; break;
            case "Yellow": colorID = Color.YELLOW; break;
            case "Light Gray": colorID = Color.LTGRAY; break;
            case "Magenta": colorID = Color.MAGENTA; break;
            case "Red": colorID = Color.RED; break;
            case "White": colorID = Color.WHITE; break;
            default: colorID = Color.BLACK; break;
        }
    }

    // Default the spinner color selection to Black if nothing else was selected
    public void onNothingSelected(AdapterView<?> arg0) {
        colorID = Color.BLACK;
    }
}