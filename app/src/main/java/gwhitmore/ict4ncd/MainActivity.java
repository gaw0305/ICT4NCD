package gwhitmore.ict4ncd;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*

 */
public class MainActivity extends AppCompatActivity {

    Button signInButton;
    TextView newUserTextView;
    EditText username;
    EditText password;
    TextView forgotPasswordTextView;
    DBHandler myDB;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = (Button) findViewById(R.id.signInButton);
        newUserTextView = (TextView) findViewById(R.id.newUserTextView);
        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        newUserTextView.setTextColor(Color.parseColor("#0000ff"));
        forgotPasswordTextView.setTextColor(Color.parseColor("#0000ff"));
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        myDB = new DBHandler(this);
    }

    public void newUser(View v) {
        Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
        startActivity(intent);
    }

    public void signIn(View v) {
        username = ((EditText) findViewById(R.id.usernameEditText));
        password = ((EditText) findViewById(R.id.passwordEditText));

        Cursor cursor = myDB.getAllLoginData();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).equals(username.getText().toString()) && cursor.getString(1).equals(password.getText().toString())) {
                    Intent intent = new Intent(getApplicationContext(), Welcome.class);
                    intent.putExtra("language", cursor.getString(2));
                    intent.putExtra("username", username.getText().toString());
                    intent.putExtra("password", password.getText().toString());
                    startActivity(intent);
                    return;
                }
            } while (cursor.moveToNext());
        }

        errorTextView.setText("Username or password is incorrect");
        errorTextView.setTextColor(Color.parseColor("#ff0000"));

        username.setText("");
        password.setText("");
    }

    @Override
    public void onBackPressed() { }
}
