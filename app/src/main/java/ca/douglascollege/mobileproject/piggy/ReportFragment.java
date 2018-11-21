package ca.douglascollege.mobileproject.piggy;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    PieChart pieChart;
    BarChart barChart;

    // Currency format
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");

    // this here catches the firebase and the database
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    // this is a database based on the child of the "users" field of the main database
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());


    // TODO: color theme, connect database and put values into graph chart array
    public ReportFragment() {
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


        // now we must call the field called expenseList to access the child field called expense
        // to calculate all the expenses that the user had for this month
        DatabaseReference expenseListRef = currentUserDB.child("expenseList").child("expense");
        expenseListRef.keepSynced(true);


        // here we are accessing the data of the "income" field inside the "users" field of the database
        currentUserDB.child("income");
        currentUserDB.child("income").addValueEventListener(new ValueEventListener() {
            // this method is used to retrieve data from the database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String totalIncome = "";
                /* we have to have this if statement here because when the user registers its data becomes null
                    and zero and messes the program logic, so we need to throw this exception
                */
                if(dataSnapshot.getValue() != null) {
                    totalIncome = dataSnapshot.getValue().toString();
                }else{
                    totalIncome = "0.0";
                }
                IncomeTxt.setText(CURRENCY_FORMAT.format(Double.parseDouble(totalIncome)));

                incomeStored = totalIncome;
                income = Double.parseDouble(incomeStored);
                // this will get the income that the user entered, divide it by the number of days in the current month to give us the day allowance
                incomeTotal = income / daysInMonth;
                allowanceTxt.setText(CURRENCY_FORMAT.format(incomeTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}
