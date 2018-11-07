package ca.douglascollege.mobileproject.piggy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // fade effect fot enter and exit
        Fade fade = new Fade();
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }
}
