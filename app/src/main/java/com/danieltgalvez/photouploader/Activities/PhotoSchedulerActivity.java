package com.danieltgalvez.photouploader.Activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.danieltgalvez.photouploader.Model.ExperimentSchedule;
import com.danieltgalvez.photouploader.R;

import java.util.Calendar;


public class PhotoSchedulerActivity extends FragmentActivity {

    private ExperimentSchedule schedule;

    private Button startDateTextView;
    private Button startTimeTextView;
    private EditText periodTextView;
    private Button scheduleButton;
    private EditText experimentEditText;

    public static final String EXPERIMENT_SERIES = "com.danieltgalvez.EXTRA_EXPERIMENT_SERIES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            schedule = new ExperimentSchedule();
        } else {
            schedule = savedInstanceState.getParcelable(ExperimentSchedule.EXPERIMENT_SCHEDULE);
        }

        setContentView(R.layout.activity_photo_scheduler);

        startDateTextView = (Button) findViewById(R.id.start_date_text);
        startTimeTextView = (Button) findViewById(R.id.start_time_text);
        periodTextView = (EditText) findViewById(R.id.period_text);
        scheduleButton = (Button) findViewById(R.id.schedule_button);
        experimentEditText = (EditText) findViewById(R.id.experiment_text);
        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        periodTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
                if (!hasFocus) {
                    EditText editText = (EditText) v;
                    String value = editText.getText().toString();
                    if (value.matches("[0-9]*\\.?[0-9]*")) {
                        double seconds = Double.parseDouble(value);
                        long milliSeconds = (long) seconds * 1000L;
                        schedule.setTimePeriodMillis(milliSeconds);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Must enter a valid number, like 12.0", Toast.LENGTH_LONG);
                        if (schedule.timeIsReady()) {
                            double timeMillis = (double) schedule.getTimePeriodMillis();
                            double time = timeMillis / 1000.0;
                            String timeStr = Double.toString(time);
                            if (timeStr.length() == 1) {
                                timeStr = "0" + timeStr;
                            }
                            editText.setText(timeStr);
                        } else {
                            editText.setText("");
                        }
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (schedule.isReady()) {

                    String experimentSeries = experimentEditText.getText().toString();
                    if (experimentSeries.equals("")) {
                        experimentSeries = "unnamed_experiment";
                    }

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    int alarmType = AlarmManager.RTC_WAKEUP;
                    long period = schedule.getTimePeriodMillis();
                    Intent intent = new Intent(PhotoSchedulerActivity.this, AutomaticPhotoActivity.class);
                    intent.putExtra(EXPERIMENT_SERIES, experimentSeries);
                    PendingIntent alarmIntent = PendingIntent.getActivity(PhotoSchedulerActivity.this, 0, intent, 0);
                    alarmManager.setInexactRepeating(alarmType, schedule.getStartDayMillis(), period, alarmIntent);
                    Log.i("PhotoSchedulerActivity", "Camera activity scheduled.");
                } else {
                    Toast.makeText(getApplicationContext(), "Must enter all parameters",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ExperimentSchedule.EXPERIMENT_SCHEDULE, schedule);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_scheduler, menu);
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

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            PhotoSchedulerActivity activity = ((PhotoSchedulerActivity) getActivity());
            activity.schedule.setStartTime(hourOfDay, minute);
            activity.startTimeTextView.setText(Integer.toString(hourOfDay) + ":" +
                    Integer.toString(minute));
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int monthOfYear = cal.get(Calendar.MONTH);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            // Set to current time.
            return new DatePickerDialog(getActivity(), this, year, monthOfYear, dayOfMonth);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            PhotoSchedulerActivity activity = ((PhotoSchedulerActivity) getActivity());
            activity.schedule.setStartDay(year, month, day);
            activity.startDateTextView.setText(Integer.toString(month) + "-" +
                    Integer.toString(day) + "-" + Integer.toString(year));
        }
    }
}
