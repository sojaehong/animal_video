package com.jaehong.soo.animalvideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaehong.soo.animalvideo.vo.SearchVO;
import com.jaehong.soo.animalvideo.R;

import java.util.ArrayList;

public class MainListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SearchVO> list;
    private int layout;
    private LayoutInflater inflater;

    public MainListAdapter(Context context, int layout, ArrayList<SearchVO> list){
        this.context = context;
        this.layout = layout;
        this.list = list;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(layout,null);
        }

        TextView txtTitle = convertView.findViewById(R.id.txt_title);

        txtTitle.setText(list.get(position).getTitle());

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
