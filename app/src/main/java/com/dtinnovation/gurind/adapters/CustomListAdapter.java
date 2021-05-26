package com.dtinnovation.gurind.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.dtinnovation.gurind.R;
import com.dtinnovation.gurind.models.MessageInfo;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter{
    private ArrayList<MessageInfo> messageInfos;
    private Context context;

    public CustomListAdapter(ArrayList<MessageInfo> messageInfos, Context context) {
        this.messageInfos = messageInfos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messageInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.custom_list_view_layout,null);

        MessageInfo messageInfo=messageInfos.get(i);
        TextView subject=view.findViewById(R.id.subject);
        ImageView read_dot=view.findViewById(R.id.read_dot);
        TextView date=view.findViewById(R.id.date);
        TextView body=view.findViewById(R.id.body);
        subject.setText(messageInfo.getSubject());
        date.setText(messageInfo.getDate());

        if(messageInfo.getBody().length()>50){
            body.setText(messageInfo.getBody().substring(0,20)+".....");
        }else{
            body.setText(messageInfo.getBody());
        }

        if(messageInfo.getRead_Status().equals("0")){
            read_dot.setVisibility(View.VISIBLE);
            subject.setTypeface(subject.getTypeface(), Typeface.BOLD);
        }

        return view;
    }



}
