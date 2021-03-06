package com.apollo.keyspirit.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apollo.keyspirit.R;
import com.apollo.keyspirit.widget.MyVideoView;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment implements View.OnClickListener {

    private MyVideoView mVideoView;
    private SurfaceView mSurfaceView;
    private int currentPosition;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    List<Animator> animatorList = new ArrayList<>();
    private ImageView mIvAnim;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.btn_start).setOnClickListener(this);
        view.findViewById(R.id.btn_stop).setOnClickListener(this);
        mIvAnim = view.findViewById(R.id.iv_anim);
        mVideoView = view.findViewById(R.id.video_view);
        mVideoView.setVideoURI(Uri.parse("http://alcdn.hls.xiaoka.tv/2017427/14b/7b3/Jzq08Sl8BbyELNTo/index.m3u8"));
        mVideoView.start();

        mSurfaceView = view.findViewById(R.id.surface_view);
        mediaPlayer = new MediaPlayer();
        mSurfaceView.getHolder().setKeepScreenOn(true);
        mSurfaceView.getHolder().addCallback(new SurfaceViewLis());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                ObjectAnimator alpha = ObjectAnimator.ofFloat(mIvAnim, "alpha", 1f, 0f);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(mIvAnim, "scaleX", 1.0f, 0.5f);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(5000);
                animatorList.add(set);
                set.play(alpha).with(scaleX);
                set.start();
                break;
            case R.id.btn_stop:
                for (Animator animator : animatorList) {
                    animator.end();
                }
                animatorList.clear();
                break;
        }

    }

    private class SurfaceViewLis implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (currentPosition >= 0) {
                // ??????SurfaceHolder???????????????????????????????????????????????????????????????????????????????????????
                video_play(currentPosition);
                currentPosition = 0;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // ??????SurfaceHolder???????????????????????????????????????????????????
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }

        }
    }

    /**
     * ????????????
     *
     * @param msec ??????????????????
     */
    protected void video_play(final int msec) {
//      // ????????????????????????
        try {
            mediaPlayer = new MediaPlayer();
            //?????????????????????
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // ????????????????????????
            AssetFileDescriptor fd = this.getActivity().getAssets().openFd("funny.mp4");
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(),
                    fd.getLength());
            // ?????????????????????SurfaceHolder
            mediaPlayer.setDisplay(mSurfaceView.getHolder());//????????????????????????????????????????????????SurfaceView???????????????setDisplay?????????

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();

//                    // ????????????????????????
//                    mediaPlayer.seekTo(msec);
//                    // ???????????????????????????????????????????????????????????????
//                    seekBar.setMax(mediaPlayer.getDuration());
//                    // ???????????????????????????????????????
//                    new Thread() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                isPlaying = true;
//                                while (isPlaying) {
//                                    int current = mediaPlayer
//                                            .getCurrentPosition();
//                                    seekBar.setProgress(current);
//
//                                    sleep(500);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();

//                    start.setEnabled(false);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // ????????????????????????
//                    start.setEnabled(true);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // ????????????????????????
                    video_play(0);
                    isPlaying = false;
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
