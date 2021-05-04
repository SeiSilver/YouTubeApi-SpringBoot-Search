package com.youtubeApi.youtubeApi.controller;

import com.youtubeApi.youtubeApi.model.YoutubeVideo;
import com.youtubeApi.youtubeApi.service.YoutubeAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BaseController {

    @Autowired
    private YoutubeAPIService youtubeAPIService;

    @GetMapping("/")
    public String showHome() {

        return "index";
    }

    @GetMapping("/search")
    public String showHome(Model model, @RequestParam(name = "query",defaultValue = "music") String query) {

        List<YoutubeVideo> videos = youtubeAPIService.getListVideosBySearch(query);

        model.addAttribute("list",videos);

        return "index";
    }

}
