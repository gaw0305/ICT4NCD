package gwhitmore.ict4ncd;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Arrays;

public class GraphActivity extends AppCompatActivity {

    DBHandler myDB;
    String username;
    Spinner spinner;
    GraphView line_graph;
    LineGraphSeries<DataPoint> line_series;
    String language;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        username = getIntent().getStringExtra("username");
        language = getIntent().getStringExtra("language");
        myDB = new DBHandler(this);
        spinner = (Spinner) findViewById(R.id.graphSpinner);
        line_graph = (GraphView) findViewById(R.id.graph);

        this.setTitle("");

        // Gets all the blood pressure data from the database for the current user
        final Cursor cursor = myDB.getAllPressureData(username);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Systolic Blood Pressure"))
                    updateGraph(1, 70, 200, 180, 150, 140, 90, cursor);
                else if (selectedItem.equals("Diastolic Blood Pressure"))
                    updateGraph(2, 50, 120, 110, 100, 90, 60, cursor);
                else if (selectedItem.equals("Heart Rate"))
                    updateGraph(0, 50, 100, 0, 0, 0, 0, cursor);
            }
            // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updateGraph(1, 70, 200, 180, 150, 140, 90, cursor);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_pressure, menu);
        return true;
    }

    public boolean sortByDate(ArrayList<ArrayList<String>> dataList, ArrayList<String> dataListItem) {
        boolean previousDate = false;
        for (int i = 0; i < dataList.size(); i++) {
            int year1 = Integer.parseInt(dataList.get(i).get(3).split("/")[2]);
            int year2 = Integer.parseInt(dataListItem.get(3).split("/")[2]);
            int month1 = Integer.parseInt(dataList.get(i).get(3).split("/")[1]);
            int month2 = Integer.parseInt(dataListItem.get(3).split("/")[1]);
            int day1 = Integer.parseInt(dataList.get(i).get(3).split("/")[0]);
            int day2 = Integer.parseInt(dataListItem.get(3).split("/")[0]);
            // If the year of the current item in the list is greater than the year of the new data
            // point, add the new data point before the current item, and set previousDate to true,
            // so the item is not added twice
            if (year1 > year2) {
                dataList.add(i, dataListItem);
                previousDate = true;
                break;
            }
            // If the year is the same, but the month of the current item in the list is greater than the
            // month of the new data point, add the new data point before the current item, and set
            // previousDate to true, so the item is not added twice
            else if (year1 == year2 && month1 > month2) {
                dataList.add(i, dataListItem);
                previousDate = true;
                break;
            }
            // If the year is the same and the month is the same, but the day of the current item in the
            // list is greater than the day of the new data point, add the new data point before the current
            // item, and set previousDate to true, so the item is not added twice
            else if (year1 == year2 && month1 == month2 && day1 > day2) {
                dataList.add(i, dataListItem);
                previousDate = true;
                break;
            }
        }
        // If the item has not already been added, set previousDate to false, and reset the item to be added
        return previousDate;
    }

    /**
     * Loads the graph initially, then updates it when the user chooses a different data set to be graphed
     *
     *    int dbPosition: the column number of the item to access in the database
     *    int yMin: the default minimum X-value
     *    int yMax: the default maximum X-value
     *    int crossLine1: the horizontal line crossing the graph to change the color of the top section red,
     *                    and the second section orange
     *    int crossLine2: the horizontal line crossing the graph to change the color of the next section yellow
     *    int crossLine3: the horizontal line crossing the graph to change the color of the next section green
     *    int crossLine4: the horizontal line crossing the graph to change the color of the next section blue
     */
    public void updateGraph(int dbPosition, int yMin, int yMax, int crossLine1, int crossLine2, int crossLine3, int crossLine4, Cursor cursor) {
        int numRows = 0;
        final ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        ArrayList<String> dataListItem = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                // Keeps track of the number of items for the user in the database
                numRows++;
                // Adds Heart Rate number, Systolic Blood Pressure number, Diastolic Blood Pressure number,
                // and Date to a list
                for (int i = 2; i < 5; i++)
                    dataListItem.add(Integer.toString(cursor.getInt(i)));
                dataListItem.add(cursor.getString(5));

                // Adds the new data list item to a larger data list with all the information from the database
                // in the order it should be based on date
                boolean previousDate;
                previousDate = sortByDate(dataList, dataListItem);
                if (!previousDate) dataList.add(dataListItem);
                dataListItem = new ArrayList<>();
            } while (cursor.moveToNext());
        }

        // Create an array of DataPoints with the same number of items as there are rows
        DataPoint[] data = new DataPoint[numRows+1];
        // List of month lengths, to be used to convert dates to integers
        ArrayList<Integer> monthLengths = new ArrayList<>(Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31));

        int index = 0;
        int dataPointX = 0;
        int minY = yMin;
        int maxY = yMax;
        int curDay;
        int prevDay = 0;
        int curMonth;
        int curYear;
        int prevYear = 0;
        int baseLineDateNumber = 0;

        // For each item in the list, convert the date to a number out of 365 (or 730 if two years of data, and so on)
        // this will create correct spacing on the graph
        for (int i = 0; i < dataList.size(); i++) {
            // Check for leap year
            if (Integer.parseInt(dataList.get(i).get(3).split("/")[2]) == 2020 ||
                    Integer.parseInt(dataList.get(i).get(3).split("/")[2]) == 2024 ||
                    Integer.parseInt(dataList.get(i).get(3).split("/")[2]) == 2028) {
                monthLengths.set(1, 29);
            }
            else monthLengths.set(1, 28);
            // If the data item is larger or smaller than the preset default min or max Y coordinates, set the min/max value
            // to the data item number
            if (Integer.parseInt(dataList.get(i).get(dbPosition)) < minY)
                minY = Integer.parseInt(dataList.get(i).get(dbPosition));
            if (Integer.parseInt(dataList.get(i).get(dbPosition)) > maxY)
                maxY = Integer.parseInt(dataList.get(i).get(dbPosition));

            // Splits the date item into three numbers, month, day, and year
            curMonth = Integer.parseInt(dataList.get(i).get(3).split("/")[1]) - 1;
            curDay = Integer.parseInt(dataList.get(i).get(3).split("/")[0]);
            curYear = Integer.parseInt(dataList.get(i).get(3).split("/")[2]);

            // Get a starting number for the first date, to compare with later dates and used to determine x-coordinates
            if (prevDay == 0) {
                baseLineDateNumber = baseLineDateNumber + curDay;
                for (int j = 0; j < curMonth; j++) {
                    baseLineDateNumber += monthLengths.get(j);
                    curDay = baseLineDateNumber;
                }
            }
            // Find the correct number of dates in between the previous point and the current one
            else {
                while (prevYear < curYear) {
                    for (int j = 0; j < curMonth; j++)
                        curDay += monthLengths.get(j);
                    curDay = baseLineDateNumber + curDay;
                    prevYear++;
                }
                for (int j = 0; j < curMonth; j++)
                    curDay += monthLengths.get(j);
                while (curDay > prevDay + 1)
                    prevDay++;
            }

            dataPointX = curDay - baseLineDateNumber;
            data[index] = new DataPoint(dataPointX, Integer.parseInt(dataList.get(i).get(dbPosition)));
            ArrayList<String> newPoint = new ArrayList<>();
            for (int a = 0; a < dataList.get(i).size(); a++)
                newPoint.add(dataList.get(i).get(a));
            newPoint.add(Integer.toString(dataPointX));
            dataList.set(i, newPoint);
            if (index == 0) {
                index++;
                data[index] = new DataPoint(dataPointX, Integer.parseInt(dataList.get(i).get(dbPosition)));
            }
            prevDay = curDay;
            prevYear = curYear;
            dataPointX++;
            index++;
        }
        int i = 0;
        while ((maxY-minY) %4 != 0) {
            if (i%2 == 0) maxY++;
            else minY--;
            i++;
        }
        int addedNum = 0;
        while ((dataPointX) % 4 != 0) {
            dataPointX++;
            addedNum++;
        }

        LineGraphSeries<DataPoint> line1 = newlineGraphSeries(maxY, dataPointX, "#ff4d4d");
        LineGraphSeries<DataPoint> line2 = newlineGraphSeries(crossLine1, dataPointX, "#ff8533");
        LineGraphSeries<DataPoint> line3 = newlineGraphSeries(crossLine2, dataPointX, "#ffff33");
        LineGraphSeries<DataPoint> line4 = newlineGraphSeries(crossLine3, dataPointX, "#00e600");
        LineGraphSeries<DataPoint> line5 = newlineGraphSeries(crossLine4, dataPointX, "#6666ff");

        line_graph.removeAllSeries();

        line_series = new LineGraphSeries<>(data);
        if (crossLine1 != 0) {
            line_graph.addSeries(line1);
            line_graph.addSeries(line2);
            line_graph.addSeries(line3);
            line_graph.addSeries(line4);
            line_graph.addSeries(line5);
        }
        line_graph.addSeries(line_series);

        line_series.setDrawDataPoints(true);
        line_series.setDataPointsRadius(10);
        line_series.setColor(Color.BLACK);
        line_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GraphActivity.this);
                int curData = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    if (Integer.parseInt(dataList.get(i).get(4)) == dataPoint.getX()) {
                        curData = i;
                        break;
                        }
                }
                alert.setTitle(dataList.get(curData).get(3));
                alert.setMessage("Systolic Blood Pressure: " + Integer.parseInt(dataList.get(curData).get(1))
                        + "\nDiastolic Blood Pressure: " + Integer.parseInt(dataList.get(curData).get(2))
                        + "\nHeart Rate: " + Integer.parseInt(dataList.get(curData).get(0)));
                alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.setIcon(android.R.drawable.ic_dialog_info);

                final AlertDialog dialog = alert.create();
                dialog.show();


                Button closeButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(line_graph);

        ArrayList<String> labels = new ArrayList<>();

        int startDay = Integer.parseInt(dataList.get(0).get(3).split("/")[0]);
        int startMonth = Integer.parseInt(dataList.get(0).get(3).split("/")[1]);
        int startYear = Integer.parseInt(dataList.get(0).get(3).split("/")[2]);
        startYear = startYear % 100;
        int endDay = Integer.parseInt(dataList.get(dataList.size()-1).get(3).split("/")[0]);
        int endMonth = Integer.parseInt(dataList.get(dataList.size()-1).get(3).split("/")[1]);
        int endYear = Integer.parseInt(dataList.get(dataList.size()-1).get(3).split("/")[2]);
        endYear = endYear % 100;
        if (endDay + addedNum > monthLengths.get(endMonth-1)) {
            endDay = (endDay+addedNum) % monthLengths.get(endMonth-1);
            endMonth++;
            if (endMonth == 13) {
                endMonth = 1;
                endYear++;
            }
        }
        else endDay = endDay+addedNum;
