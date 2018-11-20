package ca.douglascollege.mobileproject.piggy;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
public class IncomeFragment extends Fragment {

    // user's income input value
    private double income = 0.0;
    Button incomeBtn;
    TextView resultTxt;

    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");
    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("income");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());

    public IncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        incomeBtn = view.findViewById(R.id.btnIncome);
        resultTxt = view.findViewById(R.id.txtResult);

        currentUserDB.child("income").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resultTxt.setText(CURRENCY_FORMAT.format(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Income button clicked, pop up (here, alert dialog) will be displayed
        incomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter Your Income");
                // Set up the input
                final EditText user_input = new EditText(getActivity());
                // Specify the type of input expected; input type is number
                user_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_NUMBER);
                builder.setView(user_input);


                // Ok button clicked
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        income = Double.parseDouble(user_input.getText().toString());

                        currentUserDB.child("income").setValue(income);

                        Toast.makeText(getActivity(), "input" + income, Toast.LENGTH_LONG).show();
                        resultTxt.setText(CURRENCY_FORMAT.format(income));
                    }
                });
                // cancel button clicked
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        return view;
    }
}
