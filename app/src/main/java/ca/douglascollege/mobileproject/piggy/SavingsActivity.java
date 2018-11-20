package ca.douglascollege.mobileproject.piggy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

public class SavingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    // adpater provides array as many as we need to create list view, manage performance
    private SavingRecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    private Button addBtn;
    private EditText eventTxt;
    private EditText savingAmtTxt;
    private FabSpeedDial fab;

    // value of savings and events;
    double savings;
    String event;

    private ArrayList<SavingRecyclerView> savingsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        // default
        createSavingList();
        buildRecyclerView();


        fab = findViewById(R.id.addFabIcon);
        // How to change background color of fab?
        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView textView, int itemId) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SavingsActivity.this);
                final View expenseView = LayoutInflater.from(SavingsActivity.this).inflate(R.layout.fragment_savings_dialog, null);

                builder.setView(expenseView)
                        .setTitle("Savings")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SavingsActivity.this, "cancel clicked", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            event = eventTxt.getText().toString();
                            savings = Double.parseDouble(savingAmtTxt.getText().toString());
                                Log.d("ok", "e: " + eventTxt);
                                insertItem(0, event, savings);
                            Toast.makeText(SavingsActivity.this, "ok clicked " + event + " , " + savings, Toast.LENGTH_LONG).show();
                            }
                        });
                eventTxt = expenseView.findViewById(R.id.savingNameTxt);
                savingAmtTxt = expenseView.findViewById(R.id.savingAmtTxt);
                builder.show();
            }
        });

//        fab.addOnStateChangeListener(new FabSpeedDial.OnStateChangeListener() {
//            @Override
//            public void onStateChange(boolean open) {
//                Toast.makeText(SavingsActivity.this, "clicked", Toast.LENGTH_LONG).show();
//            }
//        });

        // Add button clicked
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // position for list
//                int position = 0;
//                // get event name
//                String event = eventTxt.getText().toString();
//                double savingAmt = Double.parseDouble(savingAmtTxt.getText().toString());
//
//                // If user did not enter name or amount, get toast message
//                // If user input correctly, call insertItem method
//                if(event == "" || savingAmtTxt.getText().toString() == ""){
//                    Toast.makeText(SavingsActivity.this, "Please Enter name and amount", Toast.LENGTH_LONG).show();
//                } else {
//                    insertItem(0, event, savingAmt);
//                }
//            }
//        });
        // get Saving event
    }

    // This method inserts Item into List
    public void insertItem(int position, String event, double savingAmt){
        String amt = "$" + savingAmt;
        savingsList.add(position, new SavingRecyclerView(R.drawable.ic_event_available_black_24dp, event, amt));
        recyclerAdapter.notifyItemChanged(position);
    }

    public void deleteItem(int position){
        // remove position from list
        savingsList.remove(position);
        // this is for animation
        recyclerAdapter.notifyItemRemoved(position);
    }

    public void changeText(int position, String text){
        savingsList.get(position).changeText1(text);
        recyclerAdapter.notifyItemChanged(position);
    }


    // add the first list item as default
    public void createSavingList(){
        savingsList = new ArrayList<>();
        savingsList.add(new SavingRecyclerView(R.drawable.currency_icon, "Event Name", "Saving Amount"));
    }


    //  You can just ignore this part as this is for adapter and layout!
    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerAdapter = new SavingRecyclerAdapter(savingsList);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new SavingRecyclerAdapter.OnSavingItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeText(position, "Clicked");
            }

            @Override
            public void onDeleteClick(int position) {
                deleteItem(position);
            }

        });
    }
}
