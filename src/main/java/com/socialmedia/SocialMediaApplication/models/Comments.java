package com.socialmedia.SocialMediaApplication.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "comments")
public class Comments {
    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String postId;
    @Indexed
    private String parentId; // just one level up comment parent
    private String content;
    private List<UserActivity> likesList = new ArrayList<>(); // user id -> like
    private List<UserActivity> dislikesList = new ArrayList<>(); // user id -> dislike
    private List<String> replies = new ArrayList<>(); // comment ids
    @CreatedDate
    private Long dateCreated;
    @LastModifiedDate
    private Long lastUpdated;
    @Version
    private Integer version;

}
