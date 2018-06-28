/*
 * Copyright (c) 2017 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.iostudio.adapter.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.app.iostudio.R;
import com.app.iostudio.model.VideoDictionary;
import com.app.iostudio.youtube.YouTubePlayerHelper;
import com.app.iostudio.youtube.YouTubePlayerManager;
import com.app.iostudio.youtube.common.AspectRatioFrameLayout;
import com.app.iostudio.youtube.common.ViewUtil;
import com.squareup.picasso.Picasso;

import im.ene.toro.ToroPlayer;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

import static im.ene.toro.ToroUtil.visibleAreaOffset;

/**
 * @author eneim (8/1/17).
 */

@SuppressWarnings({"WeakerAccess", "unused"}) //
public final class YouTubeVideoViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {

    private static final String TAG = "YouT:ViewHolder";
    private final YouTubePlayerManager helperManager;

    String videoId;
    YouTubePlayerHelper helper;

    final FrameLayout playerView;
    final AspectRatioFrameLayout playerViewContainer;
    private Context context;
    public ImageView thumbnailView;

    public YouTubeVideoViewHolder(Context context, YouTubePlayerManager helperManager,
            View itemView) {
        super(itemView);
        this.context = context;
        this.helperManager = helperManager;
        playerViewContainer = itemView.findViewById(R.id.player_container);
        playerView = itemView.findViewById(R.id.player_view);
        thumbnailView = itemView.findViewById(R.id.thumbnail);
        int viewId = ViewUtil.generateViewId();
        playerView.setId(viewId);
    }

    @NonNull
    @Override
    public FrameLayout getPlayerView() {
        return playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
        if (helper == null) {
            helper = helperManager.obtainHelper(this, this.videoId);
        }
        helper.initialize(container, playbackInfo);
        thumbnailView.setVisibility(View.VISIBLE);
    }

    @Override
    public void play() {
        thumbnailView.setVisibility(View.GONE);
        if (helper != null) helper.play();
    }

    @Override
    public void pause() {
        if (helper != null) helper.pause();
        thumbnailView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        thumbnailView.setVisibility(View.VISIBLE);
        helperManager.releaseHelper(this);
        this.helper = null;
    }

    @Override
    public boolean wantsToPlay() {
        // YouTube Player API requires the player view to be fully visible.
        return visibleAreaOffset(this, itemView.getParent()) >= 0.999;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    private String getVideoImg(String videoId) {
        return "http://img.youtube.com/vi/" + videoId +
                "/mqdefault.jpg;"; //medium quality thumbnail

    }

    public void bind(@NonNull VideoDictionary item) {
        this.videoId = item.getYtId();

        Picasso.with(context)
                .load(getVideoImg(this.videoId))
                .fit().centerInside()
                .noPlaceholder()
                .into(thumbnailView);
/*
        Thumbnail thumbnail = item.getSnippet().getThumbnails().getHigh();
        if (thumbnail != null && thumbnail.getHeight() > 0) {
            playerViewContainer
                    .setAspectRatio(thumbnail.getWidth() / (float) thumbnail.getHeight());
            Glide.with(itemView).load(thumbnail.getUrl()).apply(options).into(thumbnailView);
        }
*/
    }

    @Override
    public String toString() {
        return "Player{" + "videoId='" + videoId + '\'' + ", order=" + getPlayerOrder() + '}';
    }


    public void onRecycled() {
        // do nothing
    }

}
