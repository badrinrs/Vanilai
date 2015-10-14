package me.madri.vanilai;

/**
 * This class is a bean class for Current Earthquake values.
 * Created by bnara on 10/12/2015.
 */
public class CurrentEarthquake {

    public long mTimeOfEarthquake;
    public double mMagnitude;
    public long mLastUpdated;
    public String mPlace;
    public String mAlertType;
    public String mSummary;
    public int mCount;

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public long getTimeOfEarthquake() {
        return mTimeOfEarthquake;
    }

    public void setTimeOfEarthquake(long timeOfEarthquake) {
        mTimeOfEarthquake = timeOfEarthquake;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public void setMagnitude(double magnitude) {
        mMagnitude = magnitude;
    }

    public long getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        mLastUpdated = lastUpdated;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public String getAlertType() {
        return mAlertType;
    }

    public void setAlertType(String alertType) {
        this.mAlertType = alertType;
    }
}
