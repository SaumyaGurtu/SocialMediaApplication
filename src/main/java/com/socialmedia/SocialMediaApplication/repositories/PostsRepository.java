package com.socialmedia.SocialMediaApplication.repositories;

import com.socialmedia.SocialMediaApplication.models.Posts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostsRepository extends MongoRepository<Posts, String> {
}
