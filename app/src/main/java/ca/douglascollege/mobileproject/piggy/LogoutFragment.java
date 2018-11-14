package ca.douglascollege.mobileproject.piggy;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutFragment extends Fragment {


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener listener;
    private ProgressBar progressBar;
    private TextView txtSignIn, txtSignOut;
    private Button btnSignOut;

    public LogoutFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_logout, container, false);

       // Sign out button clicked, sign out
        btnSignOut = view.findViewById(R.id.btnSignOut);
        txtSignOut = view.findViewById(R.id.txtSignOut);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Add the buttons
                builder.setTitle("Log out");
                builder.setMessage("Are you sure to log out?")
                        .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Log.d("clicekd","Clicekd");
                        progressBar = view.findViewById(R.id.progressBarSignout);
                        progressBar.setVisibility(View.VISIBLE);
                        txtSignOut.setVisibility(View.VISIBLE);
                        firebaseAuth.signOut();
                        startActivity(new Intent(getActivity(), WelcomeActivity.class));
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                    }
                });

                builder.show();

            }
        });
        return view;
    }
}
