package gwhitmore.ict4ncd;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WeightActivity extends AppCompatActivity {

    int year;
    int month;
    int day;
    TextView dateText;
    static final int DATE_DIALOG = 0;
    static final int EDIT_DATE_DIALOG = 1;
    ListView weightListView;
    String username;
    String language;
    String password;
    int curPosition;
    String item1;
    String item2;
    String item3;
    String item4;
    ArrayList<Spanned> weightList;
    ArrayAdapter<Spanned> adapter;
    EditText weightText;
    EditText heightText;
    DBHandler myDB;
    String dateInput;
    String weightInput;
    String heightInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        username = getIntent().getStringExtra("username");
        language = getIntent().getStringExtra("language");
        password = getIntent().getStringExtra("password");

        myDB = new DBHandler(WeightActivity.this);

        dateText = (TextView) findViewById(R.id.dateText);
        weightText = (EditText) findViewById(R.id.weightText);
        heightText = (EditText) findViewById(R.id.heightText);
        weightListView = (ListView) findViewById(R.id.weightListView);

        weightList = getInfoFromDB();
        adapter = new ArrayAdapter<>(WeightActivity.this, android.R.layout.simple_list_item_1, weightList);

        weightListView.setAdapter(adapter);

        this.setTitle("Weight and BMI");

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setCurrentDate();

        weightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                String[] items = item.split("\n");
                curPosition = position;

                item1 = items[0];
                item2 = items[1].split(": ")[1].split(" ")[0];
                item3 = items[2].split(": ")[1].split(" ")[0];
                item4 = items[3].split(": ")[1].split(" ")[0];

                AlertDialog.Builder builder = new AlertDialog.Builder(WeightActivity.this);
                if (language.equals("Bislama")) builder.setTitle("Changem Data");
                else if (language.equals("English")) builder.setTitle("Edit Data");
                else if (language.equals("French")) builder.setTitle("Changer la donnée");
                LayoutInflater inflater = WeightActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.edit_weight_layout, null);
                builder.setView(dialogView);
//
//                editHeartRateText = (EditText) dialogView.findViewById(R.id.heartRateText);
//                editSystolicText = (EditText) dialogView.findViewById(R.id.systolicText);
//                editDiastolicText = (EditText) dialogView.findViewById(R.id.diastolicText);
//                editDateText = (TextView) dialogView.findViewById(R.id.dateText);
//
//                Cursor cursor = myDB.getAllHeartData();
//
//                if (cursor.moveToFirst()) {
//                    do {
//                        if (cursor.getString(1).equals(username)) {
//                            if (item1.equals(cursor.getString(5)) &&
//                                    item2.equals(Integer.toString(cursor.getInt(2))) &&
//                                    item3.equals(Integer.toString(cursor.getInt(3))) &&
//                                    item4.equals(Integer.toString(cursor.getInt(4)))) {
//                                editHeartRateText.setText(Integer.toString(cursor.getInt(2)));
//                                editSystolicText.setText(Integer.toString(cursor.getInt(3)));
//                                editDiastolicText.setText(Integer.toString(cursor.getInt(4)));
//                                editDateText.setText(cursor.getString(5));
//                                dbID = cursor.getInt(0);
//                            }
//                        }
//                    } while (cursor.moveToNext());
//                }

                AlertDialog alertDialog = builder.create();
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

    public ArrayList<Spanned> getInfoFromDB() {
        ArrayList<Spanned> fromDB = new ArrayList<>();
        Cursor cursor = myDB.getAllWeightData(username);
        ArrayList<String> dates = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                heightInput = Double.toString(cursor.getDouble(2));
                weightInput = Double.toString(cursor.getDouble(3));
                dateInput = cursor.getString(4);
                int index = sortByDate(dates, dateInput);
                dates.add(index, dateInput);
                fromDB.add(index, Html.fromHtml(formatInput(heightInput, weightInput, dateInput)));
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

    public void changeDate(View view) {
        showDialog(DATE_DIALOG);
    }

    public void changeEditDate(View view) { showDialog(EDIT_DATE_DIALOG); }

    public void addToList(View view) {
        dateInput = day + "/" + (month+1) + "/" + year;
        weightInput = weightText.getText().toString();
        heightInput = heightText.getText().toString();

        if (dateInput == null || weightInput == null || heightInput == null)
            Toast.makeText(getApplicationContext(), "Please fill all boxes", Toast.LENGTH_LONG).show();
        else {
            String formattedInput = formatInput(heightInput, weightInput, dateInput);
            myDB.insertWeightData(username, Double.parseDouble(heightInput), Double.parseDouble(weightInput), dateInput);
            weightList.add(0, Html.fromHtml(myDB.getTableAsString()));
            adapter.notifyDataSetChanged();
            weightList.add(0, Html.fromHtml(formattedInput));
            adapter.notifyDataSetChanged();

            weightText.setText("");
            heightText.setText("");
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String formatInput(String heightInput, String weightInput, String dateInput) {
        String height = "";
        String weight = "";
        String fontColor = "";
        if (language.equals("Bislama")) {
            height = "Hait: ";
            weight = "Weit: ";
        }
        else if (language.equals("English")) {
            height = "Height: ";
            weight = "Weight: ";
        }
        else if (language.equals("French")) {
            height = "Taille: ";
            weight = "Poids: ";
        }

        Double something = Double.parseDouble(heightInput) * Double.parseDouble(heightInput);

        Double bmi = Double.parseDouble(weightInput)/(Double.parseDouble(heightInput) * .01 * Double.parseDouble(heightInput) * .01);
        DecimalFormat df = new DecimalFormat("#.##");
        bmi = Double.valueOf(df.format(bmi));

        if (bmi < 18.5)
            fontColor = "#0000ff";
        else if (bmi < 25)
            fontColor = "#00b300";
        else if (bmi < 30)
            fontColor = "#ff751a";
        else
            fontColor = "red";

        String input = dateInput + "<br> &nbsp; &nbsp; " + height + heightInput + "cm"
                + "<br> &nbsp; &nbsp; " +  weight + weightInput + "kg" + "<font color=" + fontColor + ">"
                + "<br> &nbsp; &nbsp; " + "BMI: " + bmi;

        return input;
    }

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

//            editDateText.setText(new StringBuilder()
//                    .append(selectedDay).append("/").append(selectedMonth+1).append("/").append(selectedYear));
        }
    };
}
