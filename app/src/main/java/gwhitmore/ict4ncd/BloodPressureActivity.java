package gwhitmore.ict4ncd;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class BloodPressureActivity extends AppCompatActivity {

    Button addButton;
    EditText heartRateText;
    EditText systolicText;
    EditText diastolicText;
    TextView dateText;
    TextView timeText;
    ListView heartRateListView;
    ArrayList<Spanned> heartRateList;
    ArrayAdapter<Spanned> adapter;
    ImageView heartRateInfo;
    ImageView diastolicInfo;
    ImageView systolicInfo;
    String heartRateInput;
    String systolicInput;
    String diastolicInput;
    String dateInput;
    MediaPlayer audio;
    static final int DATE_DIALOG = 0;
    static final int EDIT_DATE_DIALOG = 1;
    int year;
    int month;
    int day;
    ArrayList<String> months;
    DBHandler myDB;
    String username;
    String language;
    Menu menu;
    MenuItem languageItem;
    MenuItem export;
    MenuItem graphs;
    MenuItem logout;
    EditText editHeartRateText;
    EditText editSystolicText;
    EditText editDiastolicText;
    TextView editDateText;
    TextView editTimeText;
    AlertDialog alertDialog;
    int curPosition;
    String item1;
    String item2;
    String item3;
    String item4;
    Integer dbID;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addButton = (Button) findViewById(R.id.addButton);
        heartRateText = (EditText) findViewById(R.id.heartRateText);
        systolicText = (EditText) findViewById(R.id.systolicText);
        diastolicText = (EditText) findViewById(R.id.diastolicText);
        dateText = (TextView) findViewById(R.id.dateText);
//        timeText = (TextView) findViewById(R.id.timeText);
        heartRateListView = (ListView) findViewById(R.id.heartRateListView);
        heartRateInfo = (ImageView) findViewById(R.id.heartRateInfo);
        diastolicInfo = (ImageView) findViewById(R.id.diastolicInfo);
        systolicInfo = (ImageView) findViewById(R.id.systolicInfo);
        months = new ArrayList<>(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
        myDB = new DBHandler(this);

        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");
//        language = getIntent().getStringExtra("language");
        language = myDB.getLanguage(username);

        this.setTitle("Blood Pressure");

        translatePage(true);

        heartRateList = getInfoFromDB();

        adapter = new ArrayAdapter<>(BloodPressureActivity.this, android.R.layout.simple_list_item_1, heartRateList);

        heartRateListView.setAdapter(adapter);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setCurrentDate();

        heartRateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                String[] items = item.split("\n");
                curPosition = position;

                item1 = items[0];
                item2 = items[1].split(": ")[1].split(" ")[0];
                item3 = items[2].split(": ")[1].split(" ")[0];
                item4 = items[3].split(": ")[1].split(" ")[0];

                AlertDialog.Builder builder = new AlertDialog.Builder(BloodPressureActivity.this);
                if (language.equals("Bislama")) builder.setTitle("Changem Data");
                else if (language.equals("English")) builder.setTitle("Edit Data");
                else if (language.equals("French")) builder.setTitle("Changer la donnée");
                LayoutInflater inflater = BloodPressureActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.edit_pressure_layout, null);
                builder.setView(dialogView);

                editHeartRateText = (EditText) dialogView.findViewById(R.id.heartRateText);
                editSystolicText = (EditText) dialogView.findViewById(R.id.systolicText);
                editDiastolicText = (EditText) dialogView.findViewById(R.id.diastolicText);
                editDateText = (TextView) dialogView.findViewById(R.id.dateText);
//                editTimeText = (TextView) dialogView.findViewById(R.id.editTimeText);

                editHeartRateText.setText(item2);
                editSystolicText.setText(item3);
                editDiastolicText.setText(item4);
                editDateText.setText(item1);

                dbID = myDB.getSinglePressureDataID(username, item1);

                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public int sortByDate(ArrayList<String> dataList, String dataListItem) {
        int index = 0;
        for (int i = 0; i < dataList.size(); i++) {
            int year1 = Integer.parseInt(dataList.get(i).split("/")[2]);
            int year2 = Integer.parseInt(dataListItem.split("/")[2]);
            int month1 = Integer.parseInt(dataList.get(i).split("/")[1]);
            int month2 = Integer.parseInt(dataListItem.split("/")[1]);
            int day1 = Integer.parseInt(dataList.get(i).split("/")[0]);
            int day2 = Integer.parseInt(dataListItem.split("/")[0]);
            if (year1 < year2) return i;
            else if (year1 == year2 && month1 < month2) return i;
            else if (year1 == year2 && month1 == month2 && day1 < day2) return i;
        }
        return index;
    }

    public void update(View view) {
        alertDialog.dismiss();
        myDB.deleteItemPressureTable(dbID);
        String formattedInput = formatInput(editHeartRateText.getText().toString(), editSystolicText.getText().toString(), editDiastolicText.getText().toString(), editDateText.getText().toString());
        heartRateList.set(curPosition, Html.fromHtml(formattedInput));
        adapter.notifyDataSetChanged();
        myDB.insertPressureData(username, Integer.parseInt(editHeartRateText.getText().toString()),
                Integer.parseInt(editSystolicText.getText().toString()),
                Integer.parseInt(editDiastolicText.getText().toString()), editDateText.getText().toString());
//        String myString = myDB.getTableAsString();
//        heartRateList.add(0, Html.fromHtml(myString));
//        adapter.notifyDataSetChanged();
    }

    public void delete(View view) {
        alertDialog.dismiss();
        myDB.deleteItemPressureTable(dbID);
        heartRateList.remove(curPosition);
        adapter.notifyDataSetChanged();
    }

    private void translatePage(Boolean onStart) {

        if (language.equals("Bislama")) {
            heartRateText.setHint("Hat Ret (Pols)");
            systolicText.setHint("Sistolic Blad Presa");
            diastolicText.setHint("Diastolic Blad Presa");
            addButton.setText("Add");
            if (!onStart) {
                languageItem.setTitle("Langwij");
                export.setTitle("Expot Data");
                graphs.setTitle("Luk Graf");
                logout.setTitle("Log Aot");
            }
        }
        else if (language.equals("English")) {
            heartRateText.setHint("Heart Rate (Pulse)");
            systolicText.setHint("Systolic Blood Pressure");
            diastolicText.setHint("Diastolic Blood Pressure");
            addButton.setText("Add");
            if (!onStart) {
                languageItem.setTitle("Language");
                export.setTitle("Export Data");
                graphs.setTitle("See Graphs");
                logout.setTitle("Log Out");
            }
        }
        else if (language.equals("French")) {
            heartRateText.setHint("Rythme Cardiaque (Impulsion)");
            systolicText.setHint("La Pression Artérielle Systolique");
            diastolicText.setHint("La Pression Artérielle Diastolique");
            addButton.setText("Ajouter");
            if (!onStart) {
                languageItem.setTitle("Langue");
                export.setTitle("Exporter les données");
                graphs.setTitle("Voir les graphiques");
                logout.setTitle("Déconnecter");
            }
        }
        // heartRateList = new ArrayList();
        heartRateList = getInfoFromDB();
        adapter = new ArrayAdapter<>(BloodPressureActivity.this, android.R.layout.simple_list_item_1, heartRateList);

        heartRateListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        languageItem = menu.findItem(R.id.language);
        export = menu.findItem(R.id.export);
        graphs = menu.findItem(R.id.graphs);
        logout = menu.findItem(R.id.logout);

//        if (language.equals("Bislama")) {
//            languageItem.setTitle("Langwij");
//            export.setTitle("Expot Data");
//            graphs.setTitle("Luk Graf");
//            logout.setTitle("Log Aot");
//        }
//        else if (language.equals("English")) {
//            languageItem.setTitle("Language");
//            export.setTitle("Export Data");
//            graphs.setTitle("See Graphs");
//            logout.setTitle("Log Out");
//        }
//        else if (language.equals("French")) {
//            languageItem.setTitle("Langue");
//            export.setTitle("Exporter les données");
//            graphs.setTitle("Voir les graphiques");
//            logout.setTitle("Déconnecter");
//        }

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pressure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            startActivity(intent);
        }

        else if (id == R.id.export) {
            if (!isExternalStorageWritable()) {
                Toast.makeText(getApplicationContext(), "Storage is not currently available. Please try again later.", Toast.LENGTH_LONG);
            }
            else {
                Toast.makeText(getApplicationContext(), "File exported", Toast.LENGTH_LONG);
                File file = getStorageDir("test.csv");
                Cursor cursor = myDB.getAllPressureData(username);
                if (cursor.moveToFirst()) {
                    do {
                        String toAdd = cursor.getString(5) + "," + Integer.toString(cursor.getInt(2)) +
                                "," + Integer.toString(cursor.getInt(3)) + "," + Integer.toString(cursor.getInt(4));
                        writeToFile(toAdd, file);
                    } while (cursor.moveToNext());
                }
            }
        }
        else if (id == R.id.graphs) {
            if (myDB.getAllPressureData(username).getCount() < 2) {
                if (language.equals("English"))
                    Toast.makeText(getApplicationContext(), "Please add more data before trying to graph", Toast.LENGTH_LONG);
                else if (language.equals("Bislama"))
                    Toast.makeText(getApplicationContext(), "Plis putum moa data bifo yu traem luk graf", Toast.LENGTH_LONG);
                else if (language.equals("French"))
                    Toast.makeText(getApplicationContext(), "S'il vous plait, ajoutez plus de donnés avant d'essayer de voir les graphiques", Toast.LENGTH_LONG);
            }
            Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("language", language);
            startActivity(intent);
        }
        else if (id == R.id.bislama) {
            language = "Bislama";
            translatePage(true);
            // String pass = password;
            myDB.updateUserTable(username, language, password);

        }
        else if (id == R.id.english) {
            language = "English";
            translatePage(true);
            myDB.updateUserTable(username, language, password);
        }
        else if (id == R.id.french) {
            language = "French";
            translatePage(true);
            myDB.updateUserTable(username, language, password);
        }

        return super.onOptionsItemSelected(item);
    }

    public File getStorageDir(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileName);
        if (!file.mkdirs()) {
            Toast.makeText(getApplicationContext(), "File not properly created. Please try again later.", Toast.LENGTH_LONG);
        }
        return file;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public ArrayList<Spanned> getInfoFromDB() {
        ArrayList<Spanned> fromDB = new ArrayList<>();
        Cursor cursor = myDB.getAllPressureData(username);
        ArrayList<String> dates = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                heartRateInput = Integer.toString(cursor.getInt(2));
                diastolicInput = Integer.toString(cursor.getInt(3));
                systolicInput = Integer.toString(cursor.getInt(4));
                dateInput = cursor.getString(5);
                int index = sortByDate(dates, dateInput);
                dates.add(index, dateInput);
                fromDB.add(index, Html.fromHtml(formatInput(heartRateInput, diastolicInput, systolicInput, dateInput)));
            } while (cursor.moveToNext());
        }

        return fromDB;
    }

    public void heartInfoButtonClicked(View view) {
        String title = "";
        String message = "";
        if (language.equals("Bislama")) {
            title = "Hat Ret (Pulse)";
            message = "Hamas taem hat blo yu i pam oa lo 1 minit (60 seken)";
        }
        else if (language.equals("English")) {
            title = "Heart Rate (Pulse)";
            message = "How many times your heart beats in 1 minute (60 seconds)";
        }
        else if (language.equals("French")) {
            title = "Rythme Cardiaque (Pulse)";
            message = "Le nombre de battememnt du coeur par minute (60 secondes)";
        }

        int messageAudio = R.raw.aniwa;

        infoButtonClicked(title, message, messageAudio);
    }

    public void systolicInfoButtonClicked(View view) {
        String title = "";
        String message = "";
        if (language.equals("Bislama")) {
            title = "Sistolic Blad Presa";
            message = "Stret long taem we hat i pam, hemia amon blo fos wea blad i pus agansem wol blo rop blo blad." +
                    "Namba ia bae i moa bigwan bitim 'Diastolic Blad Presa'";
        }
        else if (language.equals("English")) {
            title = "Systolic Blood Pressure";
            message = "Measures pressure as your heart ejects blood. This number will be larger than the number for 'Diastolic" +
                    "Blood Pressure'";
        }
        else if (language.equals("French")) {
            title = "La Pression Artérielle Systolique";
            message = "Il correspond à la pression artérielle mesurée lors de la contraction du coeur. Ce numéro va être plus grand" +
                    "que le numéro pour 'La Pression Artérielle Diastolique";
        }

        int messageAudio = R.raw.dj;

        infoButtonClicked(title, message, messageAudio);
    }

    public void diastolicInfoButtonClicked(View view) {
        String title = "";
        String message = "";
        if (language.equals("Bislama")) {
            title = "Diastolic Blad Presa";
            message = "Stret long taem we hat i spel lo medel lo 2 pam, hemia amaon blo fos wea blad i pus agansem wol blo rop blo blad." +
                    "Namba ia bae i moa smol bitim 'Systolic Blad Presa'";
        }
        else if (language.equals("English")) {
            title = "Diastolic Blood Pressure";
            message = "Measures pressure when your heart relaxes and prepares for its next pump. This number will be smaller than the number" +
                    "for 'Systolic Blood Pressure'";
        }
        else if (language.equals("French")) {
            title = "La Pression Artérielle Diastolique";
            message = "Il correspond à la pression artérielle mesurée pendant la remplissage du coeur. Ce numéro va être plus petit" +
                    "que le numéro pour 'La Pression Artérielle Systolique";
        }

        int messageAudio = R.raw.loyal;

        infoButtonClicked(title, message, messageAudio);
    }

    public void infoButtonClicked(String title, String message, final int messageAudio) {
        AlertDialog.Builder alert = new AlertDialog.Builder(BloodPressureActivity.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setNegativeButton("Audio", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setNeutralButton("More Info", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setIcon(android.R.drawable.ic_dialog_info);

        final AlertDialog dialog = alert.create();
        dialog.show();

        audio = MediaPlayer.create(BloodPressureActivity.this, messageAudio);

        Button closeButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.stop();
                dialog.dismiss();
            }
        });

        Button audioButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio = MediaPlayer.create(BloodPressureActivity.this, messageAudio);
                audio.start();
            }
        });

        Button moreInfoButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MoreInfo.class);
                startActivity(intent);
                audio.stop();
                dialog.dismiss();
            }
        });
    }

    public void setCurrentDate() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String[] dateTimeString = currentDateTimeString.split(" ");
        dateText.setText(day + "/" + (month+1) + "/" + year);
