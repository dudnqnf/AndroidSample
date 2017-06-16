package com.sportsfactory.sportforall;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.sportsfactory.adapter.ClubPagerAdapter;
import com.sportsfactory.adapter.HomePagerAdapter;
import com.sportsfactory.info.URL;

public class MainActivity extends FragmentActivity {

    private HomePagerAdapter HomeSectionsPagerAdapter;
    private ClubPagerAdapter ClubSectionsPagerAdapter;
    private ViewPager mViewPager;

    private FrameLayout viewpagerLayout;
    private FrameLayout webviewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewpagerLayout = (FrameLayout)findViewById(R.id.viewpagerLayout);
        webviewLayout = (FrameLayout)findViewById(R.id.webviewLayout);

        //하단탭별로 페이져 생성
        HomeSectionsPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        ClubSectionsPagerAdapter = new ClubPagerAdapter(getSupportFragmentManager());

        //홈,동호회 탭
        //페이저 적용
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(HomeSectionsPagerAdapter);
        //탭 적용
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //웹뷰
        final WebView mWebview = (WebView)findViewById(R.id.mWebview);

        //어뎁터변경
        Button homeBtn = (Button)findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceLayout(viewpagerLayout);
                mViewPager.setAdapter(HomeSectionsPagerAdapter);
            }
        });

        Button clubBtn = (Button)findViewById(R.id.clubBtn);
        clubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceLayout(viewpagerLayout);
                mViewPager.setAdapter(ClubSectionsPagerAdapter);
            }
        });

        Button newsBtn = (Button)findViewById(R.id.newsBtn);
        newsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = URL.TEST_URL + "news.html";
                mWebview.loadUrl(url);
                replaceLayout(webviewLayout);
            }
        });

        Button myBtn = (Button)findViewById(R.id.myBtn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = URL.TEST_URL + "home1.html";
                mWebview.loadUrl(url);
                replaceLayout(webviewLayout);
            }
        });
    }

    public void replaceLayout(View v){

        v.setVisibility(View.VISIBLE);

        if(v == viewpagerLayout)
            webviewLayout.setVisibility(View.INVISIBLE);
        else if(v == webviewLayout)
            viewpagerLayout.setVisibility(View.INVISIBLE);
    }

}
