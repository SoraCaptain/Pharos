package com.iems5722.group1.pharos.fragment.subfragment.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.fragment.subfragment.person.Entity_Person_List;

import java.util.List;

/**
 * Created by Sora on 16/2/17.
 */

public class LvAdapter_Home extends BaseAdapter {
    private List<Entity_Home> content;
    private LayoutInflater mInflater;

    public LvAdapter_Home(Context context, List<Entity_Home> content) {
        this.content = content;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.content.size();
    }

    public Object getItem(int position) {
        return this.content.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public int getItemViewType(int position) {

        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Home entity = this.content.get(position);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        if(!entity.getPlaceImgWidth().equals("0")) {
            convertView = this.mInflater.inflate(R.layout.fragment_home_content, null);
        }
        viewHolder.ivImg = (ImageView)convertView.findViewById(R.id.ivImg);
        viewHolder.tvName = (TextView)convertView.findViewById(R.id.tvName);
        viewHolder.tvRate = (TextView)convertView.findViewById(R.id.tvRate);
        viewHolder.tvType = (TextView)convertView.findViewById(R.id.tvType);

        viewHolder.tvName.setText(entity.getPlaceName());
        viewHolder.tvType.setText(entity.getPlaceType());
        viewHolder.tvRate.setText(entity.getPlaceRate());
        //viewHolder.ivImg.setImageBitmap(entity.getPlaceImg());
        String url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=" + entity.getPlacePhotoPre() + "&key=AIzaSyARMqBMqfTYhi6NrUuF7RmvoJ69yBTynYA";
        Glide.with(mInflater.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .crossFade()
                .into(viewHolder.ivImg);
        convertView.setTag(viewHolder);

        return convertView;
    }

    private class ViewHolder {
        public ImageView ivImg;
        public TextView tvName;
        public TextView tvType;
        public TextView tvRate;
        ViewHolder() {}
    }

}
