package ca.douglascollege.mobileproject.piggy;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    View rootView;
    CardView income, expense, report, savings;
    public DashboardFragment() {
        // Required empty public constructor
    }


    // TODO: when user click, transition with animation

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Income card clicked
        income = rootView.findViewById(R.id.income);
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), IncomeActivity.class));
            }
        });

        // Expense card clicked
        expense = rootView.findViewById(R.id.expense);
        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ExpenseActivity.class));
            }
        });

        // Report card clicked
        report = rootView.findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReportActivity.class));
            }
        });

        // Savings card clicked
        savings = rootView.findViewById(R.id.savings);
        savings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SavingsActivity.class));
            }
        });
        return  rootView;
    }
}
