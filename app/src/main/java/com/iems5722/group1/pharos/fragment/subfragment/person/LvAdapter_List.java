package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iems5722.group1.pharos.R;

import java.util.List;

/**
 * Created by Sora on 16/2/17.
 */

public class LvAdapter_List extends BaseAdapter {
    private List<Entity_Person_List> content;
    private LayoutInflater mInflater;

    public LvAdapter_List(Context context, List<Entity_Person_List> content) {
        this.content = content;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return 1;
    }

    public Object getItem(int position) {
        return 1;
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public int getItemViewType(int position) {

        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Entity_Person_List entity = this.content.get(position);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        if(entity.getType() == 1) {
            convertView = this.mInflater.inflate(R.layout.fragment_me_content, null);
        }
        ImageView ivNotice = (ImageView)convertView.findViewById(R.id.imgNote);
        TextView tvMsg = (TextView)convertView.findViewById(R.id.tv_msg);
        TextView tvMsgNum = (TextView)convertView.findViewById(R.id.tv_msg_num);
        viewHolder.ivNotice = ivNotice;
        viewHolder.tvMsg = tvMsg;
        viewHolder.tvMsgNum = tvMsgNum;
        convertView.setTag(viewHolder);
        viewHolder.tvMsg.setText(entity.getContent()+" messages unread");
        if (entity.getContent().equals("0")){
            viewHolder.tvMsgNum.setVisibility(View.INVISIBLE);
        }
        else{
            viewHolder.tvMsgNum.setVisibility(View.VISIBLE);
            viewHolder.tvMsgNum.setText(entity.getContent());
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView ivNotice;
        public TextView tvMsg;
        public TextView tvMsgNum;
        ViewHolder() {}
    }

}
