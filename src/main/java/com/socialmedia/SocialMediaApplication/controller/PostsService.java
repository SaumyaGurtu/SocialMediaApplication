package com.socialmedia.SocialMediaApplication.controller;

import com.socialmedia.SocialMediaApplication.dto.CommentDto;
import com.socialmedia.SocialMediaApplication.dto.PostDto;
import com.socialmedia.SocialMediaApplication.models.Comments;
import com.socialmedia.SocialMediaApplication.models.Posts;
import com.socialmedia.SocialMediaApplication.models.Reaction;
import com.socialmedia.SocialMediaApplication.models.UserActivity;
import com.socialmedia.SocialMediaApplication.repositories.CommentsRepository;
import com.socialmedia.SocialMediaApplication.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostsService {
    @Autowired
    PostsRepository postsRepository;
    @Autowired
    CommentsRepository commentsRepository;

    public Posts createPost(PostDto postDto) {
        // validate post request
        // convert PostDto to Post collection obj
        Posts post = new Posts();
        post.setContent(postDto.getContent());
        post.setUserId(postDto.getUserId());
        Long now = System.currentTimeMillis();
        post.setDateCreated(now);
        post.setLastUpdated(now);
        return postsRepository.save(post);
    }

    public Comments addCommentToPost(String postId, CommentDto commentDto) {
        Optional<Posts> postEntity = postsRepository.findById(postId);
        if (postEntity.isPresent()) {
            // validate comment
            Comments comment = new Comments();
            comment.setContent(commentDto.getContent());
            comment.setUserId(commentDto.getUserId());
            comment.setPostId(postId);
            Long now = System.currentTimeMillis();
            comment.setDateCreated(now);
            comment.setLastUpdated(now);
            Comments savedEntity = commentsRepository.save(comment);
            Posts postToUpdate = postEntity.orElseThrow();
            postToUpdate.getReplies().add(savedEntity.getId());
            postsRepository.save(postToUpdate);
            return savedEntity;
        }
        return null;
    }

    public Posts updateUserActivity(String postId, String activity, String userId) {
        Optional<Posts> postEntity = postsRepository.findById(postId);
        if (postEntity.isPresent()) {
            Posts postToUpdate = postEntity.orElseThrow();
            UserActivity userActivity = new UserActivity();
            userActivity.setUserId(userId);
            if (activity.equals("like"))
                userActivity.setActivityType(Reaction.LIKE);
            else if (activity.equals("dislike"))
                userActivity.setActivityType(Reaction.DISLIKE);
            else
                return null;
            userActivity.setDateCreated(System.currentTimeMillis());
            if (activity.equals("like")) {
                for (UserActivity like : postToUpdate.getLikesList()) {
                    if (like.getUserId().equals(userId)) {
                        return postToUpdate;
                    }
                }
                UserActivity dislikeByUserId = null;
                for (UserActivity dislike : postToUpdate.getDislikesList()) {
                    if (dislike.getUserId().equals(userId)) {
                        dislikeByUserId = dislike;
                    }
                }
                if (dislikeByUserId != null)
                    postToUpdate.getDislikesList().remove(dislikeByUserId);
                postToUpdate.getLikesList().add(userActivity);
            }
            else {
                for (UserActivity dislike : postToUpdate.getDislikesList()) {
                    if (dislike.getUserId().equals(userId)) {
                        return postToUpdate;
                    }
                }
                UserActivity likeByUserId = null;
                for (UserActivity like : postToUpdate.getLikesList()) {
                    if (like.getUserId().equals(userId)) {
                        likeByUserId = like;
                    }
                }
                if (likeByUserId != null)
                    postToUpdate.getLikesList().remove(likeByUserId);
                postToUpdate.getDislikesList().add(userActivity);
            }
            return postsRepository.save(postToUpdate);
        }
        return null;
    }

    public Posts getPostById(String id) {
        return postsRepository.findById(id).orElseThrow();
    }

    public Page<Comments> getAllCommentsByPostId(String postId, Pageable paging) {
        Optional<Posts> postEntity = postsRepository.findById(postId);
        if (!postEntity.isPresent()) return null;
        return commentsRepository.findAllByIdIn(postEntity.orElseThrow().getReplies(), paging);
    }

    public List<String> getUsersByActivityOnPost(String id, String activity, int offset, int limit) {
        Optional<Posts> postEntity = postsRepository.findById(id);
        if (!postEntity.isPresent()) return null;
        List<UserActivity> userActivityList;
        if (activity.equals("like"))
            userActivityList = postEntity.orElseThrow().getLikesList();
        else if (activity.equals("dislike"))
            userActivityList = postEntity.orElseThrow().getDislikesList();
        else
            userActivityList = new ArrayList<>();

        if (userActivityList.isEmpty()) return new ArrayList<>();

        List<String> users = new ArrayList<>();
        int startPage = offset*limit;
        for (int i=startPage; i<startPage+limit && i<userActivityList.size(); i++) {
            users.add(userActivityList.get(i).getUserId());
        }
        return users;
    }

    public Page<Posts> getAllPosts(Pageable paging) {
        return postsRepository.findAll(paging);
    }
}
