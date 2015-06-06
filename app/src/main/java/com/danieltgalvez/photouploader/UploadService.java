package com.danieltgalvez.photouploader;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class UploadService extends IntentService {

    public static final String uploadLocation = "http://192.168.1.90:3000/photos/create";

    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("IntentService", "Successfully enter intent service.");
        PictureHolder pictureHolder = intent.getParcelableExtra(PictureHolder.PICTURE_HOLDER);
        String url = uploadLocation + "?time=" + pictureHolder.getDateAsLong() + "&experiment="
                + pictureHolder.getExperiment();
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            HttpEntity entity = new FileEntity(pictureHolder.getImageFile(), "image/jpeg");
            /*
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //FileBody fileBody = new FileBody(pictureHolder.getImageFile(), "image/jpeg");
            FileBody fileBody = new FileBody(pictureHolder.getImageFile());
            builder.addPart("image", fileBody);
            HttpEntity entity = builder.build();
            */
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            // TODO: Check that server successfully receives image. Code 200.
            // How to communicate this back to the activity? No idea.
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e("UploadService", "Server not returning code 200 upon upload.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
