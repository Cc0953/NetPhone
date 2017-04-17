package com.example.cc.netphone;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder> {
    private Context mContext;
    private List<Phone> mPhoneList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView PhoneImage;
        TextView PhoneName;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            PhoneImage = (ImageView) itemView.findViewById(R.id.phone_type_image);
            PhoneName = (TextView) itemView.findViewById(R.id.phone_type_name);
        }
    }

    public PhoneAdapter(List<Phone> PhoneList) {
        mPhoneList = PhoneList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_phone, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Phone phone = mPhoneList.get(position);
                List<PhoneDataBases> list = DataSupport.findAll(PhoneDataBases.class);
                Intent intent = new Intent(mContext, Phone_content.class);
                intent.putExtra(Phone_content.Phone_NAME, phone.getName());
                mContext.startActivity(intent);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Phone phone = mPhoneList.get(position);
        try {
            holder.PhoneName.setText(phone.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glide.with(mContext).load(phone.getFile()).into(holder.PhoneImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mPhoneList.size();
    }


}
