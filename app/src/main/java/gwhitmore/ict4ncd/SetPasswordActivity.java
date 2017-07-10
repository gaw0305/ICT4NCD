package gwhitmore.ict4ncd;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;

import org.w3c.dom.Text;

import java.util.List;

public class SetPasswordActivity extends AppCompatActivity {

    String password;
    PatternLockView patternLockView;
    Button submitButton;
    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        this.setTitle("Draw pattern to unlock app");

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setEnabled(false);
        retryButton = (Button) findViewById(R.id.retryButton);

        patternLockView = (PatternLockView) findViewById(R.id.patternLock);
        patternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.green));
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
                TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
                titleTextView.setText("Release finger when finished.");
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {}

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if (submitButton.getText().equals("Confirm")) {
                    String checkPassword = PatternLockUtils.patternToString(patternLockView, pattern);
                    if (checkPassword.equals(password)) {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        submitButton.setEnabled(true);
                        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
                        titleTextView.setText("Your unlock pattern has been set as:");
                        patternLockView.setEnabled(false);
                    }
                    else {
                        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
                        titleTextView.setText("Try again.");
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                        patternLockView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                patternLockView.clearPattern();
                            }
                        }, 1000);
                    }
                }
                else {
                    password = PatternLockUtils.patternToString(patternLockView, pattern);
                    if (password.length() < 4) {
                        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
                        titleTextView.setText("You need to connect at least 4 dots. Try again.");
                    }
                    else {
                        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
                        titleTextView.setText("Pattern saved.");
                        submitButton.setEnabled(true);
                        patternLockView.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCleared() {}
        });
    }

    public void submitButtonClicked(View view) {
        if (submitButton.getText().equals("Continue")) {
            patternLockView.clearPattern();
            patternLockView.setEnabled(true);
            submitButton.setText("Confirm");
            retryButton.setText("Cancel");
            TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
            titleTextView.setText("Draw the pattern again to confirm it.");
        }
        else if (submitButton.getText().equals("Confirm")) {
            DBHandler myDB = new DBHandler(this);
            myDB.insertLoginData(getIntent().getStringExtra("username"), password, getIntent().getStringExtra("language"));
            Intent intent = new Intent(getApplicationContext(), ModeChooserActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("language", getIntent().getStringExtra("language"));
            intent.putExtra("password", password);
            startActivity(intent);
        }
        submitButton.setEnabled(false);
    }

    public void retryButtonClicked(View view) {
        if (retryButton.getText().equals("Retry")) {
            patternLockView.clearPattern();
        }
        else if (retryButton.getText().equals("Cancel")) {
            patternLockView.clearPattern();
            Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() { }
}
