package com.example.fitnessapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterScreen extends AppCompatActivity {

    SQLiteDatabase database;
    EditText name, email, pw, height, weight, age;

    String nameField, emailField, pwField, heightField, weightField, ageField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.nameField);
        email = findViewById(R.id.emailField);
        pw = findViewById(R.id.pwField);
        weight = findViewById(R.id.weightField);
        age = findViewById(R.id.ageField);
        height = findViewById(R.id.heightField);

        database = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);

    }

    public void registerBtnClick(View view) {
        //get text from fields
        nameField =  name.getText().toString().trim();
        emailField = email.getText().toString().trim();
        pwField = pw.getText().toString().trim();
        heightField = height.getText().toString().trim();
        weightField = weight.getText().toString().trim();
        ageField = age.getText().toString().trim();

        //If all the fields have inputs, then proceed with account creation
        if( !isEmpty(name) & !isEmpty(pw) & !isEmpty(age) & !isEmpty(height) & !isEmpty(weight) & !isEmpty(email) ) {
            database.execSQL("INSERT INTO account (name, password, email, age, height, weight) VALUES ('"+nameField+"','"+pwField+"','"+emailField+"','"+heightField+"','"+weightField+"','"+ageField+"')");
            Log.i("Msg", "Successfuly Created?");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.i("Value", "Fail");
            Toast.makeText(getApplicationContext(), "One or more fields is empty", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isEmpty(EditText text) {
        String checker = text.getText().toString();
        //Preferred to use TextUtils
        return TextUtils.isEmpty(checker);
    }


}
