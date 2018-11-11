package ca.douglascollege.mobileproject.piggy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout frameLayout;
    private DashboardFragment dashboardFragment;
    private LogoutFragment logoutFragment;
    private ReportFragment reportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // fade transition effect fot enter and exit
        Fade fade = new Fade();
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        final ImageView piggyIcon = findViewById(R.id.piggyImg);

        frameLayout = findViewById(R.id.dashboard_frame);
        bottomNav = findViewById(R.id.bottom_nav);

        dashboardFragment = new DashboardFragment();
        logoutFragment = new LogoutFragment();
        reportFragment = new ReportFragment();

        //setting default fragment layout, which is Dashboard
        setFrame(dashboardFragment);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home: {
                        setFrame(dashboardFragment);
                        return true;
                    }
                    case R.id.report: {
                        setFrame(reportFragment);
                        return true;
                    }
                    case R.id.logout: {
                        setFrame(logoutFragment);
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
    }

    private void setFrame(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_frame, fragment);
        fragmentTransaction.commit();
    }
}
