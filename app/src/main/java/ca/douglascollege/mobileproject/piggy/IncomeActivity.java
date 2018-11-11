package ca.douglascollege.mobileproject.piggy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class IncomeActivity extends AppCompatActivity {

    //TODO alert dialog design need to change! not pretty

    // user's income input value
    private double income = 0.0;
    Button incomeBtn;
    TextView resultTxt;
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$0,000.###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        incomeBtn = findViewById(R.id.btnIncome);
        resultTxt = findViewById(R.id.txtResult);

        // Income button clicked, pop up (here, alert dialog) will be displayed
        incomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(IncomeActivity.this);
                builder.setTitle("Enter Your Income");

                // Set up the input
                final EditText user_input = new EditText(IncomeActivity.this);
                // Specify the type of input expected; input type is number
                user_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_NUMBER);
                builder.setView(user_input);

                // Ok button clicked
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        income = Double.parseDouble(user_input.getText().toString());
                        Toast.makeText(IncomeActivity.this, "input" + income, Toast.LENGTH_LONG).show();
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
    }
}
