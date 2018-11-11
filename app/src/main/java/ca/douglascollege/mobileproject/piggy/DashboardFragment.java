package ca.douglascollege.mobileproject.piggy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_dashboard, container, false);
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        income = rootView.findViewById(R.id.income);
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("income", "income clickd!!!!!");
            }
        });
        return  rootView;
    }



}
