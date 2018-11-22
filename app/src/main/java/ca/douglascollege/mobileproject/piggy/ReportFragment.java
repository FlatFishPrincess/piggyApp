package ca.douglascollege.mobileproject.piggy;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    ListView reportListView;
    TextView incomeTxt, expenseTxt, reportResultTxt, goodBadTxt;
    ImageView goodJob, badJob;
    // Currency format
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

    double income, expense, savingsAmt, overSpent;
    boolean isOverSpent;
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

        reportListView = view.findViewById(R.id.reportList);
        incomeTxt = view.findViewById(R.id.income);
        expenseTxt = view.findViewById(R.id.expense);
        reportResultTxt = view.findViewById(R.id.reportResultTxt);
        goodBadTxt = view.findViewById(R.id.goodBadTxt);
        goodJob = view.findViewById(R.id.goodJob);
        badJob = view.findViewById(R.id.badJob);

        // this list will be displayed in reportListView
        list = new ArrayList<String>();

//
//        // now we must call the field called expenseList to access the child field called expense
//        // to calculate all the expenses that the user had for this month
//        DatabaseReference expenseListRef = currentUserDB.child("expenseList").child("expense");
//        expenseListRef.keepSynced(true);


//         here we are accessing the data of the "income" field inside the "users" field of the database
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
                    income = Double.parseDouble(totalIncome);
                }else{
                    totalIncome = "0.0";
                    income = Double.parseDouble(totalIncome);
                }
//                incomeTxt.setText(CURRENCY_FORMAT.format(Double.parseDouble(totalIncome)));
                list.add("Income" + "       " + CURRENCY_FORMAT.format(Double.parseDouble(totalIncome)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // now we must call the field called expenseList to access the child field called expense
        // to calculate all the expenses that the user had for this month

        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.activity_expense_entries, R.id.expenseEntries, list);
        DatabaseReference expenseListRef = currentUserDB.child("expenseList").child("expense");
        expenseListRef.keepSynced(true);

        expense = 0;
        savingsAmt = 0;
        expenseListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    ExpenseReport expenseReport = new ExpenseReport();
                    // adding the date inside the date field of the Expense class
                    expenseReport.name = ds.child("name").getValue(String.class);
                    // adding the value inside the date field of the Expense class
                    expenseReport.value = ds.child("value").getValue(Double.class);
                    // adding the expense inside the ArrayList
                    list.add(expenseReport.getName() + "                "  + CURRENCY_FORMAT.format(expenseReport.getValue()));
                    expense = expense + ds.child("value").getValue(Double.class);
                }
                list.add("");
                list.add("Total Expense                                       " + CURRENCY_FORMAT.format(expense));
                list.add("");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // check if user has overExpense, if not display good job
        currentUserDB.child("overExpense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){

                }else {
                    overSpent = Double.parseDouble(dataSnapshot.getValue().toString());
//                    Toast.makeText(getContext(), "over" + overSpent, Toast.LENGTH_LONG).show();
                    currentUserDB.child("savingsSoFar").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() == null){
                                list.add("Savings                                                  " + CURRENCY_FORMAT.format(0));
                            }else {
                                savingsAmt = Double.parseDouble(dataSnapshot.getValue().toString());
                                list.add("Savings                                                  " + CURRENCY_FORMAT.format(savingsAmt));
                            }
                            if(savingsAmt > 0){
//                    Toast.makeText(getContext(), " " + savingsAmt, Toast.LENGTH_SHORT).show();
                                goodJob.setVisibility(View.VISIBLE);
                                list.add("Overspent Expense (daily basis)         " + CURRENCY_FORMAT.format(overSpent));
                                goodBadTxt.setText("GOOD JOB!");
                                reportResultTxt.setText("You have saved" + CURRENCY_FORMAT.format(savingsAmt));
                            } else {
//                    Toast.makeText(getContext(), " " + savingsAmt, Toast.LENGTH_SHORT).show();
                                goodBadTxt.setText("BAD JOB!");
                                reportResultTxt.setText("You have overspent " + CURRENCY_FORMAT.format(overSpent));
                                list.add("Overspent Expense (daily basis)         " + CURRENCY_FORMAT.format(overSpent));
                                badJob.setVisibility(View.VISIBLE);
                            }
                            reportListView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


}