//        String startDate = Integer.toString(startDay) + "/" + Integer.toString(startMonth) + "/" + Integer.toString(startYear);
//        String endDate = Integer.toString(endDay) + "/" + Integer.toString(endMonth) + "/" + Integer.toString(endYear);
        String startDate = Integer.toString(startDay) + "/" + Integer.toString(startMonth);
        String endDate = Integer.toString(endDay) + "/" + Integer.toString(endMonth);
        labels.add(startDate);
        boolean keepGoing = true;
        while (keepGoing) {
            if (monthLengths.get(startMonth-1) == startDay) {
                startDay = 1;
                startMonth++;
                if (startMonth == 13) {
                    startMonth = 1;
                    startYear++;
                }
            }
            else startDay++;

            if (startDay == endDay && startMonth == endMonth && startYear == endYear)
                keepGoing = false;
            labels.add(Integer.toString(startDay) + "/" + Integer.toString(startMonth));
            // labels.add(Integer.toString(startDay) + "/" + Integer.toString(startMonth) + "/" + Integer.toString(startYear));
        }
        labels.add(endDate);

        int divider = labels.size()/4;

        ArrayList<String> newLabels = new ArrayList<>();
        newLabels.add(labels.get(0));
        newLabels.add(labels.get(divider));
        newLabels.add(labels.get(divider+divider));
        newLabels.add(labels.get(divider+divider+divider));
        newLabels.add(labels.get(labels.size()-1));


        staticLabelsFormatter.setHorizontalLabels(newLabels.toArray(new String[newLabels.size()]));
        line_graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

//        line_graph.getViewport().setScrollable(true); // enables horizontal scrolling
//        line_graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling

        // set manual X bounds
        line_graph.getViewport().setXAxisBoundsManual(true);
//        line_graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphActivity.this));
        line_graph.getViewport().setMinX(0);
        line_graph.getViewport().setMaxX(dataPointX);

        // set manual Y bounds
        line_graph.getViewport().setYAxisBoundsManual(true);
        line_graph.getViewport().setMinY(minY);
        line_graph.getViewport().setMaxY(maxY);

        line_graph.getViewport().setScrollable(true);
    }

    public LineGraphSeries<DataPoint> newlineGraphSeries(int yValue, int xValue, String color) {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, yValue),
                new DataPoint(xValue, yValue),
        });
        series.setColor(Color.BLACK);
        series.setDrawBackground(true);
        series.setBackgroundColor(Color.parseColor(color));

        return series;
    }
}
