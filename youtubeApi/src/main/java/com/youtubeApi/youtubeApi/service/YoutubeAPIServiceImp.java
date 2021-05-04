package com.youtubeApi.youtubeApi.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.youtubeApi.youtubeApi.model.Channel;
import com.youtubeApi.youtubeApi.model.YoutubeVideo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class YoutubeAPIServiceImp implements YoutubeAPIService {

    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${youtube.apiKey}")
    private String apiKey;

    @Override
    public List<YoutubeVideo> getListVideosBySearch(String searchValue) {

        try {
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            }).setApplicationName("YoutubeAPISearch").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(apiKey);
            search.setQ(searchValue);
            search.setType("video");
            search.setMaxResults(50L);

            String[] randomOrder = {"date", "rating", "videoCount", "viewCount", "relevance"};
            String orderBy = randomOrder[new Random().nextInt(randomOrder.length)];
            search.setOrder(orderBy);

            List<SearchResult> searchResultList = search.execute().getItems();

//            for (int i = 0; i < 1; i++) {
//                SearchListResponse response = search.execute();
//                searchResultList.addAll(response.getItems());
//                search.setPageToken(response.getNextPageToken());
//            }

            if (!searchResultList.isEmpty()) {
                return customList(searchResultList);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        }

        return new ArrayList<>();
    }

    // format lại Object theo mong muốn
    private List<YoutubeVideo> customList(List<SearchResult> searchResultList) {

        List<YoutubeVideo> videos = new ArrayList<>();

        for (SearchResult singleVideo : searchResultList) {
            ResourceId rId = singleVideo.getId();
            if (rId.getKind().equals("youtube#video")) {
                YoutubeVideo youtubeVideo = new YoutubeVideo();

                Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("high");
                youtubeVideo.setVideoId(rId.getVideoId());

                youtubeVideo.setTitle(singleVideo.getSnippet().getTitle());
                youtubeVideo.setThumbnail(thumbnail.getUrl());
                youtubeVideo.setVideoUrl("https://www.youtube.com/watch?v=" + rId.getVideoId());
                LocalDateTime newDate = LocalDateTime.parse(singleVideo.getSnippet().getPublishedAt().toString(), DateTimeFormatter.ISO_DATE_TIME);
                Timestamp timestamp = Timestamp.valueOf(newDate);
                youtubeVideo.setPublishedAt(timestamp);
                singleVideo.getSnippet().getChannelId();
                // get channel
                Channel cn = getChannelByChannelId(singleVideo.getSnippet().getChannelId());
                youtubeVideo.setChannel(cn);
                // end

                videos.add(youtubeVideo);

            }
        }

        return videos;
    }

    // lấy thông tin channel của từng video
    private Channel getChannelByChannelId(String channelId) {

        try {

            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            }).setApplicationName("YoutubeApiChannelList").build();

            YouTube.Channels.List request = youtube.channels().list("snippet,statistics");
            request.setKey(apiKey);
            request.setMaxResults(1L);
            request.setId(channelId);

            ChannelListResponse response = request.execute();
            List<com.google.api.services.youtube.model.Channel> responseResultList = response.getItems();

            if (!responseResultList.isEmpty()) {
                var temp = responseResultList.get(0);
                Thumbnail thumbnail = (Thumbnail) responseResultList.get(0).getSnippet().getThumbnails().get("high");

                Channel newChannel = new Channel();
                newChannel.setChannelId(channelId);
                newChannel.setChannelTitle(temp.getSnippet().getTitle());
                newChannel.setChannelIcon(thumbnail.getUrl());

                BigInteger sub = temp.getStatistics().getSubscriberCount();
                newChannel.setSubscriberCount(sub != null ? sub.longValue() : 0);

                return newChannel;
            } else {
                return null;
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e + " : " + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        }

        return null;
    }

}
