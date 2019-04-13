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
    SharedPreferences sharedpreferences;
    int autoSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
        //database.execSQL("DROP TABLE IF EXISTS account");
        database.execSQL("CREATE TABLE IF NOT EXISTS account (name VARCHAR, password TEXT, email VARCHAR(1024), age VARCHAR, height VARCHAR, weight VARCHAR, id INTEGER PRIMARY KEY)" );

        //Add users for testing
        database.execSQL("INSERT INTO account (name, password, email, age, height, weight) VALUES ('admin','pw','admin@email.com', 1, 1, 1 )");


        sharedpreferences = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        int j = sharedpreferences.getInt("key", 0);

        if(j > 0){
            Intent activity = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(activity);
            finish();
        }


    }

    public boolean checkExists(String username){

        //Check if exists in database
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


        if(checkExists(username)){

            Cursor cursor = database.rawQuery("SELECT password FROM account WHERE email='"+username+"'",null);
            if (cursor.moveToFirst()) {
                do {
                   dbPassword = cursor.getString(cursor.getColumnIndex("password"));
                } while (cursor.moveToNext());
            }


            if(password.equals(dbPassword)) {



                Intent activity = new Intent(getApplicationContext(), HomeActivity.class);
                activity.putExtra("username", name);
                startActivity(activity);

                autoSave = 1;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("key", autoSave);
                editor.apply();
                Log.i("Check:", "PASSED");

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
