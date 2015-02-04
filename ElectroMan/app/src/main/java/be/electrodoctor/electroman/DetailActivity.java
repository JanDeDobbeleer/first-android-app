package be.electrodoctor.electroman;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
        final RepairJob job = dbHelper.getRepairJob(Long.parseLong(message));
        assignText(R.id.Content_Name, job.getClient().getName());
        assignText(R.id.Content_Description, job.getDescription());
        assignText(R.id.Content_Street, job.getClient().getAddress().getStreet());
        assignText(R.id.Content_Number, Integer.toString(job.getClient().getAddress().getNumber()));
        assignText(R.id.Content_PostalCode, Integer.toString(job.getClient().getAddress().getPostalCode()));
        assignText(R.id.Content_City, job.getClient().getAddress().getCity());
        assignText(R.id.Content_Comment, job.getComment());
        ImageButton imagebtn = (ImageButton)findViewById(R.id.google_maps);
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + job.getClient().getAddress().getNumber() + " "  + job.getClient().getAddress().getStreet() + ", " + job.getClient().getAddress().getPostalCode() + " " + job.getClient().getAddress().getCity());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        if(job.isProcessed()) {
            LinearLayout commentBlock = (LinearLayout)findViewById(R.id.Detail_CommentBlock);
            commentBlock.setVisibility(View.VISIBLE);
        }
    }

    private void assignText(int id, String text){
        TextView textView = (TextView)findViewById(id);
        if(textView == null)
            return;
        textView.setText(text);
    }
}
