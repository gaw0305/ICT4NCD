package gwhitmore.ict4ncd;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class AllDataActivity extends AppCompatActivity {

    String username;
    String password;
    String language;
    ListView allDataListView;
    ArrayList<Spanned> allDataList;
    ArrayAdapter<Spanned> adapter;
    DBHandler myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");
        language = getIntent().getStringExtra("language");
        allDataListView = (ListView) findViewById(R.id.allDataListView);
        myDB = new DBHandler(this);

        this.setTitle("Aggregate Data");

        allDataList = getInfoFromDB();

        adapter = new ArrayAdapter<>(AllDataActivity.this, android.R.layout.simple_list_item_1, allDataList);

        allDataListView.setAdapter(adapter);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


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
        ArrayList<String> dates = new ArrayList<>();
        Cursor pressureCursor = myDB.getAllPressureData(username);
        if (pressureCursor.moveToFirst()) {
            do {
                String heartRateInput = Integer.toString(pressureCursor.getInt(2));
                String diastolicInput = Integer.toString(pressureCursor.getInt(3));
                String systolicInput = Integer.toString(pressureCursor.getInt(4));
                String dateInput = pressureCursor.getString(5);
                String input = formatPressureInput(heartRateInput, diastolicInput, systolicInput, dateInput);
                input += checkForSameDateData("SUGAR", dateInput, false);
                input += checkForSameDateData("WEIGHT", dateInput, false);
                int index = sortByDate(dates, dateInput);
                dates.add(index, dateInput);
                fromDB.add(index, Html.fromHtml(input));
            } while (pressureCursor.moveToNext());
        }

        Cursor sugarCursor = myDB.getAllSugarData(username);
        if (sugarCursor.moveToFirst()) {
            do {
                String bloodSugarInput = Integer.toString(sugarCursor.getInt(2));
                String foodInput = sugarCursor.getString(3);
                String dateInput = sugarCursor.getString(4);
                if (!dates.contains(dateInput)) {
//                    String input = checkForSameDateData("PRESSURE", dateInput, true);
//                    String date = null;
//                    if (input.equals("")) date=dateInput;
                    String input = formatSugarInput(bloodSugarInput, foodInput, dateInput);
                    input += checkForSameDateData("WEIGHT", dateInput, false);
                    int index = sortByDate(dates, dateInput);
                    dates.add(index, dateInput);
                    fromDB.add(index, Html.fromHtml(input));
                }
            }while (sugarCursor.moveToNext());
        }

        Cursor weightCursor = myDB.getAllSugarData(username);
        if (weightCursor.moveToFirst()) {
            do {
                String heightInput = Double.toString(weightCursor.getDouble(2));
                String weightInput = Double.toString(weightCursor.getDouble(3));
                String dateInput = weightCursor.getString(4);
                if (!dates.contains(dateInput)) {
//                    String input = checkForSameDateData("PRESSURE", dateInput, true);
//                    input += checkForSameDateData("SUGAR", dateInput, true);
//                    String date = null;
//                    if (input.equals("")) date=dateInput;
                    String input = formatWeightInput(heightInput, weightInput, dateInput);
                    int index = sortByDate(dates, dateInput);
                    dates.add(index, dateInput);
                    fromDB.add(index, Html.fromHtml(input));
                }
            }while (weightCursor.moveToNext());
        }

        return fromDB;
    }

    public String checkForSameDateData(String table, String dateInput, boolean useDate) {
        String data = "";
        Cursor cursor = myDB.getSingleDataItemByDate(username, dateInput, table);
        if (cursor.moveToFirst()) {
            String firstItem = Integer.toString(cursor.getInt(2));
            String secondItem = cursor.getString(3);
            String thirdItem;
            String date = null;
            if (useDate) date = dateInput;
            if (table.equals("SUGAR"))
                data = "\n" + formatSugarInput(firstItem, secondItem, date);
            else if (table.equals("WEIGHT"))
                data = "\n" + formatWeightInput(firstItem, secondItem, date);
            else {
                thirdItem = Integer.toString(cursor.getInt(4));
                data = "\n" + formatPressureInput(firstItem, secondItem, thirdItem, date);
            }
        }
        return data;
    }

    private String formatPressureInput(String heartRateInput, String systolicInput, String diastolicInput, String dateInput) {
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

    private String formatSugarInput(String bloodSugarInput, String selectedButton, String dateInput) {
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
        }
        else {
            if (Double.parseDouble(bloodSugarInput) < 7)
                fontColor = "#0000ff";
            else if (Double.parseDouble(bloodSugarInput) < 10)
                fontColor = "#00b300";
            else
                fontColor = "red";
        }
        String input = "";
        if (dateInput != null) input = dateInput;
        input += "<font color=" + fontColor + "><br> &nbsp; &nbsp; "
                + bloodSugar + bloodSugarInput + "</font> "
                + "&nbsp; &nbsp; || &nbsp; &nbsp; " + selectedButton;

        return input;
    }

    private String formatWeightInput(String heightInput, String weightInput, String dateInput) {
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
        String input = "";
        if (dateInput != null) input = dateInput;
        input = "<br> &nbsp; &nbsp; " + height + heightInput + "cm"
                + "<br> &nbsp; &nbsp; " +  weight + weightInput + "kg" + "<font color=" + fontColor
                + "><br> &nbsp; &nbsp; " + "BMI: " + bmi + "</font>";

        return input;
    }

}
