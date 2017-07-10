package gwhitmore.ict4ncd;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class Welcome extends AppCompatActivity {

    Spinner loginSpinner;
    List<String> spinnerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginSpinner = (Spinner) findViewById(R.id.loginSpinner);

        loginSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem != "Choose Profile") {
                    Intent intent = new Intent(getApplicationContext(), PasswordActivity.class);
                    intent.putExtra("username", selectedItem);
                    startActivity(intent);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        addItemsOnSpinner();
    }

    public void addItemsOnSpinner() {

        spinnerList.add("Choose Profile");

        DBHandler db = new DBHandler(this);
        Cursor cursor = db.getAllLoginData();

        if (cursor.moveToFirst()) {
            do {
                spinnerList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loginSpinner.setAdapter(dataAdapter);
    }

    public void newProfileClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
        startActivity(intent);
    }

//    public void launchGame(View v) {
//        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("vanuasoft.vangov");
//        if (launchIntent != null) {
//            startActivity(launchIntent);//null pointer check in case package name was not found
//        }
//        else {
////            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=vanuasoft.vangov")));
//            } catch (android.content.ActivityNotFoundException anfe) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=vanuasoft.vangov")));
//            }
////            Toast.makeText(getApplicationContext(), "Nope", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void deleteProfileClicked(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(Welcome.this);
        alert.setTitle("Choose Profile to Delete");
        final ArrayAdapter<String> profileNames = new ArrayAdapter<>(Welcome.this, android.R.layout.select_dialog_multichoice);
        for (String name : spinnerList) {
            if (!name.equals("Choose Profile")) profileNames.add(name);
        }
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setAdapter(profileNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = profileNames.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(Welcome.this);
                builderInner.setMessage("Are you sure you want to delete this profile?");
                builderInner.setTitle("");
                builderInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        spinnerList.remove(strName);
                        DBHandler myDB = new DBHandler(Welcome.this);
                        myDB.deleteUserData(strName);
                        dialog.dismiss();
                    }
                });
                builderInner.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        alert.show();
    }
}
