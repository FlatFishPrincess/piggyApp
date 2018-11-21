package ca.douglascollege.mobileproject.piggy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.ArrayList;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

public class SavingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    // adpater provides array as many as we need to create list view, manage performance
    private SavingRecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private Button addBtn;
    private FabSpeedDial fab;
    private EditText eventTxt;
    private EditText savingAmtTxt;
    private TextView savTxt;
    int position = 0;
    String event,svAmount;
    String key;
    DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$ #,###.00");
    double saved;


    // TODO: customizing alert dialog layout, handle user account
    // this here catches the firebase and the database
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    // this is a database based on the child of the "users" field of the main database
    final DatabaseReference currentUserDB = mDatabase.child(firebaseAuth.getCurrentUser().getUid());

    private ArrayList<SavingRecyclerView> savingsList;
    private ArrayList<String> keyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        addBtn = findViewById(R.id.btnAdd);
        eventTxt = findViewById(R.id.txtEvent);
        savingAmtTxt = findViewById(R.id.txtSavingAmt);

        savTxt = findViewById(R.id.savingsTxt);
        saved = 0;

        currentUserDB.child("savingsSoFar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    savTxt.setText("You have saved: " + CURRENCY_FORMAT.format(0) + " so far");
                }else {
                    savTxt.setText("You have saved: " + CURRENCY_FORMAT.format(dataSnapshot.getValue()) + " so far");
                    saved = Double.parseDouble(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        currentUserDB.child("savings").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    createSavingList();
                    buildRecyclerView();
                }else {
                    createSavingList();
                    buildRecyclerView();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("name").getValue() != null && ds.child("value").getValue() != null) {
                            String event = ds.child("name").getValue(String.class);
                            double value = ds.child("value").getValue(Double.class);
                            String chave = ds.child("savingId").getValue(String.class);
                            String val = String.valueOf(value);
                            try {
                                savingsList.add(new SavingRecyclerView(R.drawable.ic_event_available_black_24dp, event, val));
                                keyList.add(chave);
                            }
                            catch(Exception e){
                                String a = e.getMessage();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // floating icon clicked, add event
        fab = findViewById(R.id.addFabIcon);
        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
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
                               svAmount = savingAmtTxt.getText().toString();
                               double savingAmt = Double.parseDouble(savingAmtTxt.getText().toString());

                               // get the key of child
                               key = currentUserDB.child("savings").push().getKey();
                               final DatabaseReference dbref = currentUserDB.child("savings").child(key);
                               dbref.child("savingId").setValue(key);
                               dbref.child("name").setValue(event);
                               dbref.child("value").setValue(savingAmt);

                               // If user did not enter name or amount, get toast message
                               // If user input correctly, call insertItem method
                               if(event.equals(null) || svAmount.equals(null)){
                                   Toast.makeText(SavingsActivity.this, "Please enter name and amount", Toast.LENGTH_LONG).show();
                               } else {
                                    Toast.makeText(SavingsActivity.this, event + " "+ savingAmt, Toast.LENGTH_LONG).show();
                                   insertItem(0, event, savingAmt, key);
                               }
                               position = position + 1;
                           }
                       });
               eventTxt = expenseView.findViewById(R.id.savingNameTxt);
               savingAmtTxt = expenseView.findViewById(R.id.savingAmtTxt);
               builder.show();
           }
       });
    }

    // This method inserts Item into List
    public void insertItem(int position, String event, double savingAmt, String keyS){
        String amt = "$" + savingAmt;
        savingsList.add(new SavingRecyclerView(R.drawable.ic_event_available_black_24dp, event, amt));
        keyList.add(keyS);
        recyclerAdapter.notifyItemChanged(position);
    }

    public void deleteItem(final int position){

        String name = savingsList.get(position).getText1();

        currentUserDB.child("savings").child(keyList.get(position)).removeValue();

        savingsList.remove(position);
        keyList.remove(position);
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
        keyList = new ArrayList<>();
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
//                changeText(position, "Clicked");
            }

            @Override
            public void onDeleteClick(int position) {
                deleteItem(position);
            }

        });
    }
}