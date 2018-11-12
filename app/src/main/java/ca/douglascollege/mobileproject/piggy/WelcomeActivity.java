package ca.douglascollege.mobileproject.piggy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passwordText;
    private Button signInBtn;
    private Button registerBtn;
    private ImageView piggyIcon;

    // Firebase is for sign up and registration for authentication
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        piggyIcon = findViewById(R.id.piggyImg);

        // Sign Button clicked, login user method
        signInBtn = findViewById(R.id.btnSignIn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Register button clicked, go to RegisterActivitiy
        registerBtn = findViewById(R.id.btnRegister);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
                Intent registerIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        WelcomeActivity.this, piggyIcon, ViewCompat.getTransitionName(piggyIcon)
                );
                startActivity(registerIntent, options.toBundle());
            }
        });

        // fade transition effect fot enter and exit
        Fade fade = new Fade();
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }

    // Check login user
    private void loginUser() {

        emailText = findViewById(R.id.emailTxt);
        passwordText = findViewById(R.id.passwordTxt);

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        Log.d("sign", email + ", " + password);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Sign in success, update UI with the signed-in user's information
                        if (task.isSuccessful()) {
//                            Log.d("success_signIn", "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            // Start Dashboard Activitiy with animation.
                            // Piggy Icon is a shared transition item.
                            Intent dashboardIntent = new Intent(WelcomeActivity.this, DashboardActivity.class);
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    WelcomeActivity.this, piggyIcon, ViewCompat.getTransitionName(piggyIcon)
                            );
                            startActivity(dashboardIntent, options.toBundle());
//                            startActivity(new Intent(WelcomeActivity.this, DashboardActivity.class));
//                            Toast.makeText(WelcomeActivity.this, "Sign In successfully.",
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a fail message to the user.
                            Log.w("failure_signIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Please try again or Register.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
