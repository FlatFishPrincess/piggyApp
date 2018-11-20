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
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

    // this view is for sharing fragment
    View view;
    // Currency format
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");

//    double expense;
    double total;
    double incomeTotal;
    TextView expenseTxt, IncomeTxt, allowanceTxt;

    String incomeStored;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());

    // date string for storing database
    String dateValue;

    // Calendar clicked, Dialog input values
    TextView amountSpentTxt, expenseNameTxt;
    double amountSpent;
    String expenseName, groupchoice;
    Spinner group;


    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_expense, container, false);
        IncomeTxt = view.findViewById(R.id.txtIncome);
        allowanceTxt = view.findViewById(R.id.txtAllowance);
        total = 0;
        incomeTotal = 0;

        expenseTxt = view.findViewById(R.id.txtExpense);
        CalendarView calendarView=view.findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        final int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        expenseTxt.setText(CURRENCY_FORMAT.format(total));


        // Get current income amt from database.
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

        // If calendar is clicked, dialog(fragment_expense_dialog.xml) triggered
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

//                Intent i = new Intent(getActivity(), DayExpenseActivity.class);
//                i.putExtra("key",value);
//                startActivity(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View expenseView = inflater.inflate(R.layout.fragment_expense_dialog, null);

                // dateValue will be stored as date in database
                dateValue= month + "/" + dayOfMonth + "/" + year;

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
                                amountSpent = Double.parseDouble(amountSpentTxt.getText().toString());
                                expenseName = expenseNameTxt.getText().toString();
                                groupchoice = group.getSelectedItem().toString();

                                DatabaseReference dbref = currentUserDB.child("expenseList").child("expense").push();  ;
                                dbref.child("date").setValue(dateValue);
                                dbref.child("value").setValue(amountSpent);
                                dbref.child("category").setValue(groupchoice);

                                Toast.makeText(getContext(), " "+ amountSpent + expenseName + groupchoice + dateValue, Toast.LENGTH_LONG).show();
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

    public void openDialog(){
        ExpenseDialogFragment expenseDialogFragment = new ExpenseDialogFragment();
        expenseDialogFragment.show(getFragmentManager(), "expense_dialog");
    }
}