//        timeText.setText(" " + dateTimeString[3].split(":")[0] + ":" + dateTimeString[3].split(":")[1] + dateTimeString[4]);
    }

    public void changeDate(View view) {
        showDialog(DATE_DIALOG);
    }

    public void changeEditDate(View view) { showDialog(EDIT_DATE_DIALOG); }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, datePickerListener,
                        year, month, day);
            case EDIT_DATE_DIALOG:
                return new DatePickerDialog(this, editDatePickerListener,
                        year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            dateText.setText(new StringBuilder()
                    .append(day).append("/").append(month+1).append("/").append(year));
        }
    };

    private DatePickerDialog.OnDateSetListener editDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            editDateText.setText(new StringBuilder()
                    .append(selectedDay).append("/").append(selectedMonth+1).append("/").append(selectedYear));
        }
    };

    public void addToList(View view) {
        heartRateInput = heartRateText.getText().toString();
        systolicInput = systolicText.getText().toString();
        diastolicInput = diastolicText.getText().toString();
        dateInput = day + "/" + (month+1) + "/" + year;

        if (heartRateInput == null || heartRateInput.length() == 0 ||
                systolicInput == null || systolicInput.length() == 0 ||
                diastolicInput == null || diastolicInput.length() == 0 ||
                dateInput == null || dateInput.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please fill every box before clicking add", Toast.LENGTH_LONG).show();
        }
        else {
            String formattedInput = formatInput(heartRateInput, systolicInput, diastolicInput, dateInput);
            boolean result = myDB.insertPressureData(username, Integer.parseInt(heartRateInput), Integer.parseInt(systolicInput), Integer.parseInt(diastolicInput), dateInput);
            if (result == false) Toast.makeText(getApplicationContext(), "Insertion failed", Toast.LENGTH_LONG);
            else Toast.makeText(getApplicationContext(), "Insertion a success", Toast.LENGTH_LONG);
            heartRateList.add(0, Html.fromHtml(formattedInput));
            adapter.notifyDataSetChanged();
            heartRateText.setText("");
            systolicText.setText("");
            diastolicText.setText("");
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String formatInput(String heartRateInput, String systolicInput, String diastolicInput, String dateInput) {
        String heartRate = "";
        String systolic = "";
        String diastolic = "";
        if (language.equals("Bislama")) {
            heartRate = "Hat Ret: ";
            systolic = "Sistolic Blad Presa: ";
            diastolic = "Diastolic Blad Presa: ";
        }
        else if (language.equals("English")) {
            heartRate = "Heart Rate: ";
            systolic = "Systolic Blood Pressure: ";
            diastolic = "Diastolic Blood Pressure: ";
        }
        else if (language.equals("French")) {
            heartRate = "Rythme Cardiaque: ";
            systolic = "La Pression Artérielle Systolique: ";
            diastolic = "La Pression Artérielle Diastolique: ";
        }
        String input = dateInput + "<br> &nbsp; &nbsp; " + heartRate + heartRateInput;

        if (Integer.parseInt(systolicInput) < 90)
            input += "<font color='#0000ff'> <br> &nbsp; &nbsp; " + systolic + systolicInput + "</font>";
        else if (Integer.parseInt(systolicInput) < 140)
            input += "<font color='#00b300'> <br> &nbsp; &nbsp; " + systolic + systolicInput + "</font>";
        else if (Integer.parseInt(systolicInput) < 150)
            input += "<font color='#ffa31a'> <br> &nbsp; &nbsp; " + systolic + systolicInput + "</font>";
        else if (Integer.parseInt(systolicInput) < 180)
            input += "<font color='#ff751a'> <br> &nbsp; &nbsp; " + systolic + systolicInput + "</font>";
        else
            input += "<font color='red'> <br> &nbsp; &nbsp; " + systolic + systolicInput + "</font>";
        if (Integer.parseInt(diastolicInput) < 60)
            input += "<font color='#0000ff'> <br> &nbsp; &nbsp; " + diastolic + diastolicInput + "</font>";
        else if (Integer.parseInt(diastolicInput) < 90)
            input += "<font color='#00b300'> <br> &nbsp; &nbsp; " + diastolic + diastolicInput + "</font>";
        else if (Integer.parseInt(diastolicInput) < 100)
            input += "<font color='#ffa31a'> <br> &nbsp; &nbsp; " + diastolic + diastolicInput + "</font>";
        else if (Integer.parseInt(diastolicInput) < 110)
            input += "<font color='#ff751a'> <br> &nbsp; &nbsp; " + diastolic + diastolicInput + "</font>";
        else
            input += "<font color='red'> <br> &nbsp; &nbsp; " + diastolic + diastolicInput + "</font>";

        return input;
    }

    private void writeToFile(String data, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            out.write(data.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {}
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("heart", heartRateText.getText().toString());
        savedInstanceState.putString("systolic", systolicText.getText().toString());
        savedInstanceState.putString("diastolic", diastolicText.getText().toString());
        savedInstanceState.putString("date", dateText.getText().toString());
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        heartRateInput = savedInstanceState.getString("heart");
        systolicInput = savedInstanceState.getString("systolic");
        diastolicInput = savedInstanceState.getString("diastolic");
        dateInput = savedInstanceState.getString("date");
    }
}