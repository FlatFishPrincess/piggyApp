package ca.douglascollege.mobileproject.piggy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn ;
    private EditText emailText;
    private EditText passwordText;
    private EditText passwordConfirmText;
    private TextView signInTextView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.btnRegister);
        emailText = findViewById(R.id.emailTxt);
        passwordText = findViewById(R.id.passwordTxt);
        passwordConfirmText = findViewById(R.id.txtPasswordConfirm);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", "clicked");
                registerUser(emailText, passwordText, passwordConfirmText);
            }
        });
    }

    private void registerUser(EditText emailText, EditText passwordText, EditText passwordConfirmText) {

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //get email and password from input and trim them
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String passwordConfirm = passwordConfirmText.getText().toString().trim();
        Log.d("register user!!!!!", email + ",   " + password);


        //if empty,  return to finish the registerUser method
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(passwordConfirm)){
            Toast.makeText(this, "Please enter password confirmation", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if(password == passwordConfirm){
            //create user with firebase auth
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //user successfully registered
                                Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                                AuthResult result = task.getResult();
                                progressBar.setVisibility(View.INVISIBLE);
                            } else{
                                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                                Exception exception = task.getException();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Password does not match with confirmation", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }


    }
}
