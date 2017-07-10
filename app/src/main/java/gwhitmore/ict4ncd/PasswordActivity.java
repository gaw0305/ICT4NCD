package gwhitmore.ict4ncd;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;

import java.util.List;

public class PasswordActivity extends AppCompatActivity {

    PatternLockView patternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        patternLockView = (PatternLockView) findViewById(R.id.patternLock);
        patternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.green));
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {}

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {}

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String password = PatternLockUtils.patternToString(patternLockView, pattern);
                DBHandler myDB = new DBHandler(getApplicationContext());
                Cursor cursor = myDB.getAllLoginData();
                Boolean correctPassword = false;
                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getString(0).equals(getIntent().getStringExtra("username")) && cursor.getString(1).equals(password)) {
                            Intent intent = new Intent(getApplicationContext(), ModeChooserActivity.class);
                            intent.putExtra("language", cursor.getString(2));
                            intent.putExtra("username", getIntent().getStringExtra("username"));
                            intent.putExtra("password", password);
                            correctPassword = true;
                            // patternLockView.setC
                            patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                            startActivity(intent);
                        }
                    } while (cursor.moveToNext());
                }
                if (!correctPassword) {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
                    titleTextView.setText("Incorrect Password. Please try again.");
                }
            }

            @Override
            public void onCleared() {}
        });
    }
}
