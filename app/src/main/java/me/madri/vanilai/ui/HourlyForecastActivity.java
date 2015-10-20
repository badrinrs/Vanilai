package me.madri.vanilai.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.madri.vanilai.R;
import me.madri.vanilai.adapters.HourAdapter;
import me.madri.vanilai.weather.HourlyVanilai;
import me.madri.vanilai.weather.Vanilai;

public class HourlyForecastActivity extends Activity{

    //private HourlyVanilai[] mHourly;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.locationLabel)
    TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);
        mLocationLabel.setText(Vanilai.getCity());
        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        HourlyVanilai[] hourly = Arrays.copyOf(parcelables, parcelables.length, HourlyVanilai[].class);
        HourAdapter adapter = new HourAdapter(this, hourly);

        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);
    }

}
