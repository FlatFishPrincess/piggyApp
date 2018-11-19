package ca.douglascollege.mobileproject.piggy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class DayExpenseActivity extends AppCompatActivity {

    TextView date;
    EditText amountSpent;
    Button save, cancel;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());
    String value;
    double amount;
    String name, groupchoice;
    Spinner group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_expense);

        date = (TextView)findViewById(R.id.dateTxt);
        save = (Button)findViewById(R.id.saveBtn);
        cancel = (Button)findViewById(R.id.cancelBtn);
        amountSpent = (EditText) findViewById(R.id.amountSpentTxt);
        group = (Spinner)findViewById(R.id.categorySpinner);






        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             value = extras.getString("key");
            //The key argument here must match that used in the other activity
            date.setText(value);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DayExpenseActivity.this, ExpenseActivity.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupchoice = group.getSelectedItem().toString();
                amount = Double.parseDouble(amountSpent.getText().toString());
                DatabaseReference dbref = currentUserDB.child("expenseList").child("expense").push();  ;

                dbref.child("value").setValue(amount);
                dbref.child("date").setValue(value);
                dbref.child("category").setValue(groupchoice);

                Toast.makeText(getApplicationContext(), "Expense Saved", Toast.LENGTH_LONG).show();
                startActivity(new Intent(DayExpenseActivity.this, ExpenseActivity.class));
            }
        });


    }


}
