package com.app.iostudio.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.app.iostudio.R;
import com.app.iostudio.adapter.base.VideoRecyclerAdapter;
import com.app.iostudio.adapter.viewholder.VideoViewHolder;
import com.app.iostudio.app.App;
import com.app.iostudio.data.DashboardMenuData;
import com.app.iostudio.model.VideoData;
import com.app.iostudio.model.VideoDictionary;
import com.app.iostudio.pref.IOPref;
import com.app.iostudio.youtube.YouTubePlayerDialog;
import com.app.iostudio.youtube.YouTubePlayerHelper;
import com.app.iostudio.youtube.YouTubePlayerManager;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import im.ene.toro.PlayerSelector;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.media.VolumeInfo;
import im.ene.toro.widget.Container;

import static im.ene.toro.media.PlaybackInfo.INDEX_UNSET;
import static im.ene.toro.media.PlaybackInfo.TIME_UNSET;

public class MainActivity extends BaseActivity implements CacheListener,
        YouTubePlayerDialog.Callback, YouTubePlayerHelper.Callback {

    RecyclerView recyclerView_video;
    private Context context;
    private VideoRecyclerAdapter videoRecyclerAdapter;
    private ArrayList<VideoDictionary> videoArrayList;
    Container my_fancy_videos;
    private PlaybackInfo baseInfo;

    // Orientation helper stuff
    private WindowManager windowManager;
    PlayerSelector selector = PlayerSelector.DEFAULT;  // backup current selector.
    VideoRecyclerAdapter.CallbackVideo adapterCallback;
    private final int SETTINGS_RESULT = 108;


    // youtube
    YouTubePlayerManager playerManager;
    // Save manifest orientation
    int manifestOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(true, false, getString(R.string.app_name), false);
        initBase();

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        //youtube
        if (getIntent().getExtras() == null) manifestOrientation = getRequestedOrientation();


    }

    @Override
    protected void initView() {
        context = this;
        videoArrayList = new ArrayList<>();

        recyclerView_video = findViewById(R.id.recyclerView_video);
        my_fancy_videos = findViewById(R.id.my_fancy_videos);

    }

    @Override
    protected void initData() {


        //from json string convert it to data
        VideoData data = new Gson().fromJson(DashboardMenuData.videosData, VideoData.class);
        videoArrayList.clear();
        videoArrayList.addAll(data.getData());
        boolean isMuteByDefault =
                IOPref.getInstance().getBoolean(context, IOPref.PreferenceKey.isMute, true);

        for (int i = 0; i < videoArrayList.size(); i++) {

            HttpProxyCacheServer proxy = App.getProxy(context);
            proxy.registerCacheListener(this, videoArrayList.get(i).getUrl());
            String proxyUrl = proxy.getProxyUrl(videoArrayList.get(i).getUrl());
            videoArrayList.get(i).setProxyUrl(proxyUrl);


            videoArrayList.get(i).setMute(isMuteByDefault);
            videoArrayList.get(i).setTimeStamp(System.currentTimeMillis() + i * 60_000);
        }


        // Prepare Container for youtube
        playerManager = new YouTubePlayerManager(getSupportFragmentManager(), this);


        videoRecyclerAdapter = new VideoRecyclerAdapter(context,
                videoArrayList,
                isMuteByDefault,
                System.currentTimeMillis(),
                playerManager);
        my_fancy_videos.setAdapter(videoRecyclerAdapter);
        my_fancy_videos.setLayoutManager(new LinearLayoutManager(context));
        my_fancy_videos.setCacheManager(videoRecyclerAdapter);
        my_fancy_videos.setPlayerSelector(selector);

        if (isMuteByDefault) {
            my_fancy_videos.setPlayerInitializer(new Container.Initializer() {
                @NonNull
                @Override
                public PlaybackInfo initPlaybackInfo(int order) {
                    VolumeInfo volumeInfo = new VolumeInfo(true, 0.75f);
                    return new PlaybackInfo(INDEX_UNSET, TIME_UNSET, volumeInfo);
                }
            });
        }


        adapterCallback = new VideoRecyclerAdapter.CallbackVideo() {
            @Override
            public void onItemClick(@NonNull VideoViewHolder viewHolder, @NonNull View view,
                    @NonNull VideoDictionary item, int position) {
                Log.d("adapterCallback", " onItemClick");
            }
        };

    }


    @Override
    protected void bindEvent() {

    }


    @Override
    protected void onDestroy() {
        videoRecyclerAdapter.setCallback(null);
        adapterCallback = null;
        videoRecyclerAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        //Log.d("Activity  ", " onCacheAvailable: " + url + " percentsAvailable:" + percentsAvailable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(context, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_RESULT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case SETTINGS_RESULT:
                    if (resultCode == RESULT_OK) {
                        initData();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //youtube
    @Override
    public void onBigPlayerCreated() {
        Log.d("onBigPlayerCreated", ": called");
    }


    //youtube
    @Override
    public void onBigPlayerDestroyed(int videoOrder, String baseItem, PlaybackInfo latestInfo) {
        Log.d("onBigPlayerDestroyed",
                ": called: " + " baseItem:" + baseItem + " videoOrder:" + videoOrder);
    }


    //youtube
    @Override
    public void onPlayerCreated(@NonNull YouTubePlayerHelper helper,
            @NonNull YouTubePlayer player) {
        Log.d("onPlayerCreated",
                ": called: " + " player:" + player.getCurrentTimeMillis() + " helper:" +
                        helper.getVolume());
    }

    //youtube
    @Override
    public void onPlayerDestroyed(@NonNull YouTubePlayerHelper helper) {
        Log.d("onPlayerDestroyed", ": called: " + " player:" + " helper:" + helper.getVolume());

    }

    //youtube
    @Override
    public void onFullscreen(@NonNull YouTubePlayerHelper helper, @Nullable YouTubePlayer player,
            boolean fullscreen) {
        Log.d("onFullscreen",
                ": called: " + " player:" + " helper:" + helper.getVolume() + " fullscreen:" +
                        fullscreen);

    }
}
