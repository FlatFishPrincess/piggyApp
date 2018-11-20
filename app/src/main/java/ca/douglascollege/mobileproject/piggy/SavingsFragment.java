package ca.douglascollege.mobileproject.piggy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavingsFragment extends Fragment {

    private RecyclerView recyclerView;
    // adpater provides array as many as we need to create list view, manage performance
    private SavingRecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    private Button addBtn;
    private EditText eventTxt;
    private EditText savingAmtTxt;
    private ArrayList<SavingRecyclerView> savingsList;


    public SavingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        createSavingList();
        buildRecyclerView();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_savings, container, false);

        addBtn = view.findViewById(R.id.btnAdd);
        eventTxt = view.findViewById(R.id.txtEvent);
        savingAmtTxt = view.findViewById(R.id.txtSavingAmt);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Add button clicked
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // position for list
                int position = 0;
                // get event name
                String event = eventTxt.getText().toString();
                double savingAmt = Double.parseDouble(savingAmtTxt.getText().toString());

                // If user did not enter name or amount, get toast message
                // If user input correctly, call insertItem method
                if(event == "" || savingAmtTxt.getText().toString() == ""){
                    Toast.makeText(getActivity(), "Please Enter name and amount", Toast.LENGTH_LONG).show();
                } else {
                    insertItem(0, event, savingAmt);
                }
            }
        });

        return view;
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

        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(getContext());
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
