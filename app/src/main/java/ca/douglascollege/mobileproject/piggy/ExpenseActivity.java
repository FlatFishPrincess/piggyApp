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

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
                allowanceTxt.setText(CURRENCY_FORMAT.format(incomeTotal));

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
                ArrayList<Expense> expenses = new ArrayList<Expense>();
                Expense exp;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    exp = new Expense();
                    exp.date = ds.child("date").getValue(String.class);
                    exp.value = ds.child("value").getValue(Double.class);
                    expenses.add(exp);
                    expense = expense + ds.child("value").getValue(Double.class);
                }
                expenseTxt.setText(CURRENCY_FORMAT.format(expense));
                try {
                    total = CURRENCY_FORMAT.parse(IncomeTxt.getText().toString()).doubleValue() - expense;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                totalTxt.setText(CURRENCY_FORMAT.format(total));

                expenses = OrganizeByDate(expenses);
                Double baseDailyAllowance = 0.0;
                Double inc = 0.0;
                try {
                    inc = CURRENCY_FORMAT.parse(IncomeTxt.getText().toString()).doubleValue();
                    baseDailyAllowance = CURRENCY_FORMAT.parse(IncomeTxt.getText().toString()).doubleValue() / daysInMonth;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Double overExp = GetOverExpenses(expenses, baseDailyAllowance);

                currentUserDB.child("overExpense").setValue(overExp);

                //updating today's allowance
                int day = Calendar.getInstance().get(5);

                Double todaysAllowance = GetTodayAllowance(inc, baseDailyAllowance, day, daysInMonth, overExp);
                allowanceTxt.setText(CURRENCY_FORMAT.format(todaysAllowance));
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

    public ArrayList<Expense> OrganizeByDate(ArrayList<Expense> input){
        ArrayList<Expense> output = new ArrayList<Expense>();

        for(Expense exp : input){
            if(exp.date != null) {
                if (output.size() == 0) {
                    output.add(exp);
                } else {
                    int i = 0;
                    for (Expense outExp : output) {
                        if (DateToInt(outExp.date) == DateToInt(exp.date)) {
                            outExp.value = outExp.value + exp.value;
                            break;
                        } else {
                            if (DateToInt(outExp.date) > DateToInt(exp.date)) {
                                output.add(i, exp);
                                break;
                            } else {
                                i++;
                            }
                        }

                    }
                    if (i == output.size()) {
                        output.add(exp);
                    }
                }
            }
        }

        return output;
    }

    public Double GetOverExpenses(ArrayList<Expense> input, Double dailyAllow){
        Double output = 0.0;

        String todayS = "" + Calendar.getInstance().get(1);//today's year
        int mon = Calendar.getInstance().get(2)+1; //today's month
        if(mon < 10){
            todayS = todayS + "0" + mon;
        }else{
            todayS = todayS + mon;
        }
        int day = Calendar.getInstance().get(5);//get today's day
        if(day < 10){
            todayS = todayS + "0" + day;
        }else{
            todayS = todayS + day;
        }

        int today = Integer.parseInt(todayS);


        for(Expense exp: input){
            if(exp.value > dailyAllow){
                if(DateToInt(exp.date) < today && mon == Integer.parseInt(exp.date.split("/")[0])) {
                    output = output + (exp.value - dailyAllow);
                }
            }
        }

        return output;
    }

    public int DateToInt(String date)
    {
        String[] dateS = date.split("/");
        //year
        date = dateS[2];
        //add month
        if(dateS[0].length() == 1){
            date = date + "0" + dateS[0];
        }else{
            date = date + dateS[0];
        }
        //add day
        if(dateS[1].length() == 1){
            date = date + "0" + dateS[1];
        }else{
            date = date + dateS[1];
        }

        int output = Integer.parseInt(date);

        return output;
    }

    private Double GetTodayAllowance(Double income, Double baseDailyAllowance, int today, int numberOfDays, Double overExpended){
        Double output = 0.0;

        output = (income - (((today - 1)*baseDailyAllowance) + overExpended))/(numberOfDays-today+1);

        return output;
    }
}
