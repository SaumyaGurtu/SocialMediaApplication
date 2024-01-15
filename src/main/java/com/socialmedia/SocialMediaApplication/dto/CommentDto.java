package com.socialmedia.SocialMediaApplication.dto;

import lombok.Data;

@Data
public class CommentDto {
    private String userId;
    private String postId;
    private String parentId;
    private String content;

    public CommentDto(String userId, String postId, String parentId, String content) {
        this.userId = userId;
        this.postId = postId;
        this.parentId = parentId;
        this.content = content;
    }
}
