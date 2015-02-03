package be.electrodoctor.electroman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import be.electrodoctor.electroman.database.SQLiteHelper;
import be.electrodoctor.electroman.model.RepairJob;


public class DetailActivity extends ActionBarActivity {

    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainListViewFragment.JOB_ID);
        dbHelper = new SQLiteHelper(this);
        assignJob(message);
    }

    private void assignJob(String message) {
        RepairJob job = dbHelper.getRepairJob(Long.parseLong(message));
        assignText(R.id.Content_Name, job.getClient().getName());
        assignText(R.id.Content_Description, job.getDescription());
        assignText(R.id.Content_Street, job.getClient().getAddress().getStreet());
        assignText(R.id.Content_Number, Integer.toString(job.getClient().getAddress().getNumber()));
        assignText(R.id.Content_PostalCode, Integer.toString(job.getClient().getAddress().getPostalCode()));
        assignText(R.id.Content_City, job.getClient().getAddress().getCity());
    }

    private void assignText(int id, String text){
        TextView textView = (TextView)findViewById(id);
        if(textView == null)
            return;
        textView.setText(text);
    }
}
