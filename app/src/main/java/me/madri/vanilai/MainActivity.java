package me.madri.vanilai;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static me.madri.vanilai.R.string.current_humidity;
import static me.madri.vanilai.R.string.current_precipitation;
import static me.madri.vanilai.R.string.current_temperature;
import static me.madri.vanilai.R.string.current_time_label;
import static me.madri.vanilai.R.string.fahrenheit_label;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentVanilai mCurrentVanilai;
    private double mLatitude;
    private double mLongitude;
    private Geocoder mGeoCoder;
    private LocationManager mLocationManager;
    private VanilaiLocationService mVanilaiLocationService;
    private Location mLocation;
    private CurrentEarthquake mCurrentEarthquake;
    private int mOnSuccessToggle = 0;
    private int mOnFailureToggle = 0;

    @Bind(R.id.timeLabel)
    TextView mTimeLabel;
    @Bind(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @Bind(R.id.humidityValue)
    TextView mHumidityValue;
    @Bind(R.id.precipValue)
    TextView mPrecipValue;
    @Bind(R.id.summaryLabel)
    TextView mSummaryLabel;
    @Bind(R.id.iconImageView)
    ImageView mIconImageView;
    @Bind(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.locationLabel)
    TextView mLocationLabel;
    @Bind(R.id.temperatureSwitch)
    Switch mTemperatureSwitch;
    @Bind(R.id.placeLabel)
    TextView mPlaceLabel;
    @Bind(R.id.earthquakeSummaryLabel)
    TextView mEarthquakeSummaryLabel;
    @Bind(R.id.magnitudeLabel)
    TextView mMagnitudeLabel;
    @Bind(R.id.magnitudeValue)
    TextView mMagnitudeValue;
    @Bind(R.id.placeValue)
    TextView mPlaceValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mVanilaiLocationService = new VanilaiLocationService();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mGeoCoder = new Geocoder(this, Locale.getDefault());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission denied");
            }
        }
        mLocation = mVanilaiLocationService.getLocation(LocationManager.GPS_PROVIDER,
                mLocationManager, this);
        if(mLocation==null) {
            mLatitude = 13.018410;
            mLongitude = 80.223068;
            alertUserAboutLocationError();
        } else {
            mLatitude = mLocation.getLatitude();
            mLongitude = mLocation.getLongitude();
        }
        Log.v(TAG, "Latitude: " + mLatitude + ", longitude: " + mLongitude);
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation = mVanilaiLocationService.getLocation(LocationManager.GPS_PROVIDER,
                        mLocationManager, getApplicationContext());
                if (mLocation == null) {
                    alertUserAboutLocationError();
                } else {
                    mLatitude = mLocation.getLatitude();
                    mLongitude = mLocation.getLongitude();
                    getForecast(mLatitude, mLongitude);
                }
            }
        });
        getForecast(mLatitude, mLongitude);
        mTemperatureSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnTemperatureSwitch();
            }
        });
        Log.d(TAG, "Main UI code is running!");
    }

    private void getForecast(final double latitude, final double longitude) {
        mOnSuccessToggle=0;
        mOnFailureToggle=0;
        if(isNetworkAvailable()) {
            toggleRefresh();
            final OkHttpClient client = new OkHttpClient();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String apiKey = "e5aa93a28a1be7d23a7482e2b8d5c60a";
                    String forecastUrl = "https://api.forecast.io/forecast/"+apiKey+"/"+latitude+","+longitude;
                    Request request = new Request.Builder()
                            .url(forecastUrl)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOnFailureToggle++;
                                }
                            });
                            alertUserAboutError();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOnSuccessToggle++;
                                }
                            });
                            try {
                                String jsonData = response.body().string();
                                Log.v(TAG, jsonData);
                                if (response.isSuccessful()) {
                                    mCurrentVanilai = getCurrentDetails(jsonData,
                                            getCityFromLocation(latitude, longitude,
                                                    mGeoCoder));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateDisplay();
                                        }
                                    });
                                } else {
                                    alertUserAboutError();
                                }
                            } catch (IOException | JSONException e) {
                                Log.e(TAG, "Forecast Exception caught: " + e);
                            }
                        }
                    });
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date curDate = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(curDate);
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    Date priorDate= cal.getTime();
                    String earthquakeUrl = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&" +
                            "starttime="+dateFormat.format(priorDate)+"&endtime="+dateFormat.format(curDate)
                            +"&latitude="+latitude+"&longitude="+longitude+
                            "&maxradius=25&callback&eventtype=earthquake&minmagnitude=3";
                    Log.v(TAG, "Earthquake Url: " + earthquakeUrl);
                    Request earthquakeRequest = new Request.Builder()
                            .url(earthquakeUrl)
                            .build();
                    Call earthquakeCall = client.newCall(earthquakeRequest);
                    earthquakeCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOnFailureToggle++;
                                }
                            });
                            alertUserAboutError();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOnSuccessToggle++;
                                }
                            });
                            try {
                                Log.v(TAG, "Getting earthquake data");
                                String jsonEarthquakeData = response.body().string();
                                Log.v(TAG, jsonEarthquakeData);
                                if (response.isSuccessful()) {
                                    mCurrentEarthquake = getEarthquakeDetails(jsonEarthquakeData);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateEarthquakeDisplay();
                                        }
                                    });
                                } else {
                                    alertUserAboutError();
                                }
                            } catch (IOException | JSONException e) {
                                Log.e(TAG, "Earthquake Exception caught: " + e);
                            }
                        }
                    });
                }
            });
            if((mOnSuccessToggle%2==0)||(mOnFailureToggle>0)) {
                toggleRefresh();
            }
        } else {
            Toast.makeText(this, getString(R.string.error_network_message),
                    Toast.LENGTH_LONG).show();
            alertUserAboutNetworkError();
        }
    }

    private void updateEarthquakeDisplay() {
        long earthquakeTime = mCurrentEarthquake.getTimeOfEarthquake();
        long lastEarthquakeTime = mCurrentEarthquake.getLastUpdated();
        long currentTime = new Date().getTime();
        long diffInMinsEarthquake = Math.abs(currentTime - earthquakeTime) / 60000;
        long diffInMinsUpdate = Math.abs(currentTime - lastEarthquakeTime) / 60000;
        Log.v(TAG, "Time from Earthquake: "+diffInMinsEarthquake);
        Log.v(TAG, "Time from Earthquake Update: "+diffInMinsUpdate);
        Resources res = getResources();
        if((diffInMinsEarthquake<=120)||(diffInMinsUpdate<=120)) {
            if (mCurrentEarthquake.getCount() > 0) {
                mMagnitudeValue.setText(res.getString(R.string.current_earthquake_magnitude,
                        Double.toString(mCurrentEarthquake.getMagnitude())));
                String alertColor = mCurrentEarthquake.getAlertType();
                if (alertColor.equalsIgnoreCase("green")) {
                    mMagnitudeValue.setTextColor(Color.GREEN);
                } else if (alertColor.equalsIgnoreCase("yellow")) {
                    mMagnitudeValue.setTextColor(Color.YELLOW);
                } else if (alertColor.equalsIgnoreCase("orange")) {
                    mMagnitudeValue.setTextColor(Color.parseColor("#FFFF8000"));
                } else if (alertColor.equalsIgnoreCase("red")) {
                    mMagnitudeValue.setTextColor(Color.RED);
                }
                mPlaceValue.setText(mCurrentEarthquake.getPlace());
                mEarthquakeSummaryLabel.setText(mCurrentEarthquake.getSummary());
            }
        }else {
            mMagnitudeLabel.setText("");
            mPlaceLabel.setText("");
            mEarthquakeSummaryLabel.setText(R.string.default_earthquake_text);
        }
    }

    private CurrentEarthquake getEarthquakeDetails(String jsonEarthquakeData) throws JSONException {
        mCurrentEarthquake = new CurrentEarthquake();
        JSONObject earthquakeJson = new JSONObject(jsonEarthquakeData);
        JSONObject metadata = earthquakeJson.getJSONObject("metadata");
        int count = metadata.getInt("count");
        mCurrentEarthquake.setCount(count);
        if(count>0) {
            JSONArray features = earthquakeJson.getJSONArray("features");
            for(int i=0;i<features.length();i++) {
                JSONObject feature = features.getJSONObject(i).getJSONObject("properties");
                mCurrentEarthquake.setMagnitude(feature.getDouble("mag"));
                mCurrentEarthquake.setPlace(feature.getString("place"));
                mCurrentEarthquake.setTimeOfEarthquake(feature.getLong("time"));
                mCurrentEarthquake.setLastUpdated(feature.getLong("updated"));
                String alert="";
                if(feature.getString("alert")!=null) {
                    alert = feature.getString("alert");
                }
                mCurrentEarthquake.setAlertType(alert);
            }
        }
        mCurrentEarthquake.setSummary("Found "+count+" Earthquakes in Area!");
        return mCurrentEarthquake;
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        Resources res = getResources();
        mTemperatureLabel.setText(res.getString(current_temperature, mCurrentVanilai.getTemperature()));
        mTimeLabel.setText(res.getString(current_time_label, mCurrentVanilai.getFormattedTime()));
        mHumidityValue.setText(res.getString(current_humidity, mCurrentVanilai.getHumidity()));
        mPrecipValue.setText(res.getString(current_precipitation, mCurrentVanilai.getPrecipChance()));
        mSummaryLabel.setText(mCurrentVanilai.getSummary());
        mLocationLabel.setText(mCurrentVanilai.getCity());
        Drawable drawable = ContextCompat.getDrawable(this, mCurrentVanilai.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mTemperatureSwitch.setChecked(false);
        mTemperatureSwitch.setText(res.getString(fahrenheit_label));
    }

    private CurrentVanilai getCurrentDetails(String jsonData, String city) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");
        CurrentVanilai vanilai = new CurrentVanilai();
        vanilai.setTime(currently.getLong("time"));
        vanilai.setHumidity(currently.getDouble("humidity"));
        vanilai.setPrecipChance(currently.getDouble("precipProbability"));
        vanilai.setIcon(currently.getString("icon"));
        vanilai.setSummary(currently.getString("summary"));
        vanilai.setTemperature(currently.getDouble("temperature"));
        vanilai.setTimeZone(timezone);
        vanilai.setCity(city);
        Log.i(TAG, "From JSON, time: " + vanilai.getFormattedTime());
        return vanilai;
    }

    private void alertUserAboutNetworkError() {
        NetworkAlertDialogFragment dialog = new NetworkAlertDialogFragment();
        dialog.show(getFragmentManager(), "Network Alert Dialog");
    }

    private void alertUserAboutLocationError() {
        LocationDialogFragment dialog = new LocationDialogFragment();
        dialog.show(getFragmentManager(), "Location Alert Dialog");
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "Error Dialog");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if((networkInfo !=null) && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public String getCityFromLocation(double latitude, double longitude,Geocoder geocoder) {
        Log.v(TAG, "Getting City");
        try {
            if(Geocoder.isPresent()) {
                List<Address> addressList = geocoder.getFromLocation(
                        latitude, longitude, 1);
                Log.v(TAG, "Address List: " + addressList);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    Log.v(TAG, "Address: \"" + address.getLocality() + "\"");
                    return address.getLocality();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);
        }
        Log.v(TAG, "Got City as null");
        return null;
    }

    public void setOnTemperatureSwitch() {
        Resources resources = getResources();
        Log.v(TAG, mTemperatureSwitch.getText().toString());
        if(mTemperatureSwitch.getText().toString().equalsIgnoreCase("°F")) {
            mTemperatureSwitch.setChecked(true);
            String temperatureText = mTemperatureLabel.getText().toString();
            double currentTemperature = Double.parseDouble(temperatureText);
            double newTemperature = ((currentTemperature - 32) * 5)/9;
            mCurrentVanilai.setTemperature(newTemperature);
            mTemperatureLabel.setText(resources.getString(R.string.current_temperature,
                    mCurrentVanilai.getTemperature()));
            mTemperatureSwitch.setText("°C");
            Log.v(TAG, "Current temp: "+mCurrentVanilai.getTemperature()+"°C");
        } else if(mTemperatureSwitch.getText().toString().equalsIgnoreCase("°C")) {
            mTemperatureSwitch.setChecked(false);
            String temperatureText = mTemperatureLabel.getText().toString();
            double currentTemperature = Double.parseDouble(temperatureText);
            double newTemperature = ((currentTemperature * 9) / 5) + 32;
            mCurrentVanilai.setTemperature(newTemperature);
            mTemperatureLabel.setText(resources.getString(R.string.current_temperature,
                    mCurrentVanilai.getTemperature()));
            mTemperatureSwitch.setText("°F");
            Log.v(TAG, "Current temp: " + mCurrentVanilai.getTemperature() + "°F");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        Log.v(TAG, "Last update time: " + DateFormat.getTimeInstance().format(new Date()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
