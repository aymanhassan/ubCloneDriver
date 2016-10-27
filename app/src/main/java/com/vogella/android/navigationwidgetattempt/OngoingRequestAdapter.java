package com.vogella.android.navigationwidgetattempt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nezuma on 10/27/16.
 */

public class OngoingRequestAdapter extends RecyclerView.Adapter <com.vogella.android.navigationwidgetattempt.OngoingRequestAdapter.OngoingRequestViewHolder> {

    private List<request> RequestList;
    private Context context;

    public OngoingRequestAdapter(List<request> RequestList, Context context) {
        this.RequestList = RequestList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return RequestList.size();
    }

    @Override
    public void onBindViewHolder(final com.vogella.android.navigationwidgetattempt.OngoingRequestAdapter.OngoingRequestViewHolder RequestViewHolder, int i) {
        request ci = RequestList.get(i);

        RequestViewHolder.passenger_name.setText(ci.passenger_name);
        RequestViewHolder.passenger_phone.setText(ci.passenger_phone);
        RequestViewHolder.status.setText(ci.status);
        RequestViewHolder.pickup.setText(ci.pickup);
        RequestViewHolder.dest.setText(ci.dest);
        RequestViewHolder.request_time.setText(ci.time);

        RequestViewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("passenger_name", RequestViewHolder.passenger_name.getText().toString());
                intent.putExtra("passenger_phone", RequestViewHolder.passenger_phone.getText().toString());
                intent.putExtra("status", RequestViewHolder.status.getText().toString());
                intent.putExtra("pickup", RequestViewHolder.pickup.getText().toString());
                intent.putExtra("dest", RequestViewHolder.dest.getText().toString());
                intent.putExtra("time", RequestViewHolder.request_time.getText().toString());

                ((Activity)context).setResult(Activity.RESULT_OK, intent);
                ((Activity)context).finish();

            }
        });
    }

    @Override
    public com.vogella.android.navigationwidgetattempt.OngoingRequestAdapter.OngoingRequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.request_card, viewGroup, false);

        return new com.vogella.android.navigationwidgetattempt.OngoingRequestAdapter.OngoingRequestViewHolder(itemView);
    }

    public static class OngoingRequestViewHolder extends RecyclerView.ViewHolder{
        protected TextView passenger_name;
        protected TextView passenger_phone;
        protected TextView request_time;
        protected TextView status;
        protected TextView pickup;
        protected TextView dest;
        RelativeLayout card;

        public OngoingRequestViewHolder(View v){
            super(v);
            passenger_name = (TextView) v.findViewById(R.id.card_passenger_name);
            passenger_phone = (TextView) v.findViewById(R.id.card_passenger_phone);
            request_time = (TextView) v.findViewById(R.id.card_time);
            status = (TextView) v.findViewById(R.id.card_status);
            pickup = (TextView) v.findViewById(R.id.card_pickup);
            dest = (TextView) v.findViewById(R.id.card_dest);
            card = (RelativeLayout) itemView.findViewById(R.id.card_view);
        }
    }

}

