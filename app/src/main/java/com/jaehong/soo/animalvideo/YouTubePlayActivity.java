package com.jaehong.soo.animalvideo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyNativeAdHelper;
import com.fsn.cauly.CaulyNativeAdInfoBuilder;
import com.fsn.cauly.CaulyNativeAdView;
import com.fsn.cauly.CaulyNativeAdViewListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.jaehong.soo.animalvideo.adapter.MovieListAdapter;
import com.jaehong.soo.animalvideo.common.HttpClient;
import com.jaehong.soo.animalvideo.common.URLRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.youtube.player.YouTubePlayer;
import com.jaehong.soo.animalvideo.dbhelper.YouTubeDBHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YouTubePlayActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener, CaulyNativeAdViewListener{

    private ListView listView = null;
    private TextView txtTitle;
    private ImageView imageFavorite;
    private MovieListAdapter adapter = null;

    private YouTubePlayer ytp;
    private boolean isFullScreen = false;
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    private Map<String, Object> map = null;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private String nextPageToken = null;
    boolean lastItemVisibleFlag = false;

    private YouTubeDBHelper youTubeDBHelper;

    private final String serverKey = "AIzaSyB0rJcKoAhPqFqLYVyrcTwhs3O-FW7EHsk";
    private final String APP_CODE = "42geXpWU";
    private int position = 0;

    private String videoId;
    private String search;
    private String title;
    private String thumbnails;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_play);

        youTubeDBHelper = new YouTubeDBHelper(getApplicationContext());

        txtTitle = findViewById(R.id.txt_title);
        imageFavorite = findViewById(R.id.image_favorite);

        intent = getIntent();

        videoId = intent.getStringExtra("videoId");
        title = intent.getStringExtra("title");
        search = intent.getStringExtra("search");
        thumbnails = intent.getStringExtra("thumbnails");

        txtTitle.setText(title);

        listView = findViewById(R.id.list_you_tube);
        adapter = new MovieListAdapter(getApplicationContext(), R.layout.movie_list_small_item, list, listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                videoId = list.get(position).get("videoId").toString();
                title = list.get(position).get("title").toString();
                thumbnails = list.get(position).get("thumbnails").toString();
                txtTitle.setText(title);

                ytp.loadVideo(videoId);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                    if(position <= 60){
                        position = position + 20;
                        requestNative();
                    }
                    appendListData(nextPageToken);
                }
            }

        });

        initializeYoutubePlayer();
        appendListData(nextPageToken);

        imageFavorite.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            ytp.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    // 화면이 종료될 때  Destory()필수 호출
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CaulyNativeAdHelper.getInstance().destroy();
    }

    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     */
    private void initializeYoutubePlayer() {

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_you_tube);

        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(serverKey, this);
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

                if (nextPageToken != null && !nextPageToken.trim().equals("")) {
                    req.put("pageToken", nextPageToken);
                }

                HttpClient client = new HttpClient();
                String result = client.get(req);
                if (result != null) {
                    ObjectMapper om = new ObjectMapper();
                    try {
                        map = om.readValue(result, new TypeReference<Map<String, Object>>() {
                        });

                        YouTubePlayActivity.this.nextPageToken = (String) map.get("nextPageToken");

                        List<Map<String, Object>> items = (List<Map<String, Object>>) map.get("items");
                        for (int i = 0; i < items.size(); i++) {
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
                        YouTubePlayActivity.this.runOnUiThread(new Runnable() {
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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        ytp = youTubePlayer;

        ytp.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        ytp.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                isFullScreen = b;
            }
        });

        ytp.loadVideo(videoId);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(getApplicationContext(), "영상 연결 실패", Toast.LENGTH_LONG).show();
    }

    // Request Native AD
    // 네이티브 애드에 보여질 디자인을 정의하고 세팅하는 작업을 수행한다. (icon, image, title, subtitle, description ...)
    // CaulyNativeAdViewListener 를 등록하여 onReceiveNativeAd or onFailedToReceiveNativeAd 로 네이티브광고의 상태를 전달받는다.
    public void requestNative(){
        CaulyAdInfo adInfo = new CaulyNativeAdInfoBuilder(APP_CODE)
                .layoutID(R.layout.movie_list_small_item)// 네이티브애드에 보여질 디자인을 작성하여 등록한다.
                .iconImageID(R.id.thumbnails)       // 아이콘 등록
                .titleID(R.id.title)	        // 제목 등록
                .subtitleID(R.id.date)	// 부제목 등록
                .build();

        CaulyNativeAdView nativeAd = new CaulyNativeAdView(this);
        nativeAd.setAdInfo(adInfo);
        nativeAd.setAdViewListener(this);
        nativeAd.request();
    }
    // 네이티브애드가 없거나, 네트웍상의 이유로 정상적인 수신이 불가능 할 경우 호출이 된다.
    public void onFailedToReceiveNativeAd(CaulyNativeAdView adView,	int errorCode, String errorMsg) {

    }
    // 네이티브애드가 정상적으로 수신되었을 떄, 호출된다.
    public void onReceiveNativeAd(CaulyNativeAdView adView, boolean isChargeableAd) {
        //우선  앱의 리스트에 등록을 하고, 똑같은 위치의 포지션에 수신한 네이티브애드를 등록한다.
        list.add(position,null);
        CaulyNativeAdHelper.getInstance().add(this,listView,position,adView);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        if (v == imageFavorite) {
            if (youTubeDBHelper.favoriteDataCheck(videoId) == true) {
                youTubeDBHelper.favoriteInsert(videoId, thumbnails,title);
                Toast.makeText(getApplicationContext(), "영상을 보관하였습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "이미 보관된 영상입니다.", Toast.LENGTH_SHORT).show();
            }

        }

    }
}