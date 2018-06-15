package com.jaehong.soo.animalvideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jaehong.soo.animalvideo.GetJsonData;
import com.jaehong.soo.animalvideo.R;
import com.jaehong.soo.animalvideo.YouTubeListActivity;
import com.jaehong.soo.animalvideo.adapter.MainListAdapter;
import com.jaehong.soo.animalvideo.vo.SearchVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OmnivoreFragment extends Fragment{
    private ListView listView;
    private MainListAdapter adapter;
    private ArrayList<SearchVO> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.omnivore_list_fragment,null);

        listView = view.findViewById(R.id.list_omnivore);

        try {
            list = getJsonItem();
        } catch (JSONException e) {
            Log.e("Json","Json 읽어오기 오류");
        }

        adapter = new MainListAdapter(getContext(), R.layout.main_list_item, list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), YouTubeListActivity.class);
                intent.putExtra("title", list.get(position).getTitle());
                intent.putExtra("search", list.get(position).getSearch());
                startActivity(intent);
            }
        });

        return view;
    }

    private ArrayList<SearchVO> getJsonItem() throws JSONException {
        ArrayList<SearchVO> list = new ArrayList<>();
        JSONObject jsonObject = new GetJsonData().getJsonObject(getContext(),"animal.json");
        JSONArray animalJsonArray = new JSONArray(jsonObject.getString("animal"));
        JSONArray jsonArray = new JSONArray(animalJsonArray.getJSONObject(2).getString("omnivore"));

        for(int i = 0; i < jsonArray.length(); i++){
            SearchVO animalVO = new SearchVO();
            animalVO.setTitle(jsonArray.getJSONObject(i).getString("title"));
            animalVO.setSearch(jsonArray.getJSONObject(i).getString("search"));
            list.add(animalVO);
        }

        return list;
    }
}
