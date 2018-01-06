package com.example.lee.myapplication;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;

/**
 * Created by lee on 2018/1/5.
 */

public class MediaUtils {

    public static String VIDEO_TYPE = "video/";

    public static String AUDIO_TYPE = "audio/";


    public static MediaFormat getMediaFormat(MediaExtractor mediaExtractor, String type, String path) {

        MediaFormat mediaFormat = null;
        try {
            mediaExtractor.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
            if (mediaExtractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME).startsWith(type)) {
                mediaFormat = mediaExtractor.getTrackFormat(i);
            }
        }
        return mediaFormat;
    }
}
