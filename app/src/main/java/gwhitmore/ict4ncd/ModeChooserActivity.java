package gwhitmore.ict4ncd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ModeChooserActivity extends AppCompatActivity {

    String password;
    String username;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_chooser);
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");
        language = getIntent().getStringExtra("language");
    }

    public void bloodPressureButtonClicked(View view) {
        startIntent(new Intent(getApplicationContext(), BloodPressureActivity.class));
    }

    public void bloodSugarButtonClicked(View view) {
        startIntent(new Intent(getApplicationContext(), BloodSugarActivity.class));
    }

    public void weightButtonClicked(View view) {
        startIntent(new Intent(getApplicationContext(), WeightActivity.class));
    }

    public void listButtonClicked(View view) {
        startIntent(new Intent(getApplicationContext(), AllDataActivity.class));
    }

    public void graphButtonClicked(View view) {
        startIntent(new Intent(getApplicationContext(), GraphActivity.class));
    }

    public void logoutButtonClicked(View view) {
        startIntent(new Intent(getApplicationContext(), Welcome.class));
    }

    public void startIntent(Intent intent) {
        intent.putExtra("username", username);
        intent.putExtra("language", language);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {}
}