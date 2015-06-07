package com.danieltgalvez.photouploader.Activities;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.danieltgalvez.photouploader.R;

import java.io.IOException;

public class AutomaticPhotoActivity extends ActionBarActivity implements SurfaceHolder.Callback {

    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_photo);

        //camera = Camera.open();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    //region SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            //Maintain backward compatibility with 2.3.6 phone we may be using.
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            holder.setFixedSize(1, 1);
            camera.takePicture(null, null, jpegCallback);
            AutomaticPhotoActivity.this.finish();

            // OpenCV magic goes here!
        } catch (IOException e) {
            Log.d("AutomaticPhotoActivity", "IO Exception", e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //endregion

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i("AutomaticPhotoActivity", "Photo taken automatically!");
            // save stuff!
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_automatic_photo, menu);
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
}
