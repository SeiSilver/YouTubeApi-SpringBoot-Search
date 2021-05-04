package com.youtubeApi.youtubeApi.model;

import lombok.Data;

@Data
public class Channel {

    private String channelId;

    private String channelTitle;

    private String channelIcon;

    private long subscriberCount;

}
