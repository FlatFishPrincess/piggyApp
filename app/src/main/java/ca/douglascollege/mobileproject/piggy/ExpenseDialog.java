package ca.douglascollege.mobileproject.piggy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExpenseDialog extends AppCompatDialogFragment {

//    private ExpenseDialogListener listener;
    TextView date;
    EditText amountSpent, expenseName;
    Button save, cancel;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());
    String value;
    double amount;
    String name, groupchoice;
    Spinner group;


    @Override
   public Dialog onCreateDialog(Bundle savedInstanceState){
       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       LayoutInflater inflater = getActivity().getLayoutInflater();
       View view = inflater.inflate(R.layout.expense_calendar_clicked_dialog, null);

        builder.setView(view)
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
                        String amt = amountSpent.getText().toString();
                        String name = expenseName.getText().toString();
                        Toast.makeText(getContext(), "ok clicked"+ amt + name, Toast.LENGTH_LONG).show();
                    }
                });

        amountSpent = view.findViewById(R.id.amountSpentTxt);
        expenseName = view.findViewById(R.id.expenseNameTxt);

        return builder.create();
   }
}
