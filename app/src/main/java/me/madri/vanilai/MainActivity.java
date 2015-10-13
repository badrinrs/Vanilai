package me.madri.vanilai;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static me.madri.vanilai.R.string.current_humidity;
import static me.madri.vanilai.R.string.current_precipitation;
import static me.madri.vanilai.R.string.current_temperature;
import static me.madri.vanilai.R.string.current_time_label;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentVanilai mCurrentVanilai;
    private double mLatitude;
    private double mLongitude;
    private Geocoder mGeoCoder;
    private LocationManager mLocationManager;
    private VanilaiLocationService mVanilaiLocationService;
    private Location mLocation;
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
        Log.d(TAG, "Main UI code is running!");
    }

    private void getForecast(final double latitude, final double longitude) {
        String apiKey = "e5aa93a28a1be7d23a7482e2b8d5c60a";
        String forecastUrl = "https://api.forecast.io/forecast/"+apiKey+"/"+latitude+","+longitude;
        if(isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
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
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
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
                        Log.e(TAG, "Exception caught: " + e);
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.error_network_message),
                    Toast.LENGTH_LONG).show();
            alertUserAboutNetworkError();
        }
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

    public static String getCityFromLocation(double latitude, double longitude,Geocoder geocoder) {
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
