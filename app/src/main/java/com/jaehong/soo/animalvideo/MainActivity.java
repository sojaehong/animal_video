package com.jaehong.soo.animalvideo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyAdInfoBuilder;
import com.fsn.cauly.CaulyCloseAd;
import com.fsn.cauly.CaulyCloseAdListener;
import com.jaehong.soo.animalvideo.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements CaulyCloseAdListener{

    private Toolbar myToolbar;

    //카우리 종료 광고
    private final String APP_CODE = "42geXpWU";
//    private final String APP_CODE = "CAULY";
    private CaulyCloseAd mCloseAd ;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //뷰 페이져 셋팅
        ViewPager viewPager = findViewById(R.id.view_pager_main);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        //텝 레이아웃 셋팅
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        //툴바에 액션바 셋팅
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);

        //카울리 종료 광고
        CaulyAdInfo closeAdInfo = new CaulyAdInfoBuilder(APP_CODE).build();
        mCloseAd = new CaulyCloseAd();

        mCloseAd.setAdInfo(closeAdInfo);
        mCloseAd.setCloseAdListener(this);
        //

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

    // 카울리 종료 광고
    @Override
    protected void onResume() {
        super.onResume();
        if (mCloseAd != null)
            mCloseAd.resume(this); // 필수 호출
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 앱을 처음 설치하여 실행할 때, 필요한 리소스를 다운받았는지 여부.
            if (mCloseAd.isModuleLoaded())
            {
                mCloseAd.show(this);
            }
            else
            {
                // 광고에 필요한 리소스를 한번만  다운받는데 실패했을 때 앱의 종료팝업 구현
                showDefaultClosePopup();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDefaultClosePopup() {
        new AlertDialog.Builder(this).setTitle("").setMessage("종료 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니요",null)
                .show();
    }

    // CaulyCloseAdListener
    @Override
    public void onFailedToReceiveCloseAd(CaulyCloseAd ad, int errCode,String errMsg) {
    }
    // CloseAd의 광고를 클릭하여 앱을 벗어났을 경우 호출되는 함수이다.
    @Override
    public void onLeaveCloseAd(CaulyCloseAd ad) {
    }
    // CloseAd의 request()를 호출했을 때, 광고의 여부를 알려주는 함수이다.
    @Override
    public void onReceiveCloseAd(CaulyCloseAd ad, boolean isChargable) {

    }
    //왼쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    @Override
    public void onLeftClicked(CaulyCloseAd ad) {

    }
    //오른쪽 버튼을 클릭 하였을 때, 원하는 작업을 수행하면 된다.
    //Default로는 오른쪽 버튼이 종료로 설정되어있다.
    @Override
    public void onRightClicked(CaulyCloseAd ad) {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    @Override
    public void onShowedCloseAd(CaulyCloseAd ad, boolean isChargable) {
    }
    // 카울리 종료 광고 끝
}
