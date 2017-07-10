package gwhitmore.ict4ncd;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class NewUserActivity extends AppCompatActivity {

    Button submitButton;
    EditText username;
    EditText password;
    EditText password2;
    DBHandler myDB;
    TextView errorTextView;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        submitButton = (Button) findViewById(R.id.submitButton);
        spinner = (Spinner) findViewById(R.id.spinner);
        myDB = new DBHandler(this);
    }

    public void submit(View v) {
        username = (EditText) findViewById(R.id.usernameText);

        Boolean profileExists = false;

        Cursor cursor = myDB.getAllLoginData();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(username.getText().toString())) {
                    profileExists = true;
                }
            } while (cursor.moveToNext());
        }

        if (TextUtils.isEmpty(username.getText().toString())) {
            username.setError("This field is required");
        }
        else if (profileExists) {
            username.setError("Username already exists");
        }
        else if (spinner.getSelectedItem().equals("Choose a Language")) {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
//            errorText.setText("Choose a Language");
        }
        else {
            Intent intent = new Intent(getApplicationContext(), SetPasswordActivity.class);
            intent.putExtra("username", username.getText().toString());
            intent.putExtra("language", spinner.getSelectedItem().toString());
            startActivity(intent);
        }
//        username = (EditText) findViewById(R.id.usernameText);
//        password = (EditText) findViewById(R.id.passwordText);
//        password2 = (EditText) findViewById(R.id.password2Text);
//
//        String passwordText = password.getText().toString();
//        String password2Text = password.getText().toString();
//
//        Cursor cursor = myDB.getAllLoginData();
//
//        if (cursor.getCount() != 0) {
//            cursor.moveToFirst();
//            while (cursor.moveToNext()) {
//                if (cursor.getString(0).equals(username.getText().toString())) {
//                    errorTextView.setText("Username already exists.\nPlease choose a different username.");
//                    username.setText("");
//                    password.setText("");
//                    password2.setText("");
//                    return;
//                }
//            }
//        }
//
//        if (username.getText().toString() == "" || password.getText().toString() == "" || password2.getText().toString() == "") {
//            errorTextView.setText("Please fill in all fields before clicking sumbit");
//            username.setText("");
//            password.setText("");
//            password2.setText("");
//        }
//        else if (passwordText.length() < 8) {
//            errorTextView.setText("Password is too short. Please enter another password.");
//            password.setText("");
//            password2.setText("");
//        }
//        else if (!passwordText.equals(password2Text)) {
//            errorTextView.setText("Passwords do not match. Please reenter your password.");
//            password.setText("");
//            password2.setText("");
//        }
//        else {
//            myDB.insertLoginData(username.getText().toString(), password.getText().toString(), spinner.getSelectedItem().toString());
//            Intent intent = new Intent(getApplicationContext(), Welcome.class);
//            intent.putExtra("username", username.getText().toString());
//            intent.putExtra("language", spinner.getSelectedItem().toString());
//            intent.putExtra("password", passwordText);
//            startActivity(intent);
//        }
    }
}
