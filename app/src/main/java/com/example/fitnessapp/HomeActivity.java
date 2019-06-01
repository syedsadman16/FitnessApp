package com.example.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences, statSharedPreferences, acctSharedPreferences;
    TextView greetings, distance, calories, estSteps;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Shared preference to save autologin data
        sharedPreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        //Shared preference for daily statistics
        statSharedPreferences = getSharedPreferences("statistics", Context.MODE_PRIVATE);
        //Sharedpreference for details about registered user
        acctSharedPreferences = getSharedPreferences("personDetails", Context.MODE_PRIVATE);

        //Updating the most recent statistics
        String dist = statSharedPreferences.getString("dist", "0");
        distance = findViewById(R.id.mainDistanceDisplay);
        distance.setText(dist);

        String name = acctSharedPreferences.getString("Name", "Unknown");
        greetings = findViewById(R.id.greetingTextView);
        greetings.setText("You are signed in as " + name);

        String calorie = statSharedPreferences.getString("calorie", "0");
        calories = findViewById(R.id.calorieDisplay);
        calories.setText(calorie);

        Integer steps = statSharedPreferences.getInt("steps", 0);
        estSteps = findViewById(R.id.stepsDisplay);
        estSteps.setText(Integer.toString(steps));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add items to actionbar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Sign out options selection
        switch (item.getItemId()) {
            case R.id.signOut:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("key", 0);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:  return super.onOptionsItemSelected(item);
        }

    }

    //When the 'START RUN' button is clicked
    public void startRun(View view) {
        Intent goToCardio = new Intent(getApplicationContext(), CardioActivity.class);
        startActivity(goToCardio);
    }





}
