package com.youtubeApi.youtubeApi.service;

import com.youtubeApi.youtubeApi.model.YoutubeVideo;

import java.util.List;

public interface YoutubeAPIService {

    List<YoutubeVideo> getListVideosBySearch(String searchValue);
}
