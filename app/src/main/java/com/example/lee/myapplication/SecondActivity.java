package com.example.lee.myapplication;

import android.app.ProgressDialog;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lee on 2018/1/4.
 */

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edit_cmd)
    EditText editCmd;
    @BindView(R.id.btn_do)
    Button btnDo;
    @BindView(R.id.command_output)
    LinearLayout commandOutput;

    private MediaCodec mediaCodec;
    private MediaCodecInfo mediaCodecInfo;
    private MediaExtractor mVideoExtractor;
    private MediaExtractor mAudioExtractor;
    private MediaFormat mVideoFormat;
    private MediaFormat mAudioFormat;
    private MediaMuxer muxer;
    private ProgressDialog progressDialog;

    private int video_frame_rate;
    private int indexVideo;
    private int indexAudio;

    private MediaAsyncTask asyncTask;
    private MediaParams mediaParams;


    private String inputVideoPath = Environment.getExternalStorageDirectory() + File.separator + "GALA.mp4";
    private String outputVideoPath = Environment.getExternalStorageDirectory() + File.separator + "output11.mp4";
    private String outputAudioPath = Environment.getExternalStorageDirectory() + File.separator + "output11.mp3";


    private void initUI() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(null);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMax(100);

        btnDo.setOnClickListener(this);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        initUI();

        mVideoExtractor = new MediaExtractor();

        asyncTask = new MediaAsyncTask();

        try {
            mVideoExtractor.setDataSource(inputVideoPath);
            TextView textView = new TextView(this);

            textView.setText("TrackCount" + mVideoExtractor.getTrackCount() + "\n"
                    + "SampleTime" + mVideoExtractor.getSampleTime() + "\n" +
                    "CachedDuration" + mVideoExtractor.getCachedDuration() + "\n");

            commandOutput.addView(textView);

        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < mVideoExtractor.getTrackCount(); i++) {
            String mime = mVideoExtractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                //视频
                mVideoFormat = mVideoExtractor.getTrackFormat(i);
                int width = mVideoFormat.getInteger("width");
                int height = mVideoFormat.getInteger("height");
                video_frame_rate = mVideoFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
                long duration = mVideoFormat.getLong("durationUs");
                Log.i("Video", "width" + width + "\theight" + height + "\tduration" + duration + "\tvideo_frame_rate" + video_frame_rate + "\t" + mVideoFormat.toString());

                indexVideo = i;
//                mVideoExtractor.selectTrack(indexVideo);


            } else if (mime.startsWith("audio/")) {

            }

        }


        mAudioExtractor = new MediaExtractor();
        try {
            mAudioExtractor.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "gala.m4a");

        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < mAudioExtractor.getTrackCount(); i++) {
            String mime = mAudioExtractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                //音频

                mAudioFormat = mAudioExtractor.getTrackFormat(i);
                int channel_count = mAudioFormat.getInteger("channel-count");
                int track_id = mAudioFormat.getInteger("track-id");
                int bitrate = mAudioFormat.getInteger("bitrate");
                int sample_rate = mAudioFormat.getInteger("sample-rate");
//                int max_bitrate = mediaFormat.getInteger("max-bitrate");
                long duration = mAudioFormat.getLong("durationUs");


                Log.i("AudioAudioAudio", "channel_count" + channel_count + "track_id" + track_id + "bitrate" + bitrate +
                        "sample_rate" + sample_rate + "duration" + duration);
                indexAudio = i;
            }
        }


        //开始分离视频
        mediaParams = new MediaParams(mVideoExtractor, mAudioExtractor, outputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4, indexVideo, indexAudio);

        asyncTask.setListener(new MediaAsyncTask.onDealMediaListener() {
            @Override
            public void onStart() {

                progressDialog.show();
                Log.e("MediaAsync", "onStart" + System.currentTimeMillis());
            }

            @Override
            public void onProgress(float present) {
                progressDialog.setProgress((int) present);
            }

            @Override
            public void onFinsh() {
                progressDialog.dismiss();
                Log.e("MediaAsync", "onFinsh" + System.currentTimeMillis());
            }

            @Override
            public void onFailure(String message) {
                Log.e("MediaAsync", "onFailure :" + message);
            }
        });


//        mediaCodec =MediaCodec.createDecoderByType()

    }


    @Override
    public void onClick(View v) {
        asyncTask.execute(mediaParams);
    }
}
