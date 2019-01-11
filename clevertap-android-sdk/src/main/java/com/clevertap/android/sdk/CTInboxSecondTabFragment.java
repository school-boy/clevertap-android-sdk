package com.clevertap.android.sdk;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CTInboxSecondTabFragment extends CTInboxTabBaseFragment {
    RecyclerView recyclerView;
    private boolean firstTime = true;
    ArrayList<CTInboxMessage> filteredMessages = new ArrayList<>();
    ExoPlayerRecyclerView exoPlayerRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View allView = inflater.inflate(R.layout.inbox_first_tab,container,false);
        LinearLayout linearLayout = allView.findViewById(R.id.first_tab_linear_layout);
        linearLayout.setBackgroundColor(Color.parseColor(styleConfig.getInboxBackgroundColor()));
        TextView noMessageView = allView.findViewById(R.id.first_tab_no_message_view);
        //Check if video present to render appropriate recyclerview
        CTInboxMessageAdapter inboxMessageAdapter;
        if(videoPresent) {
            if(inboxMessageArrayList.size()>0) {
                exoPlayerRecyclerView = new ExoPlayerRecyclerView(getActivity());
                exoPlayerRecyclerView.setVisibility(View.VISIBLE);
                filteredMessages = filterMessages(inboxMessageArrayList,styleConfig.getSecondTab());
                exoPlayerRecyclerView.setVideoInfoList(filteredMessages);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                exoPlayerRecyclerView.setLayoutManager(linearLayoutManager);
                exoPlayerRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(18));
                exoPlayerRecyclerView.setItemAnimator(new DefaultItemAnimator());

                inboxMessageAdapter = new CTInboxMessageAdapter(filteredMessages, getActivity(), this);
                //inboxMessageAdapter.filterMessages(styleConfig.getSecondTab());//Filters the messages before rendering the list on tabs
                exoPlayerRecyclerView.setAdapter(inboxMessageAdapter);
                inboxMessageAdapter.notifyDataSetChanged();
                if (firstTime) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exoPlayerRecyclerView.playVideo();
                        }
                    }, 1000);
                    firstTime = false;
                }
                linearLayout.addView(exoPlayerRecyclerView);
                noMessageView.setVisibility(View.GONE);
            }
        }else{
            if(inboxMessageArrayList.size()>0) {
                recyclerView = allView.findViewById(R.id.first_tab_recycler_view);
                recyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(18));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                filteredMessages = filterMessages(inboxMessageArrayList,styleConfig.getFirstTab());
                inboxMessageAdapter = new CTInboxMessageAdapter(filteredMessages, getActivity(), this);
                inboxMessageAdapter.filterMessages(styleConfig.getSecondTab());//Filters the messages before rendering the list on tabs
                recyclerView.setAdapter(inboxMessageAdapter);
                inboxMessageAdapter.notifyDataSetChanged();
                noMessageView.setVisibility(View.GONE);
            }
        }

        return allView;
    }

    @Override
    public void onPause() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(videoPresent) {
                    if (exoPlayerRecyclerView != null)
                        exoPlayerRecyclerView.onPausePlayer();
                }
            }
        });
        super.onPause();
    }

    @Override
    public void onResume() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(videoPresent) {
                    if (exoPlayerRecyclerView != null)
                        exoPlayerRecyclerView.onRestartPlayer();
                }
            }
        });
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if(exoPlayerRecyclerView!=null && videoPresent)
            exoPlayerRecyclerView.onRelease();
        super.onDestroy();
    }
}
