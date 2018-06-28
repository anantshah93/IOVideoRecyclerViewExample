package com.app.iostudio.adapter.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.iostudio.R;
import com.app.iostudio.adapter.viewholder.VideoViewHolder;
import com.app.iostudio.adapter.viewholder.YouTubeVideoViewHolder;
import com.app.iostudio.model.VideoDictionary;
import com.app.iostudio.youtube.YouTubePlayerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import im.ene.toro.CacheManager;
import im.ene.toro.PlayerSelector;
import im.ene.toro.ToroPlayer;
import im.ene.toro.media.VolumeInfo;
import im.ene.toro.widget.Container;

/**
 * Created by Anant Shah on 24/04/15.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class VideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CacheManager, PlayerSelector {

    private Context context;
    private boolean isMuteByDefault;
    private ArrayList<VideoDictionary> dataArrayList;
    private boolean isErrorView = false;
    private final int TYPE_ERROR = -1;
    private final int TYPE_CELL = 1;
    private final int TYPE_CELL_YT = 2;
    private int isShowAll = 0;
    private final int VIEW_TYPE_LOADING = 3;
    private boolean isHorizontalView;

    private String message;

    private final long initTimeStamp;
    @Nullable
    public CallbackVideo callback;

    @SuppressWarnings("WeakerAccess")
    PlayerSelector origin;
    // Keep a cache of the Playback order that is manually paused by User.
    // So that if User scroll to it again, it will not start play.
    // Value will be updated by the ViewHolder.
    final AtomicInteger lastUserPause = new AtomicInteger(-1);


    //youtube
    private final YouTubePlayerManager manager;


    public void setAndShowError(String message, boolean isErrorView) {
        this.message = message;
        this.isErrorView = isErrorView;
        notifyDataSetChanged();

    }


    public VideoRecyclerAdapter(Context context,
            ArrayList<VideoDictionary> dataArrayList,
            boolean isMuteByDefault,
            long initTimeStamp,
            YouTubePlayerManager playerManager) {
        this.context = context;
        this.initTimeStamp = initTimeStamp;
        this.isMuteByDefault = isMuteByDefault;
        this.dataArrayList = dataArrayList;
        this.manager = playerManager;
        setHasStableIds(true);
    }

    public void setCallback(@Nullable CallbackVideo callback) {
        this.callback = callback;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        switch (position) {
            default: {
                if (isErrorView) {
                    return TYPE_ERROR;
                } else {
                    try {
                        return dataArrayList.get(position) == null ? VIEW_TYPE_LOADING :
                                dataArrayList.get(position).getYtId() == null ? TYPE_CELL :
                                        TYPE_CELL_YT;
                    } catch (Exception e) {
                        return TYPE_CELL;
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = (dataArrayList != null ? dataArrayList.size() : 0);
        if (isErrorView) {
            size = 1;
        }

        return size;
    }

    public void swap(ArrayList<VideoDictionary> dataArrayList) {
        try {
            this.dataArrayList = dataArrayList;
            if (dataArrayList.size() > 0) {
                isErrorView = false;
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_CELL_YT: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_yt_player, parent, false);
                return new YouTubeVideoViewHolder(context,manager, view);
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_player, parent, false);
                VideoViewHolder
                        viewHolder = new VideoViewHolder(view, context);

                return viewHolder;
            }

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mVHolder, int position) {

        if (mVHolder instanceof YouTubeVideoViewHolder) {
            populateYTAdapterView((YouTubeVideoViewHolder) mVHolder, position);
        } else {
            populateAdapterView((VideoViewHolder) mVHolder, position);

        }

    }

    private void populateYTAdapterView(YouTubeVideoViewHolder viewHolders, int position) {
        try {
            viewHolders.bind(getItem(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        try {
            if (holder instanceof VideoViewHolder)
                ((VideoViewHolder) holder).onRecycled();
            else
                ((YouTubeVideoViewHolder) holder).onRecycled();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static abstract class CallbackVideo {

        public abstract void onItemClick(@NonNull VideoViewHolder viewHolder, @NonNull View view,
                @NonNull VideoDictionary item, int position);
    }

    // Implement the CacheManager;
    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        return getItem(order);
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        return key instanceof VideoDictionary ? dataArrayList.indexOf(key) : null;
    }


    @NonNull
    @Override
    public Collection<ToroPlayer> select(@NonNull Container container,
            @NonNull List<ToroPlayer> items) {
        Collection<ToroPlayer> originalResult = origin.select(container, items);
        ArrayList<ToroPlayer> result = new ArrayList<>(originalResult);
        if (lastUserPause.get() >= 0) {
            for (Iterator<ToroPlayer> it = result.iterator(); it.hasNext(); ) {
                if (it.next().getPlayerOrder() == lastUserPause.get()) {
                    it.remove();
                    break;
                }
            }
        }

        return result;
    }

    @NonNull
    @Override
    public PlayerSelector reverse() {
        return origin.reverse();
    }


    private VideoDictionary getItem(final int position) {
        return dataArrayList.get(position);
    }

    @SuppressLint("DefaultLocale")
    private void populateAdapterView(VideoViewHolder viewHolders, final int position) {
        try {

            VideoDictionary videoDictionary = getItem(position);
            viewHolders.bind(videoDictionary, position);
            if (videoDictionary.isMute()) {
                viewHolders.imageView_sound.setImageResource(R.drawable.mute);
                viewHolders.getCurrentPlaybackInfo().setVolumeInfo(new VolumeInfo(true, 0.75f));
            } else {
                viewHolders.imageView_sound.setImageResource(R.drawable.unmute);
                viewHolders.getCurrentPlaybackInfo().setVolumeInfo(new VolumeInfo(false, 1.0f));
            }

            viewHolders.imageView_sound
                    .setOnClickListener(new ClickHandler(videoDictionary, viewHolders, position));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ClickHandler implements View.OnClickListener {

        int position;
        VideoViewHolder viewHolders;
        VideoDictionary videoDictionary;

        public ClickHandler(VideoDictionary videoDictionary,
                VideoViewHolder viewHolders,
                int position) {
            this.position = position;
            this.videoDictionary = videoDictionary;
            this.viewHolders = viewHolders;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView_sound:
                    ImageView imageView = (ImageView) v;
                    checkAndSetMute(videoDictionary, viewHolders, imageView);
                    break;
            }
        }

    }

    private void checkAndSetMute(VideoDictionary videoDictionary,
            VideoViewHolder viewHolders, ImageView imageView) {
        if (videoDictionary.isMute()) {
            imageView.setImageResource(R.drawable.unmute);
            videoDictionary.setMute(false);
            viewHolders.helper.setVolumeInfo(new VolumeInfo(false, 1.0f));
        } else {
            imageView.setImageResource(R.drawable.mute);
            videoDictionary.setMute(true);
            viewHolders.helper.setVolumeInfo(new VolumeInfo(true, 0.75f));
        }
    }


}
