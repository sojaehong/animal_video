package com.jaehong.soo.animalvideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fsn.cauly.CaulyNativeAdHelper;
import com.jaehong.soo.animalvideo.R;

import java.util.List;
import java.util.Map;

public class MovieListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Map<String, Object>> list;
    private ListView listView;
    private LayoutInflater inflater;

    private static final int YOUR_ITEM_TYPE = 0;
    private static final int YOUR_ITEM_COUNT = 1;

    public MovieListAdapter(Context context, int layout, List<Map<String, Object>> list, ListView listView) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        this.listView = listView;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieListViewHolder viewHolder = null;

        if(convertView == null) {
            convertView = inflater.inflate(layout, null);
            viewHolder = new MovieListViewHolder();

            ImageView thumbnails = (ImageView) convertView.findViewById(R.id.thumbnails);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView date = (TextView) convertView.findViewById(R.id.date);

            viewHolder.thumbnails = thumbnails;
            viewHolder.title = title;
            viewHolder.date = date;

            viewHolder.thumbnails.setAdjustViewBounds(false);
            viewHolder.thumbnails.setScaleType(ImageView.ScaleType.FIT_XY);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MovieListViewHolder) convertView.getTag();
        }

        if(CaulyNativeAdHelper.getInstance().isAdPosition(listView, position) )
        {
            return CaulyNativeAdHelper.getInstance().getView(listView,position, convertView);
        }
        else
        {
            String thumbnails = (String) list.get(position).get("thumbnails");
            Glide.with(context).load(thumbnails).into(viewHolder.thumbnails);

            String date = (String) list.get(position).get("date");
            viewHolder.date.setText(date);

            String title = (String) list.get(position).get("title");
            viewHolder.title.setText(title);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    // 새로운 네이티브애드 타입이 존재하기 때문에 하나를 등록해준다.
    @Override
    public int getItemViewType(int position) {
        if(CaulyNativeAdHelper.getInstance().isAdPosition(listView,position))
            return YOUR_ITEM_TYPE+1;
        else
            return YOUR_ITEM_TYPE;
    }

    // 기존의 레이아웃타입 + 1 의 총개수 등록
    @Override
    public int getViewTypeCount() {
        return YOUR_ITEM_COUNT+1;
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

class MovieListViewHolder {
    ImageView thumbnails;
    TextView title;
    TextView date;
}