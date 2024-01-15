package com.socialmedia.SocialMediaApplication.models;

import lombok.Data;

@Data
public class UserActivity {
//    private String entityId; // post id or comment id
//    private String entityType; // post or comment
    private String userId;
    private Reaction activityType;
    private Long dateCreated;
}
