package com.danieltgalvez.photouploader.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Date;

/**
 * Created by danielgalvez on 5/24/15.
 */
public class PictureHolder implements Parcelable {

    public static final String PICTURE_HOLDER = "PICTURE_HOLDER";

    private File imageFile;
    private String experimentSeries;
    private Date date;

    public PictureHolder(File imageFile, String experimentSeries, Date date) {
        this.imageFile = imageFile;
        this.experimentSeries = experimentSeries;
        this.date = date;
    }

    public File getImageFile() {
        return imageFile;
    }

    public long getDateAsLong() {
        return date.getTime();
    }

    public String getExperiment() {
        return experimentSeries;
    }
    //region Parcelable

    private PictureHolder(Parcel in) {
        String filePath = in.readString();
        imageFile = new File(filePath);
        experimentSeries = in.readString();
        date = new Date(in.readLong());
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<PictureHolder> CREATOR =
            new Parcelable.Creator<PictureHolder>() {
                public PictureHolder createFromParcel(Parcel in) {
                    return new PictureHolder(in);
                }

                public PictureHolder[] newArray(int size) {
                    return new PictureHolder[size];
                }
            };

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(imageFile.getPath());
        out.writeString(experimentSeries);
        out.writeLong(date.getTime());
    }

    //endregion
}
