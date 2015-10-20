package me.madri.vanilai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.madri.vanilai.R;
import me.madri.vanilai.weather.DailyVanilai;

/**
 * Created by bnara on 10/18/2015.
 */
public class DayAdapter extends BaseAdapter {
    private Context mContext;
    private DailyVanilai[] mDailyVanilais;

    public DayAdapter(Context context, DailyVanilai[] dailyVanilais) {
        mContext = context;
        mDailyVanilais = dailyVanilais;
    }
    @Override
    public int getCount() {
        return mDailyVanilais.length;
    }

    @Override
    public Object getItem(int position) {
        return mDailyVanilais[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            //Start of View
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_view, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DailyVanilai dailyVanilai = mDailyVanilais[position];
        holder.iconImageView.setImageResource(dailyVanilai.getIconId());
        holder.temperatureLabel.setText(Integer.toString(dailyVanilai.getMaxTemperature()));
        if(position == 0) {
            holder.dayLabel.setText("Today");
        } else {
            holder.dayLabel.setText(dailyVanilai.getDayOfTheWeek());
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
