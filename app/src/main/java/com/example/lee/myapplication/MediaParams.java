package com.example.lee.myapplication;

import android.media.MediaExtractor;

/**
 * Created by lee on 2018/1/5.
 */

public class MediaParams {

    private MediaExtractor mediaExtractor;
    private String path;
    private int format;
    private int indexVideo;

    public MediaParams(MediaExtractor mediaExtractor, String path, int format, int indexVideo) {
        this.mediaExtractor = mediaExtractor;
        this.path = path;
        this.format = format;
        this.indexVideo = indexVideo;
    }

    public MediaExtractor getMediaExtractor() {
        return mediaExtractor;
    }

    public void setMediaExtractor(MediaExtractor mediaExtractor) {
        this.mediaExtractor = mediaExtractor;
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
