package ca.douglascollege.mobileproject.piggy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

    View view;
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
        returnBTn = view.findViewById(R.id.returnFab);
        total = 0;
        incomeTotal = 0;

        expenseTxt = view.findViewById(R.id.txtExpense);
        CalendarView calendarView=view.findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        final int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        expenseTxt.setText(CURRENCY_FORMAT.format(total));

        IncomeTxt = (TextView)view.findViewById(R.id.txtIncome);
        allowanceTxt = (TextView)view.findViewById(R.id.txtAllowance);
        total = 0;
        incomeTotal = 0;

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
//                Intent i = new Intent(getActivity(), DayExpenseActivity.class);
//                i.putExtra("key",value);
//                startActivity(i);
                openDialog();
            }

        });
        return view;
    }

    public void openDialog(){
        ExpenseDialog expenseDialog = new ExpenseDialog();
    }

}
