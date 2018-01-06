package com.example.lee.myapplication;

import android.media.MediaExtractor;

/**
 * Created by lee on 2018/1/5.
 */

public class MediaParams {

    private MediaExtractor videoExtractor;
    private MediaExtractor audioExtractor;
    private String path;
    private int format;
    private int indexVideo;
    private int indexAudio;

    public MediaParams(MediaExtractor videoExtractor, String path, int format, int indexVideo) {
        this.videoExtractor = videoExtractor;
        this.path = path;
        this.format = format;
        this.indexVideo = indexVideo;
    }


    public MediaParams(MediaExtractor videoExtractor, MediaExtractor audioExtractor, String path, int format, int indexVideo, int indexAudio) {
        this.videoExtractor = videoExtractor;
        this.audioExtractor = audioExtractor;
        this.path = path;
        this.format = format;
        this.indexVideo = indexVideo;
        this.indexAudio = indexAudio;
    }


    public MediaExtractor getVideoExtractor() {
        return videoExtractor;
    }

    public void setVideoExtractor(MediaExtractor videoExtractor) {
        this.videoExtractor = videoExtractor;
    }

    public MediaExtractor getAudioExtractor() {
        return audioExtractor;
    }

    public void setAudioExtractor(MediaExtractor audioExtractor) {
        this.audioExtractor = audioExtractor;
    }

    public int getIndexAudio() {
        return indexAudio;
    }

    public void setIndexAudio(int indexAudio) {
        this.indexAudio = indexAudio;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getIndexVideo() {
        return indexVideo;
    }

    public void setIndexVideo(int indexVideo) {
        this.indexVideo = indexVideo;
    }
}
