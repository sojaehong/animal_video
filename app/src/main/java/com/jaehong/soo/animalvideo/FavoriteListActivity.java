package com.jaehong.soo.animalvideo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.jaehong.soo.animalvideo.adapter.FavoriteListAdapter;
import com.jaehong.soo.animalvideo.dbhelper.YouTubeDBHelper;
import com.jaehong.soo.animalvideo.vo.FavoriteVO;

import java.util.ArrayList;

public class FavoriteListActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private ListView listView;
    private TextView txtNoList;
    private LinearLayout linearLayout;
    private Toolbar myToolbar;
    private TextView txtTitle;

    private FavoriteListAdapter adapter = null;
    private ArrayList<FavoriteVO> list;
    private YouTubeDBHelper db;

    private YouTubePlayer ytp;
    private boolean isFullScreen = false;
    private final String serverKey = "AIzaSyB0rJcKoAhPqFqLYVyrcTwhs3O-FW7EHsk";
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        db = new YouTubeDBHelper(getApplicationContext());
        list = db.favoriteDataAllSelect();

        linearLayout = findViewById(R.id.layout_player);
        txtNoList = findViewById(R.id.txt_no_list);
        listView = findViewById(R.id.recycler_favorite);
        txtTitle = findViewById(R.id.txt_title);

        //툴바에 액션바 셋팅
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        listNullCheckAndVisibilitySet();

        favoriteAdabterSet();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (linearLayout.getVisibility() == View.GONE) {
                    linearLayout.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.VISIBLE);
                }

                videoId = list.get(position).getVideoId();
                txtTitle.setText(list.get(position).getVideoTitle());
                ytp.loadVideo(videoId);
            }
        });

        initializeYoutubePlayer();
    }

    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     */
    private void initializeYoutubePlayer() {

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_favorite);

        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(serverKey, this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_favorite , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete){
            db.favoriteClear();
            list = db.favoriteDataAllSelect();
            favoriteAdabterSet();
            listNullCheckAndVisibilitySet();
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * list의 사이즈가 0인지 확인하고 visibility를 세팅
     */
    private void listNullCheckAndVisibilitySet() {
        if (list.size() == 0) {
            txtNoList.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            txtTitle.setVisibility(View.GONE);
            return;
        } else {
            txtNoList.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void favoriteAdabterSet(){
        adapter = new FavoriteListAdapter(getApplicationContext(), R.layout.favorite_list_item, list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            ytp.setFullscreen(false);
        } else {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}
