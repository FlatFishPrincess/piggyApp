package ca.douglascollege.mobileproject.piggy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {
    double incomeTotal, expense, total;
    TextView expenseTxt, IncomeTxt, allowanceTxt, totalTxt;
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");
    String incomeStored;
    double income;
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
        expense = 0;
        incomeTotal = 0;
        total = 0;
        expenseTxt = (TextView)findViewById(R.id.txtExpense);
        totalTxt = (TextView)findViewById(R.id.txtTotal);
        CalendarView calendarView=(CalendarView) findViewById(R.id.calendarView);


        Calendar cal = Calendar.getInstance();
        final int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);


        expenseTxt.setText(CURRENCY_FORMAT.format(total));


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
                IncomeTxt.setText(CURRENCY_FORMAT.format(Double.parseDouble(totalIncome)));

                incomeStored = totalIncome;
                income = Double.parseDouble(incomeStored);
                incomeTotal = income / daysInMonth;
                allowanceTxt.setText(CURRENCY_FORMAT.format(incomeTotal).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference expenseListRef = currentUserDB.child("expenseList").child("expense");
        expenseListRef.keepSynced(true);
        expenseListRef.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                expense = 0;
                String val = "";
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    expense = expense + ds.child("value").getValue(Double.class);
                }
                expenseTxt.setText(CURRENCY_FORMAT.format(expense));
                try {
                    total = CURRENCY_FORMAT.parse(IncomeTxt.getText().toString()).doubleValue() - expense;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                totalTxt.setText(CURRENCY_FORMAT.format(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String value= ((month+1) + "/" + dayOfMonth + "/" + year);
                Intent i = new Intent(ExpenseActivity.this, DayExpenseActivity.class);
                i.putExtra("key",value);
                startActivity(i);
            }

        });
    }
}
