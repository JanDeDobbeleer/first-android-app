package be.electrodoctor.electroman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import be.electrodoctor.electroman.database.SQLiteHelper;
import be.electrodoctor.electroman.model.RepairJob;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainListViewFragment.JOB_ID);
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        RepairJob job = dbHelper.getRepairJob(Long.parseLong(message));
    }
}
