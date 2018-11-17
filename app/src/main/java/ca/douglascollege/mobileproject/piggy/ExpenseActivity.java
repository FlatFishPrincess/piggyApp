package ca.douglascollege.mobileproject.piggy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {
    double expense;
    double total;
    double incomeTotal;
    TextView expenseTxt, IncomeTxt, allowanceTxt;
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");
    String incomeStored;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());
    FloatingActionButton returnBTn;


    //TODO expense pop up dialog
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Floating button clicked, go to report activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExpenseActivity.this, ReportActivity.class));
            }
        });
        IncomeTxt = (TextView)findViewById(R.id.txtIncome);
        allowanceTxt = (TextView)findViewById(R.id.txtAllowance);
        returnBTn = (FloatingActionButton)findViewById(R.id.returnFab);
        total = 0;
        incomeTotal = 0;
        expenseTxt = (TextView)findViewById(R.id.txtExpense);
        CalendarView calendarView=(CalendarView) findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        final int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        expenseTxt.setText(CURRENCY_FORMAT.format(total).toString());


        returnBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpenseActivity.this, DashboardActivity.class));
            }
        });

        currentUserDB.child("income");
        currentUserDB.child("income").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String totalIncome = dataSnapshot.getValue().toString();
                IncomeTxt.setText(CURRENCY_FORMAT.format(Double.parseDouble(totalIncome)));//dataSnapshot.getValue().toString());
                incomeStored = totalIncome;
                incomeTotal = Double.parseDouble(incomeStored) / daysInMonth;
                allowanceTxt.setText(CURRENCY_FORMAT.format(incomeTotal).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String value= month + "/" + dayOfMonth + "/" + year;
                Intent i = new Intent(ExpenseActivity.this, DayExpenseActivity.class);
                i.putExtra("key",value);
                startActivity(i);
            }

        });
    }
}
