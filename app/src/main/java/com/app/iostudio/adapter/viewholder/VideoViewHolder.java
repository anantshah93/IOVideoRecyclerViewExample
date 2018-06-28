package com.app.iostudio.adapter.viewholder;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.iostudio.R;
import com.app.iostudio.model.VideoDictionary;
import com.danikula.videocache.CacheListener;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.util.Locale;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.Playable;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

import static java.lang.String.format;


/**
 * Created by anantshah on 15/07/17.
 */

@SuppressWarnings({"WeakerAccess", "unused"}) //
public class VideoViewHolder extends RecyclerView.ViewHolder implements ToroPlayer, CacheListener {
    public TextView textView_description;
    public ImageView imageView_sound;
    public PlayerView video_view;
    private static final String TAG = "IOSTUDIO:Video:Holder";

    private Context context;
    public ExoPlayerViewHelper helper;
    Uri mediaUri;


    private final Playable.EventListener listener = new Playable.DefaultEventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            super.onPlayerStateChanged(playWhenReady, playbackState);
            Log.d("onPlayerStateChanged", ":" +
                    format(Locale.getDefault(), "STATE: %d・PWR: %s", playbackState, playWhenReady));
        }
    };


    public VideoViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        video_view = itemView.findViewById(R.id.video_view);
        imageView_sound = itemView.findViewById(R.id.imageView_sound);
        //textView_description = itemView.findViewById(R.id.textView_description);
    }

    @NonNull
    @Override
    public PlayerView getPlayerView() {
        return video_view;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
        if (mediaUri == null) throw new IllegalStateException("mediaUri is null.");
        if (helper == null) {
            helper = new ExoPlayerViewHelper(this, mediaUri);
            helper.addEventListener(listener);
        }
        helper.initialize(container, playbackInfo);
    }

    @Override
    public void play() {
        if (helper != null) helper.play();
    }

    @Override
    public void pause() {
        if (helper != null) helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        if (helper != null) {
            helper.removeEventListener(listener);
            helper.release();
            helper = null;
        }
    }


    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    @Override
    public String toString() {
        return "ExoPlayer{" + hashCode() + " " + getAdapterPosition() + "}";
    }

    public void bind(VideoDictionary videoDictionary, int position) {
        String url = videoDictionary.getProxyUrl();
        //String url = videoDictionary.getUrl();

/*

        // video cache
        HttpProxyCacheServer proxy = App.getProxy(context);
        proxy.registerCacheListener(this, url);
        String proxyUrl = proxy.getProxyUrl(url);

*/

        Uri uri = Uri.parse(url);
        videoDictionary.setMediaUri(uri);
        this.mediaUri = uri;
    }

    public void onRecycled() {
        // do nothing
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            //Log.d("onCacheAvailable",":"+url +" percentsAvailable:"+percentsAvailable);
    }
}
