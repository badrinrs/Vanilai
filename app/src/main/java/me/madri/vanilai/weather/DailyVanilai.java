package me.madri.vanilai.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Store Daily Forecast
 */
public class DailyVanilai implements Parcelable{
    private long mTime;
    private String mSummary;
    private double mMaxTemperature;
    private String mIcon;
    private String mTimezone;

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getMaxTemperature() {
        return (int) Math.round(mMaxTemperature);
    }

    public void setMaxTemperature(double maxTemperature) {
        mMaxTemperature = maxTemperature;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public int getIconId() {
        return Vanilai.getIconId(mIcon);
    }

    public String getDayOfTheWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimezone));
        Date dateTime = new Date(mTime*1000);
        return formatter.format(dateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mMaxTemperature);
        dest.writeString(mIcon);
        dest.writeString(mTimezone);
    }

    private DailyVanilai(Parcel in) {
        mTime = in.readLong();
        mSummary = in.readString();
        mMaxTemperature = in.readDouble();
        mIcon =  in.readString();
        mTimezone = in.readString();
    }

    public DailyVanilai() {

    }

    public static final Creator<DailyVanilai> CREATOR = new Creator<DailyVanilai>() {
        @Override
        public DailyVanilai createFromParcel(Parcel source) {
            return new DailyVanilai(source);
        }

        @Override
        public DailyVanilai[] newArray(int size) {
            return new DailyVanilai[size];
        }
    };
}
