package me.madri.vanilai;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bnara on 10/4/2015.
 */
public class CurrentVanilai {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mCity;
    private String mTimeZone;

    public void setCity(String city) {
        mCity = city;
    }

    public String getCity() {
        return mCity;
    }

    public String getFormattedTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        return dateFormat.format(new Date(getTime()*1000));
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        int iconId = R.drawable.clear_day;
        if(mIcon.equalsIgnoreCase("clear-day")) {
            iconId = R.drawable.clear_day;
        } else if(mIcon.equalsIgnoreCase("clear-night")) {
            iconId = R.drawable.clear_night;
        } else if(mIcon.equalsIgnoreCase("rain")) {
            iconId = R.drawable.rain;
        } else if(mIcon.equalsIgnoreCase("snow")) {
            iconId = R.drawable.snow;
        } else if(mIcon.equalsIgnoreCase("sleet")) {
            iconId = R.drawable.sleet;
        } else if(mIcon.equalsIgnoreCase("wind")) {
            iconId = R.drawable.wind;
        } else if(mIcon.equalsIgnoreCase("fog")) {
            iconId = R.drawable.fog;
        } else if(mIcon.equalsIgnoreCase("cloudy")) {
            iconId = R.drawable.cloudy;
        } else if(mIcon.equalsIgnoreCase("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        } else if(mIcon.equalsIgnoreCase("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperature() {
        return (int)Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance*100;
        return (int) Math.round(precipPercentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }
}
