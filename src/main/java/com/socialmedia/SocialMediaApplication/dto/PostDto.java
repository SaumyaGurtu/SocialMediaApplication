package com.socialmedia.SocialMediaApplication.dto;

import lombok.Data;

@Data
public class PostDto {

    private String userId;
    private String content;

    public PostDto(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }
}
