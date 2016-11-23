package cs646.assignment5;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class GetUserPotHoleList extends AppCompatActivity {

    private static final String TAG = "GetPotHoleList";
    protected static boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pothole_list);

        determinePaneLayout();

        if(isTwoPane) {
            FragmentTransaction startFragment = getSupportFragmentManager().beginTransaction();
            startFragment.add(R.id.fragment_for_tablet, new GetUserPotHoleListFragment());
            startFragment.commit();
        }
        else {
            FragmentTransaction startFragment = getSupportFragmentManager().beginTransaction();
            startFragment.replace(R.id.fragment_for_smaller_screens, new GetUserPotHoleListFragment());
            startFragment.commit();
        }
    }

    private void determinePaneLayout() {
        if (findViewById(R.id.detailedFragment) != null) {
            isTwoPane = true;
        }
        else {
            isTwoPane = false;
        }
    }
}
