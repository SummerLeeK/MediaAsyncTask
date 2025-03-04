package com.example.lee.myapplication;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by lee on 2018/1/5.
 */

public class MediaAsyncTask extends AsyncTask<MediaParams, Integer, Void> {


    private onDealMediaListener listener;

    public void setListener(onDealMediaListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.onStart();
        }
    }

    @Override
    protected Void doInBackground(MediaParams... mediaParams) {
//||mediaParams[0].getPath().endsWith("")||mediaParams[0].getPath().endsWith("m4a")
        if (mediaParams[0].getAudioExtractor() != null) {
            SynthesisVideo(mediaParams[0]);
        } else if (mediaParams[0].getPath().endsWith("mp3")) {
            DetachAudio(mediaParams[0]);
        } else {
            DetachVideo(mediaParams[0]);
        }
        return null;

    }

    interface onDealMediaListener {
        void onStart();

        void onProgress(float present);

        void onFinsh();

        void onFailure(String message);
    }

    /**
     * 分离音频
     *
     * @param mediaParams1
     */
    private void DetachAudio(MediaParams mediaParams1) {
        MediaExtractor mediaExtractor = mediaParams1.getVideoExtractor();
        String path = mediaParams1.getPath();
        int format = mediaParams1.getFormat();
        int indexAudio = mediaParams1.getIndexVideo();

        MediaMuxer muxers = null;
        try {
            muxers = new MediaMuxer(path, format);

            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(indexAudio);

//            int video_frame_rate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
            long size = mediaFormat.getLong("durationUs");
            long sum = 0;
            mediaExtractor.selectTrack(indexAudio);
            int writeAudioIndex = muxers.addTrack(mediaFormat);
            muxers.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(100 * 1024);

            long stampTime = 0;
            //获取帧之间的间隔时间
            {
                mediaExtractor.readSampleData(byteBuffer, 0);
                if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    mediaExtractor.advance();
                }
                mediaExtractor.readSampleData(byteBuffer, 0);
                long secondTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long thirdTime = mediaExtractor.getSampleTime();
                stampTime = Math.abs(thirdTime - secondTime);
                Log.e("fuck", stampTime + "");
            }

            mediaExtractor.unselectTrack(indexAudio);
            mediaExtractor.selectTrack(indexAudio);
            if (muxers != null) {


                if (indexAudio != -1) {

                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                    info.presentationTimeUs = 0;


                    while (true) {

                        int sampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                        if (sampleSize < 0) {
                            break;
                        }

                        mediaExtractor.advance();

                        info.offset = 0;
                        info.size = sampleSize;
                        info.flags = mediaExtractor.getSampleFlags();
                        info.presentationTimeUs += stampTime;
                        muxers.writeSampleData(writeAudioIndex, byteBuffer, info);

                        sum += stampTime;

                        if (listener != null) {
                            float present = (float) ((sum * 100.0) / size);
                            listener.onProgress(present);
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }

        // 释放MediaMuxer
        muxers.stop();
        muxers.release();

        mediaExtractor.release();
        if (listener != null) {
            listener.onFinsh();
        }


    }

    /**
     * 分离视频
     *
     * @param mediaParams1
     */
    private void DetachVideo(MediaParams mediaParams1) {

        MediaExtractor mediaExtractor = mediaParams1.getVideoExtractor();
        String path = mediaParams1.getPath();
        int format = mediaParams1.getFormat();
        int indexVideo = mediaParams1.getIndexVideo();

        MediaMuxer muxers = null;
        try {
            muxers = new MediaMuxer(path, format);


            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(indexVideo);

            int video_frame_rate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
            long size = mediaFormat.getLong("durationUs");
            long sum = 0;
            mediaExtractor.selectTrack(indexVideo);
            int writeVideoIndex = muxers.addTrack(mediaFormat);

            if (muxers != null) {


                muxers.start();

                if (indexVideo != -1) {

                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                    info.presentationTimeUs = 0;
                    ByteBuffer byteBuffer = ByteBuffer.allocate(100 * 1024);

                    while (true) {

                        int sampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                        if (sampleSize < 0) {
                            break;
                        }

                        mediaExtractor.advance();

                        info.offset = 0;
                        info.size = sampleSize;
                        info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                        info.presentationTimeUs += 1000 * 1000 / video_frame_rate;
                        muxers.writeSampleData(writeVideoIndex, byteBuffer, info);

                        sum += 1000 * 1000 / video_frame_rate;

                        if (listener != null) {
                            float present = (float) ((sum * 100.0) / size);
                            listener.onProgress(present);
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }

        // 释放MediaMuxer
        muxers.stop();
        muxers.release();

        mediaExtractor.release();
        if (listener != null) {
            listener.onFinsh();
        }

    }


    private void SynthesisVideo(MediaParams params) {


        MediaExtractor videoExtraor = params.getVideoExtractor();
        MediaExtractor audioExtraor = params.getAudioExtractor();

        int videoIndex = params.getIndexVideo();
        int audioIndex = params.getIndexAudio();

        int format = params.getFormat();
        String outputpath = params.getPath();

        videoExtraor.selectTrack(videoIndex);
        audioExtraor.selectTrack(audioIndex);

        long sizeVideo = videoExtraor.getTrackFormat(videoIndex).getLong("durationUs");
        long sizeAudio = audioExtraor.getTrackFormat(audioIndex).getLong("durationUs");


        long sumSize = sizeAudio + sizeVideo;

        long cur_porgress = 0;
        try {
            MediaMuxer muxer = new MediaMuxer(outputpath, format);


            int writeVideoIndex = muxer.addTrack(videoExtraor.getTrackFormat(videoIndex));
            int writeAudioIndex = muxer.addTrack(audioExtraor.getTrackFormat(audioIndex));

            muxer.start();

            ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
            long sampleVideoTime;
            long sampleAudioTime;
            {

                videoExtraor.readSampleData(buffer, 0);
                if (videoExtraor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    videoExtraor.advance();
                }

                videoExtraor.readSampleData(buffer, 0);
                long secondTime = videoExtraor.getSampleTime();
                videoExtraor.advance();
                long thirdTime = videoExtraor.getSampleTime();
                sampleVideoTime = Math.abs(thirdTime - secondTime);
            }


            {

                audioExtraor.readSampleData(buffer, 0);
                if (audioExtraor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                    audioExtraor.advance();
                }

                audioExtraor.readSampleData(buffer, 0);
                long secondTime = audioExtraor.getSampleTime();
                audioExtraor.advance();
                long thirdTime = audioExtraor.getSampleTime();
                sampleAudioTime = Math.abs(thirdTime - secondTime);
            }


            videoExtraor.unselectTrack(videoIndex);
            videoExtraor.selectTrack(videoIndex);

            while (true) {

                int sampleSize = videoExtraor.readSampleData(buffer, 0);
                if (sampleSize < 0) {
                    break;
                }

                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                info.offset = 0;
                info.size = sampleSize;
                info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                info.presentationTimeUs += videoExtraor.getSampleTime();

                muxer.writeSampleData(writeVideoIndex, buffer, info);

                Log.e("MediaAsync", "video\t" + info.presentationTimeUs + "" + videoExtraor.getSampleTime());
                cur_porgress += sampleVideoTime;

                videoExtraor.advance();

                if (listener != null) {
                    listener.onProgress((float) (cur_porgress * 100.0 / sumSize));
                }

            }


            while (true) {
                int sampleSize = audioExtraor.readSampleData(buffer, 0);
                if (sampleSize < 0) {
                    break;
                }

                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                info.offset = 0;
                info.size = sampleSize;
                info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                info.presentationTimeUs += audioExtraor.getSampleTime();

                muxer.writeSampleData(writeAudioIndex, buffer, info);

                cur_porgress += sampleAudioTime;

                audioExtraor.advance();
                Log.e("MediaAsync", "audio\t" + info.presentationTimeUs + "" + videoExtraor.getSampleTime());

                if (listener != null) {
                    listener.onProgress((float) (cur_porgress * 100.0 / sumSize));
                }

            }


            muxer.stop();
            muxer.release();
            videoExtraor.release();
            audioExtraor.release();


            if (listener != null) {
                listener.onFinsh();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
