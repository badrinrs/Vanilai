package me.madri.vanilai.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.madri.vanilai.R;
import me.madri.vanilai.weather.HourlyVanilai;

/**
 * The hour data is put into a RecyclerView Adapter
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder>{

    private HourlyVanilai[] mHourlyVanilais;
    private Context mContext;
    public HourAdapter(Context context, HourlyVanilai[] hourlyVanilais) {
        mContext = context;
        mHourlyVanilais = hourlyVanilais;
    }
    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_list_view, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        holder.bindHour(mHourlyVanilais[position]);
    }

    @Override
    public int getItemCount() {
        return mHourlyVanilais.length;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;
        public ImageView mIconImageView;
        public HourViewHolder(View view) {
            super(view);
            mTimeLabel = (TextView) view.findViewById(R.id.timeLabel);
            mTemperatureLabel = (TextView) view.findViewById(R.id.temperatureLabel);
            mSummaryLabel = (TextView) view.findViewById(R.id.summaryLabel);
            mIconImageView = (ImageView) view.findViewById(R.id.iconImageView);
            view.setOnClickListener(this);
        }
        public void bindHour(HourlyVanilai hour) {
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(Integer.toString(hour.getTemperature()));
            mIconImageView.setImageResource(hour.getIconId());
        }

        @Override
        public void onClick(View v) {
            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();
            String message = String.format("At %s it will be %s and %s", time, temperature, summary);
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
