package com.jaehong.soo.animalvideo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaehong.soo.animalvideo.FavoriteListActivity;
import com.jaehong.soo.animalvideo.R;
import com.jaehong.soo.animalvideo.dbhelper.YouTubeDBHelper;
import com.jaehong.soo.animalvideo.vo.FavoriteVO;

import java.util.ArrayList;

/**
 * Created by jeahong on 2018-05-13.
 */

public class FavoriteListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<FavoriteVO> list;
    private int layout;
    private LayoutInflater inflater;

    private String videoId;
    private String thumbnails;
    private YouTubeDBHelper db;

    public ImageView youTubeImage;
    public ImageView imageDelete;
    public TextView txtTitle;

    public FavoriteListAdapter(Context context, int layout, ArrayList<FavoriteVO> list){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(layout,null);
        }

        youTubeImage = convertView.findViewById(R.id.you_tube_player);
        imageDelete = convertView.findViewById(R.id.image_delete);
        txtTitle = convertView.findViewById(R.id.txt_title);

        videoId = list.get(position).getVideoId();
        thumbnails = list.get(position).getVideoThumbnails();

        txtTitle.setText(list.get(position).getVideoTitle());
        Glide.with(context).load(thumbnails).into(youTubeImage);

        db = new YouTubeDBHelper(context.getApplicationContext());

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoId = list.get(position).getVideoId();
                db.favoriteClear(videoId);
                list.remove(position);
                FavoriteListAdapter.this.notifyDataSetChanged();
            }
        });

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
