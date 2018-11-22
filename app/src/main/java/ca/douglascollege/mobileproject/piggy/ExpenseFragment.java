package ca.douglascollege.mobileproject.piggy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

    // Currency format
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");

    // declare variables
    double incomeTotal, expense, total;
    TextView expenseTxt, IncomeTxt, allowanceTxt, totalTxt;
    String incomeStored;
    double income;

    // this here catches the firebase and the database
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    // this is a database based on the child of the "users" field of the main database
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());

    // date string for storing database
    String dateValue;

    // When calendar clicked, Dialog pops up, below is input values from dialog
    // these are the same as DayExpenseActivity
    TextView amountSpentTxt, expenseNameTxt, dateTxt;
    double amount;
    String name, groupchoice;
    Spinner group;


    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_expense, container, false);
        IncomeTxt = view.findViewById(R.id.txtIncome);
        allowanceTxt = view.findViewById(R.id.txtAllowance);
        expenseTxt = view.findViewById(R.id.txtExpense);
        totalTxt = view.findViewById(R.id.txtTotal);
        CalendarView calendarView=view.findViewById(R.id.calendarView);

        total = 0;
        incomeTotal = 0;

        // this here will use the calendar built-in widget from Android Studio, the getInstance gets the current date.
        Calendar cal = Calendar.getInstance();
        // this variable here stores how many days there are in the current month
        final int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        // setText in expense result, default value is 0
        expenseTxt.setText(CURRENCY_FORMAT.format(total));


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

        // now we must call the field called expenseList to access the child field called expense
        // to calculate all the expenses that the user had for this month
        DatabaseReference expenseListRef = currentUserDB.child("expenseList").child("expense");
        expenseListRef.keepSynced(true);
        expenseListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expense = 0;
                String val = "";
                // we store the expenses in an ArrayList because later we will have to sum all of them
                // It's an ArrayList of Expense that is a class created to store every record
                ArrayList<Expense> expenses = new ArrayList<Expense>();
                Expense exp;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    exp = new Expense();
                    // adding the date inside the date field of the Expense class
                    exp.date = ds.child("date").getValue(String.class);
                    // adding the value inside the date field of the Expense class
                    exp.value = ds.child("value").getValue(Double.class);
                    // adding the expense inside the ArrayList
                    expenses.add(exp);
                    expense = expense + ds.child("value").getValue(Double.class);
                }
                // displaying the total expense into the expense text field
                expenseTxt.setText(CURRENCY_FORMAT.format(expense));
                try {
                    // here is calculating the total
                    total = CURRENCY_FORMAT.parse(IncomeTxt.getText().toString()).doubleValue() - expense;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                totalTxt.setText(CURRENCY_FORMAT.format(total));

                // here we call a method that was created to organize the expenses according to the date
                expenses = OrganizeByDate(expenses);
                Double baseDailyAllowance = 0.0;
                Double inc = 0.0;
                try {
                    inc = CURRENCY_FORMAT.parse(IncomeTxt.getText().toString()).doubleValue();
                    baseDailyAllowance = CURRENCY_FORMAT.parse(IncomeTxt.getText().toString()).doubleValue() / daysInMonth;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // here we call a method to see if the expense of a specifc day is greater than the daily allowance
                Double overExp = GetOverExpenses(expenses, baseDailyAllowance);

                // a new field of the database is created called overExpense and will store how much the user has overspent for the day
                currentUserDB.child("overExpense").setValue(overExp);

                //updating today's allowance
                // the 5 inside get is because it corresponds of the month value of the Calendar
                // changed 5 to DAY_OF_MONTH
                // TODO: get(5) => (Calendar.DAY_OF_MONTH) , is it the same?
                int day = Calendar.getInstance().get(5);

                // this method here will calculate the new allowance if the user overspent the daily allowance that was provided
                Double todaysAllowance = GetTodayAllowance(inc, baseDailyAllowance, day, daysInMonth, overExp);
                allowanceTxt.setText(CURRENCY_FORMAT.format(todaysAllowance));

                //savings
                Double savingsSoFar = GetSavingsSoFar(inc, baseDailyAllowance, day, daysInMonth, expenses);
                currentUserDB.child("savingsSoFar").setValue(savingsSoFar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // If calendar is clicked, dialog(fragment_expense_dialog.xml) triggered
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View expenseView = inflater.inflate(R.layout.fragment_expense_dialog, null);

                // dateValue will be stored as date in database
                dateValue= (month + 1) + "/" + dayOfMonth + "/" + year;
                dateTxt = expenseView.findViewById(R.id.dateTxt);
                dateTxt.setText(dateValue);
                builder.setView(expenseView)
                        .setTitle("Expense")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "cancel clicked", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                amount = Double.parseDouble(amountSpentTxt.getText().toString());
                                name = expenseNameTxt.getText().toString();
                                groupchoice = group.getSelectedItem().toString();

                                DatabaseReference dbref = currentUserDB.child("expenseList").child("expense").push();  ;

                                dbref.child("value").setValue(amount);
                                dbref.child("date").setValue(dateValue);
                                dbref.child("category").setValue(groupchoice);
                                dbref.child("name").setValue(name);

//                              Toast.makeText(getContext(), " "+ amount + name + groupchoice + dateValue, Toast.LENGTH_LONG).show();
                            }
                        });
                amountSpentTxt = expenseView.findViewById(R.id.amountSpentTxt);
                expenseNameTxt = expenseView.findViewById(R.id.expenseNameTxt);
                group = expenseView.findViewById(R.id.categorySpinner);
                builder.show();
            }

        });
        return view;
    }

    // this is the method that organizes all the dates
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

    // this method gets all the expenses that are over the daily allowance
    public Double GetOverExpenses(ArrayList<Expense> input, Double dailyAllow){
        Double output = 0.0;

        // get(1) => Calendar.YEAR
        // TODO: get(1) => Calendar.YEAR
        String todayS = "" + Calendar.getInstance().get(1);//today's year
        // TODO: get(2) + 1 => Calendar.MONTH + 1
        int mon = Calendar.getInstance().get(2) + 1; //today's month get(2)+1; //before
        if(mon < 10){
            todayS = todayS + "0" + mon;
        }else{
            todayS = todayS + mon;
        }
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);//get today's day
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

    public int DateToInt(String date){
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

    private Double GetSavingsSoFar(Double incomeValue, Double baseDailyAllowance, int today, int numberOfDays, ArrayList<Expense> expenses){
        Double output = 0.0;
        int expensesCount = 0;
        Double overExp = 0.0;
        ArrayList<Expense> listOfExpenses = expenses;
        Double currentAllowance = baseDailyAllowance;

        for(int currentDay = 0; currentDay < today; currentDay++){
            if(listOfExpenses.size() > expensesCount) {
                Expense exp = listOfExpenses.get(expensesCount);

                if (Integer.parseInt(exp.date.split("/")[1]) == (currentDay+1)) {
                    if (exp.value > currentAllowance) {
                        overExp = overExp + (exp.value - currentAllowance);
                        currentAllowance = GetTodayAllowance(incomeValue, baseDailyAllowance, currentDay, numberOfDays, overExp);
                    } else {
                        output = output + (currentAllowance - exp.value);
                    }

                    expensesCount++;
                } else {
                    output = output + currentAllowance;
                }
            }
            else{
                output = output + currentAllowance;
            }

        }


        return output;
    }
}
