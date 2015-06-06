package com.danieltgalvez.photouploader;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class ExperimentSchedule implements Parcelable {

    public static String EXPERIMENT_SCHEDULE = "EXPERIMENT_SCHEDULE";

    private Calendar startDate;
    private boolean timeReady;
    private boolean dayReady;
    private long timePeriodMillis; // in milliseconds
    private boolean periodReady;

    int year;
    int month;
    int day;
    int hour;
    int minute;

    public ExperimentSchedule() {
        startDate = Calendar.getInstance();
        startDate.setTimeInMillis(0L);
        timeReady = false;
        dayReady = false;
        timePeriodMillis = 0;
        periodReady = false;
    }

    public void setStartDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        dayReady = true;
    }

    public void setStartTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        timeReady = true;
    }

    public void setTimePeriodMillis(long timePeriodMillis) {
        this.timePeriodMillis = timePeriodMillis;
        periodReady = true;
    }

    public long getTimePeriodMillis() {
        return timePeriodMillis;
    }

    public boolean timeIsReady() {
        return timeReady;
    }

    public boolean isReady() {
        return timeReady && dayReady && periodReady;
    }

    //region Parcelable

    private ExperimentSchedule(Parcel in) {
        long calendarTimeMillis = in.readLong();
        startDate = Calendar.getInstance();
        startDate.setTimeInMillis(calendarTimeMillis);
        timeReady = in.readByte() != 0; // Did they think when they made Android???
        dayReady = in.readByte() != 0;
        timePeriodMillis = in.readLong();
        periodReady = in.readByte() != 0;
    }

    public int describeContents() { return 0;}

    public static final Parcelable.Creator<ExperimentSchedule> CREATOR =
            new Parcelable.Creator<ExperimentSchedule>() {
                public ExperimentSchedule createFromParcel(Parcel in) { return new ExperimentSchedule(in); }

                public ExperimentSchedule[] newArray(int size) { return new ExperimentSchedule[size]; }
            };

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(startDate.getTimeInMillis());
        out.writeByte((byte) (timeReady ? 1 : 0));
        out.writeByte((byte) (dayReady ? 1 : 0));
        out.writeLong(timePeriodMillis);
        out.writeByte((byte) (periodReady ? 1 : 0));
    }

    //endregion
}
