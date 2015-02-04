package be.electrodoctor.electroman;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import be.electrodoctor.electroman.dialogs.ProcessDialog;


public class MainActivity extends ActionBarActivity implements ProcessDialog.OnJobProcessed {

    private final String FRAGMENT = "ListViewFragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainListViewFragment(), FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onJobProcessed() {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT);
        OnUpdate updateable = (OnUpdate)fragment;
        updateable.refresh();
    }
}
