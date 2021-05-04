package com.youtubeApi.youtubeApi.model;

import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
public class YoutubeVideo {

    private String videoId;

    private String thumbnail;

    private String title;

    private String videoUrl;

    private Timestamp publishedAt;

    private Channel channel;

    // cái này để format lại ngày tháng theo định dạng mong muốn
    public String getPublishedAtToString() {
        String formattedDate = new SimpleDateFormat("MMM dd, yyyy | HH:mm:ss").format(publishedAt);
        return formattedDate;
    }

}
