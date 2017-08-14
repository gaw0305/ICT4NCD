package gwhitmore.ict4ncd;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class BloodSugarActivity extends AppCompatActivity {

    int year;
    int month;
    int day;
    TextView dateText;
    EditText bloodSugarEditText;
    static final int DATE_DIALOG = 0;
    static final int EDIT_DATE_DIALOG = 1;
    static final int TIME_DIALOG = 2;
    static final int EDIT_TIME_DIALOG = 3;
    ListView bloodSugarListView;
    ArrayAdapter<Spanned> adapter;
    String username;
    String language;
    String password;
    int curPosition;
    String item1;
    String item2;
    String item3;
    String item4;
    ArrayList bloodSugarList;
    DBHandler myDB;
    String bloodSugarInput;
    String dateInput;
    RadioGroup radioGroup;
    AlertDialog alertDialog;
    int dbID;
    EditText editBloodSugarText;
    TextView editDateText;
    RadioButton editFastingButton;
    RadioButton editAfterMealButton;
    RadioGroup editRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_sugar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bloodSugarEditText = (EditText) findViewById(R.id.bloodSugarText);
        dateText = (TextView) findViewById(R.id.dateText);
        bloodSugarListView = (ListView) findViewById(R.id.bloodSugarListView);
        username = getIntent().getStringExtra("username");
        language = getIntent().getStringExtra("language");
        password = getIntent().getStringExtra("password");
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        myDB = new DBHandler(BloodSugarActivity.this);

        bloodSugarList = getInfoFromDB();
        adapter = new ArrayAdapter<>(BloodSugarActivity.this, android.R.layout.simple_list_item_1, bloodSugarList);

        bloodSugarListView.setAdapter(adapter);

        this.setTitle("Blood Sugar");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setCurrentDate();

        bloodSugarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String item = ((TextView)view).getText().toString();
            String[] items = item.split("\n");
            curPosition = position;

            item1 = items[0];

            String[] dataList = items[1].replaceAll("\\s+", "").split(":")[1].split("||");

            String item2 = dataList[1];
            int index = 2;
            while (!dataList[index].equals("|")) {
                item2 += dataList[index];
                index ++;
            }

            index += 2;
            item3 = "";
            for (int i = index; i < dataList.length; i++)
                item3 += dataList[i];

            AlertDialog.Builder builder = new AlertDialog.Builder(BloodSugarActivity.this);
            if (language.equals("Bislama")) builder.setTitle("Changem Data");
            else if (language.equals("English")) builder.setTitle("Edit Data");
            else if (language.equals("French")) builder.setTitle("Changer la donnée");
            LayoutInflater inflater = BloodSugarActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.edit_sugar_layout, null);
            builder.setView(dialogView);

            editBloodSugarText = (EditText) dialogView.findViewById(R.id.bloodSugarText);
            editDateText = (TextView) dialogView.findViewById(R.id.dateText);
            editFastingButton = (RadioButton) dialogView.findViewById(R.id.fastingButton);
            editRadioGroup = (RadioGroup) dialogView.findViewById(R.id.radioGroup);
            editAfterMealButton = (RadioButton) dialogView.findViewById(R.id.afterMealButton);

            editBloodSugarText.setText(item2);
            editDateText.setText(item1);
            if (item3.equals("Fasting")) editFastingButton.setChecked(true);
            else editAfterMealButton.setChecked(true);

            dbID = myDB.getSingleSugarDataID(username, item1);

            alertDialog = builder.create();
            alertDialog.show();
            }
        });
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
        myDB.deleteItemSugarTable(dbID);

        int radioButtonID = editRadioGroup.getCheckedRadioButtonId();
        View radioButton = editRadioGroup.findViewById(radioButtonID);
        int index = editRadioGroup.indexOfChild(radioButton);
        String selectedButton = ((RadioButton) editRadioGroup.getChildAt(index)).getText().toString();

        String formattedInput = formatInput(editBloodSugarText.getText().toString(), selectedButton, editDateText.getText().toString());
        bloodSugarList.set(curPosition, Html.fromHtml(formattedInput));
        adapter.notifyDataSetChanged();
        myDB.insertSugarData(username, Double.parseDouble(editBloodSugarText.getText().toString()),
                selectedButton, editDateText.getText().toString());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public ArrayList<Spanned> getInfoFromDB() {
        ArrayList<Spanned> fromDB = new ArrayList<>();
        Cursor cursor = myDB.getAllSugarData(username);
        ArrayList<String> dates = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(1).equals(username)) {
                    bloodSugarInput = Integer.toString(cursor.getInt(2));
                    String foodInput = cursor.getString(3);
                    dateInput = cursor.getString(4);
                    int index = sortByDate(dates, dateInput);
                    dates.add(index, dateInput);
                    fromDB.add(index, Html.fromHtml(formatInput(bloodSugarInput, foodInput, dateInput)));
                }
            } while (cursor.moveToNext());
        }

        return fromDB;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setCurrentDate() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        dateText.setText(new StringBuilder()
                .append(day).append("/").append(month+1)
                .append("/").append(year));
    }

    public void addToList(View view) {
        bloodSugarInput = bloodSugarEditText.getText().toString();
        dateInput = day + "/" + (month+1) + "/" + year;

        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int index = radioGroup.indexOfChild(radioButton);
        String selectedButton = ((RadioButton) radioGroup.getChildAt(index)).getText().toString();

        if (bloodSugarInput == null || bloodSugarInput.length() == 0 ||
                dateInput == null || dateInput.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please fill every box before clicking add", Toast.LENGTH_LONG).show();
        }
        else {
            String formattedInput = formatInput(bloodSugarInput, selectedButton, dateInput);
            boolean result = myDB.insertSugarData(username, Double.parseDouble(bloodSugarInput), selectedButton, dateInput);
//            if (result == false) Toast.makeText(getApplicationContext(), "Insertion failed", Toast.LENGTH_LONG).show();
//            else Toast.makeText(getApplicationContext(), "Insertion a success", Toast.LENGTH_LONG).show();
            bloodSugarList.add(0, Html.fromHtml(formattedInput));
            adapter.notifyDataSetChanged();
            bloodSugarEditText.setText("");
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String formatInput(String bloodSugarInput, String selectedButton, String dateInput) {
        String bloodSugar = "";
        String fontColor = "";
        if (language.equals("Bislama")) {
            bloodSugar = "Blad suga ";
        }
        else if (language.equals("English")) {
            bloodSugar = "Blood Sugar: ";
        }
        else if (language.equals("French")) {
            bloodSugar = "Sucre dans le sang: ";
        }
        if (selectedButton.equals("Fasting")) {
            if (Double.parseDouble(bloodSugarInput) < 4)
                fontColor = "#0000ff";
            else if (Double.parseDouble(bloodSugarInput) < 7)
                fontColor = "#00b300";
            else
                fontColor = "red";
            ((RadioButton) findViewById(R.id.fastingButton)).setSelected(false);
        }
        else {
            if (Double.parseDouble(bloodSugarInput) < 7)
                fontColor = "#0000ff";
            else if (Double.parseDouble(bloodSugarInput) < 10)
                fontColor = "#00b300";
            else
                fontColor = "red";
            ((RadioButton) findViewById(R.id.afterMealButton)).setSelected(false);
        }

        String input = dateInput + "<br> &nbsp; &nbsp; " + "<font color=" + fontColor + ">"
                + bloodSugar + bloodSugarInput + "<font color='black'> "
                + "&nbsp; &nbsp; || &nbsp; &nbsp; " + selectedButton;

        return input;
    }

    public void changeDate(View view) {
        showDialog(DATE_DIALOG);
    }

//    public void changeTime(View view) { showDialog(TIME_DIALOG); }

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
//            case TIME_DIALOG:
//                return new TimePickerDialog(this, timePickerListener);
//            case EDIT_TIME_DIALOG:
//                return new TimePickerDialog(this, editTimePickerListener);
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

//            editDateText.setText(new StringBuilder()
//                    .append(selectedDay).append("/").append(selectedMonth+1).append("/").append(selectedYear));
        }
    };
}
