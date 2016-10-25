package com.vogella.android.navigationwidgetattempt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nezuma on 10/26/16.
 */

public class RequestAdapter extends RecyclerView.Adapter <RequestAdapter.RequestViewHolder> {

    private List<request> RequestList;

    public RequestAdapter(List<request> RequestList) {
        this.RequestList = RequestList;
    }


    @Override
    public int getItemCount() {
        return RequestList.size();
    }

    @Override
    public void onBindViewHolder(RequestViewHolder RequestViewHolder, int i) {
        request ci = RequestList.get(i);
        RequestViewHolder.passenger_name.setText(ci.passenger_name);
        RequestViewHolder.passenger_phone.setText(ci.passenger_phone);
        RequestViewHolder.status.setText(ci.status);
        RequestViewHolder.pickup.setText(ci.pickup);
        RequestViewHolder.dest.setText(ci.dest);
        RequestViewHolder.request_time.setText(ci.time);
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.request_card, viewGroup, false);

        return new RequestViewHolder(itemView);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        protected TextView passenger_name;
        protected TextView passenger_phone;
        protected TextView request_time;
        protected TextView status;
        protected TextView pickup;
        protected TextView dest;

        public RequestViewHolder(View v){
            super(v);
            passenger_name = (TextView) v.findViewById(R.id.card_passenger_name);
            passenger_phone = (TextView) v.findViewById(R.id.card_passenger_phone);
            request_time = (TextView) v.findViewById(R.id.card_time);
            status = (TextView) v.findViewById(R.id.card_status);
            pickup = (TextView) v.findViewById(R.id.card_pickup);
            dest = (TextView) v.findViewById(R.id.card_dest);
        }
    }

}
