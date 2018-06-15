package com.jaehong.soo.animalvideo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyNativeAdHelper;
import com.fsn.cauly.CaulyNativeAdInfoBuilder;
import com.fsn.cauly.CaulyNativeAdView;
import com.fsn.cauly.CaulyNativeAdViewListener;
import com.jaehong.soo.animalvideo.adapter.MovieListAdapter;
import com.jaehong.soo.animalvideo.common.HttpClient;
import com.jaehong.soo.animalvideo.common.URLRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YouTubeListActivity extends AppCompatActivity{
    private Map<String, Object> map = null;
    private ListView listView = null;
    private Toolbar myToolbar;
    private MovieListAdapter adapter = null;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    private String search = null;
    private String nextPageToken = null;
    boolean lastItemVisibleFlag = false;

    private final String serverKey = "AIzaSyB0rJcKoAhPqFqLYVyrcTwhs3O-FW7EHsk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_list);

        Intent intent = getIntent();
        search = intent.getStringExtra("search");
        String title = intent.getStringExtra("title");
        TextView toolbarTitle = findViewById(R.id.txt_toolbar_title);

        //툴바에 액션바 셋팅
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        toolbarTitle.setText(title);

        listView = findViewById(R.id.list);
        adapter = new MovieListAdapter(getApplicationContext(), R.layout.movie_list_big_item, list, listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), YouTubePlayActivity.class);
                intent.putExtra("videoId", list.get(position).get("videoId").toString());
                intent.putExtra("title", list.get(position).get("title").toString());
                intent.putExtra("thumbnails", list.get(position).get("thumbnails").toString());
                intent.putExtra("search", search);
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    appendListData(nextPageToken);
                }
            }

        });
        appendListData(nextPageToken);
    }

    public void appendListData(final String nextPageToken) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://www.googleapis.com/youtube/v3/search";
                URLRequest req = new URLRequest(url);
                req.put("key", serverKey);
                req.put("part", "snippet");
                req.put("type", "video");
                req.put("maxResults", "20");
                req.put("order", "viewCount");
                req.put("safeSearch", "strict");
                req.put("q", search);

                if(nextPageToken != null && !nextPageToken.trim().equals("")) {
                    req.put("pageToken", nextPageToken);
                }

                HttpClient client = new HttpClient();
                String result = client.get(req);
                if(result != null) {
                    ObjectMapper om = new ObjectMapper();
                    try {
                        map = om.readValue(result, new TypeReference<Map<String, Object>>() {});

                        YouTubeListActivity.this.nextPageToken = (String) map.get("nextPageToken");

                        List<Map<String, Object>> items = (List<Map<String, Object>>) map.get("items");
                        for(int i = 0; i < items.size(); i++) {
                            Map<String, Object> item = items.get(i);

                            Map<String, Object> id = (Map<String, Object>) item.get("id");
                            String videoId = (String) id.get("videoId");

                            Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                            String title = (String) snippet.get("title");
                            String date = (String) snippet.get("publishedAt");
                            date = date.substring(0, date.indexOf("T"));

                            Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                            Map<String, Object> def = (Map<String, Object>) thumbnails.get("medium");
                            String imgUrl = (String) def.get("url");

                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("videoId", videoId);
                            map.put("title", title);
                            map.put("date", date);
                            map.put("thumbnails", imgUrl);

                            list.add(map);
                        }
                        YouTubeListActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "서버 연결 오류", Toast.LENGTH_SHORT).show();
                }

            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.favorite){
            Intent intent = new Intent(getApplicationContext(), FavoriteListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
