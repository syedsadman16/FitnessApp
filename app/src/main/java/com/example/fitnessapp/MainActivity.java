package com.example.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText unField;
    EditText pwField;
    SQLiteDatabase database;
    SharedPreferences sharedpreferences, acctSharedPreferences;
    int autoSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
        //Clean data
        //database.execSQL("DROP TABLE IF EXISTS account");
        database.execSQL("CREATE TABLE IF NOT EXISTS account (name VARCHAR, password TEXT, email VARCHAR(1024), age VARCHAR, height VARCHAR, weight VARCHAR, id INTEGER PRIMARY KEY)" );

        //Add users for testing
        database.execSQL("INSERT INTO account (name, password, email, age, height, weight) VALUES ('admin','pw','admin@email.com', 1, 1, 1 )");

        //Sharedpreference for user registered details
        acctSharedPreferences = getSharedPreferences("personDetails", Context.MODE_PRIVATE);
        //Sharedpreferences for autologin details
        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

       /*
       //Autologin
       // Default is 0 which will not autologin
       // Once user logs in once, value of j changes to allow login
       // There is sign out option in HomeActivity that resets this to default
       // SEE LINE 124
        */
        int j = sharedpreferences.getInt("key", 0);
        if(j > 0){
            Intent activity = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(activity);
            finish();
        }


    }

    //Check if soemthign exists in the database
    public boolean checkExists(String username){
        Cursor c = database.rawQuery("SELECT * FROM account WHERE email= '"+username+"'",null);
        if(c.getCount() <= 0) {
            c.close();
            Log.i("Return:", "False");
            Toast.makeText(getApplicationContext(), "Invalid Username/Password", Toast.LENGTH_LONG).show();
            return false;
        }
        c.close();
        return true;
    }

   //action to go to Home Screen
    public void loginBtn(View view){

        unField = findViewById(R.id.unField);
        pwField = findViewById(R.id.pwField);
        String username = unField.getText().toString();
        String password = pwField.getText().toString();
        String dbPassword = null;
        String name = null;
        String weight = null;

        //If username(registered email) entered exists in database...
        if(checkExists(username)){

            //Retrieve the password associated with the email
            Cursor cursor = database.rawQuery("SELECT password FROM account WHERE email='"+username+"'",null);
            if (cursor.moveToFirst()) {
                do {
                   dbPassword = cursor.getString(cursor.getColumnIndex("password"));
                } while (cursor.moveToNext());
            }

            //If retrieved password matches email, login was successful
            if(password.equals(dbPassword)) {

                //Retrieve users name
                cursor = database.rawQuery("SELECT name FROM account WHERE email='"+username+"'",null);
                if (cursor.moveToFirst()) {
                    do {
                        name = cursor.getString(cursor.getColumnIndex("name"));
                    } while (cursor.moveToNext());
                }

                //Retrieve users weight to calculate BMI and Calories
                cursor = database.rawQuery("SELECT weight FROM account WHERE email='"+username+"'",null);
                if (cursor.moveToFirst()) {
                    do {
                        weight = cursor.getString(cursor.getColumnIndex("weight"));
                    } while (cursor.moveToNext());
                }


                Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeActivity);

                //Save user data to be used in calculations
                SharedPreferences.Editor acctEditor = acctSharedPreferences.edit();
                acctEditor.putString("weight", weight);
                acctEditor.apply();
                acctEditor.putString("Name", name);
                acctEditor.apply();

                //This is what enables autosave
                autoSave = 1;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("key", autoSave);
                editor.apply();
                Log.i("Check:", "PASSED");

                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Invalid Username/Password", Toast.LENGTH_LONG).show();
            }

        }

    }

    public void goToRegistration(View view) {

        Intent i = new Intent(getApplicationContext(), RegisterScreen.class);
        startActivity(i);

    }


}
