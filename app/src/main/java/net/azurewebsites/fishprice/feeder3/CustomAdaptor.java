package net.azurewebsites.fishprice.feeder3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Saral on 04-01-2016.
 */

    public class CustomAdaptor extends BaseAdapter {
        private static ArrayList<FeedResults> searchArrayList;

        private LayoutInflater mInflater;

        public CustomAdaptor(Context context, ArrayList<FeedResults> results) {
            searchArrayList = results;
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return searchArrayList.size();
        }

        public Object getItem(int position) {
            return searchArrayList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.custom_row_view, null);
                holder = new ViewHolder();
                holder.txtDestinationName = (TextView) convertView.findViewById(R.id.txtDestinationName);
                holder.txtScheduledDeparture = (TextView) convertView.findViewById(R.id.txtScheduledDeparture);
                holder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtDestinationName.setText(searchArrayList.get(position).getDestinationName());
            holder.txtScheduledDeparture.setText(searchArrayList.get(position).getScheduledDeparture());
            holder.txtStatus.setText(searchArrayList.get(position).getStatus());

            return convertView;
        }

        static class ViewHolder {
            TextView txtDestinationName;
            TextView txtScheduledDeparture;
            TextView txtStatus;
        }
    }

