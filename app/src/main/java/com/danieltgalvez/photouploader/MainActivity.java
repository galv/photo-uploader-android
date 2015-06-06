package com.danieltgalvez.photouploader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    File imageFile;
    PictureHolder pictureHolder;

    private EditText experimentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button photoButton = (Button) findViewById(R.id.camera_button);
        experimentText = (EditText) findViewById(R.id.experiment_text);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date currentDate = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(currentDate);
                String experimentName = experimentText.getText().toString();
                if (experimentName == "") {
                    experimentName = "default_experiment";
                }

                imageFile = new File(Environment.getExternalStorageDirectory(),
                        experimentName + "_" + timeStamp + ".jpg");
                Uri fileUri = Uri.fromFile(imageFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                pictureHolder = new PictureHolder(imageFile, experimentName, currentDate);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        Button scheduleButton = (Button) findViewById(R.id.schedule_button);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PhotoSchedulerActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // Why don't we call the superclass's method?

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "image obtained!", Toast.LENGTH_LONG).show();
                // Implicitly, the imageFile on pictureHolder now points to an actual image.
                // Perform asynchronous upload.
                Intent intent = new Intent(this, UploadService.class);
                intent.putExtra(PictureHolder.PICTURE_HOLDER, pictureHolder);
                Log.i("IntentService","Calling intent service");
                startService(intent);
            }
        } else {
            throw new Error("Incorrect request code.");
        }

    }
}