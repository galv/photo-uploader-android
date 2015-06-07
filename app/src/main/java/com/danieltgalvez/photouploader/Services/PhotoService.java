package com.danieltgalvez.photouploader.Services;

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PhotoService extends IntentService {
    public static final String ACTION_TIME_LAPSE = "com.danieltgalvez.photouploader.action.TIME_LAPSE";

    public static final String EXTRA_EXPERIMENT_SCHEDULE = "com.danieltgalvez.photouploader.extra.EXPERIMENT_SCHEDULE";

    public PhotoService() {
        super("PhotoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_TIME_LAPSE.equals(action)) {

            }
        }
    }
}
