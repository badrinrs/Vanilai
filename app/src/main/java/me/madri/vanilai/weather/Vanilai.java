package me.madri.vanilai.weather;

import me.madri.vanilai.R;

/**
 * Data Model to get Weather data.
 */
public class Vanilai {
    private CurrentVanilai mCurrentVanilai;
    private HourlyVanilai[] mHourlyVanilai;
    private DailyVanilai[] mDailyVanilai;
    private static String mCity;

    public CurrentVanilai getCurrentVanilai() {
        return mCurrentVanilai;
    }

    public void setCurrentVanilai(CurrentVanilai currentVanilai) {
        mCurrentVanilai = currentVanilai;
    }

    public static void setCity(String city) {
        mCity = city;
    }

    public static String getCity() {
        return mCity;
    }

    public HourlyVanilai[] getHourlyVanilai() {
        return mHourlyVanilai;
    }

    public void setHourlyVanilai(HourlyVanilai[] hourlyVanilai) {
        mHourlyVanilai = hourlyVanilai;
    }

    public DailyVanilai[] getDailyVanilai() {
        return mDailyVanilai;
    }

    public void setDailyVanilai(DailyVanilai[] dailyVanilai) {
        mDailyVanilai = dailyVanilai;
    }

    public static int getIconId(String iconString) {
        int iconId = R.drawable.clear_day;
        if(iconString.equalsIgnoreCase("clear-day")) {
            iconId = R.drawable.clear_day;
        } else if(iconString.equalsIgnoreCase("clear-night")) {
            iconId = R.drawable.clear_night;
        } else if(iconString.equalsIgnoreCase("rain")) {
            iconId = R.drawable.rain;
        } else if(iconString.equalsIgnoreCase("snow")) {
            iconId = R.drawable.snow;
        } else if(iconString.equalsIgnoreCase("sleet")) {
            iconId = R.drawable.sleet;
        } else if(iconString.equalsIgnoreCase("wind")) {
            iconId = R.drawable.wind;
        } else if(iconString.equalsIgnoreCase("fog")) {
            iconId = R.drawable.fog;
        } else if(iconString.equalsIgnoreCase("cloudy")) {
            iconId = R.drawable.cloudy;
        } else if(iconString.equalsIgnoreCase("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        } else if(iconString.equalsIgnoreCase("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }
}
