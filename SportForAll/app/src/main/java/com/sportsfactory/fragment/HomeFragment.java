package com.sportsfactory.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.sportsfactory.info.URL;
import com.sportsfactory.sportforall.R;

public class HomeFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public HomeFragment() {
    }

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);

        String url = URL.TEST_URL;
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        switch (sectionNumber) {
            case 1 :
                url += "home1.html";
                break;
            case 2 :
                url += "home2.html";
                break;
            case 3 :
                url += "home3.html";
                break;
            case 4 :
                url += "home4.html";
                break;
            case 5 :
                url += "home5.html";
                break;
        }

        WebView mWebView = (WebView)rootView.findViewById(R.id.frag_webview);
        mWebView.loadUrl(url);

        return rootView;
    }
}