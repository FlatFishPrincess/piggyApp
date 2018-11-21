package ca.douglascollege.mobileproject.piggy;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphChartFragment extends Fragment {

    PieChart pieChart;
    BarChart barChart;

    // Currency format
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");

    // this here catches the firebase and the database
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    // this is a database based on the child of the "users" field of the main database
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());

    public GraphChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_report, container, false);

        // get piechart and bar chart
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);

        // call piechart method
        getPieChart();
        // call bar chart method
        getBarChart();

        // now we must call the field called expenseList to access the child field called expense
        // to calculate all the expenses that the user had for this month
        DatabaseReference expenseListRef = currentUserDB.child("expenseList").child("expense");
        expenseListRef.keepSynced(true);

        return view;
    }

    private void getPieChart(){

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setHighlightPerTapEnabled(true);

        //pie entry values
        // put values into entires array
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(18.5f, "Grocery"));
        entries.add(new PieEntry(26.7f, "Clothes"));
        entries.add(new PieEntry(24.0f, "Utility"));
        entries.add(new PieEntry(30.8f, "Transportation"));

        //add list values to dataset object
        PieDataSet pieSet = new PieDataSet(entries, "Expense Category");

        //styling text color and pie chart space
        pieSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieSet.setSliceSpace(3f);

        //entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(16f);

        //center (category) style
        pieChart.setCenterText("Category");
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextColor(Color.BLACK);

        PieData data = new PieData(pieSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(16f);

        pieChart.setData(data);
        pieChart.animateY(2000, Easing.EasingOption.EaseInOutCubic);
        pieChart.invalidate(); // refresh
    }

    private void getBarChart(){

        //bar chart values
        ArrayList<BarEntry> entries = new ArrayList<>();
        float yValues = 0;
        for(int i = 0; i<7; i++){
            entries.add(new BarEntry(i, yValues));
            yValues++;
        }
//        entries.add(new BarEntry(1, 0));

        //set all entries into bar data set
        BarDataSet dataset = new BarDataSet(entries, "Expense per month");

        //x labels for bar chart
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
//        String[] labels = new String[] { "January", "February", "March", "April", "May", "June"};
//        Log.d("labels array", "labels: " + Arrays.toString(labels));
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //create bar data graph
        BarData data = new BarData(dataset);

        // set custom bar width
        data.setBarWidth(0.9f);

        //setting bar colors
        dataset.setColors(ColorTemplate.PASTEL_COLORS);
//        public static final int[] PASTEL_COLORS = {
//                Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
//                Color.rgb(191, 134, 134), Color.rgb(179, 48, 80)
//        };
//        pastel color method

        String description = "description here!!!!!!!!";
        barChart.getDescription().setText(description);
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars

        barChart.animateXY(1500, 1500);
        barChart.invalidate();

    }
}
