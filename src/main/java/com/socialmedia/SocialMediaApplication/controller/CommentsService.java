package com.socialmedia.SocialMediaApplication.controller;

import com.socialmedia.SocialMediaApplication.dto.CommentDto;
import com.socialmedia.SocialMediaApplication.models.Comments;
import com.socialmedia.SocialMediaApplication.models.Reaction;
import com.socialmedia.SocialMediaApplication.models.UserActivity;
import com.socialmedia.SocialMediaApplication.repositories.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentsService {

    @Autowired
    CommentsRepository commentsRepository;

    public Page<Comments> getAllCommentsByParentCommentId(String id, Pageable paging) {
        return commentsRepository.findAllByParentId(id, paging);
    }

    public List<String> getUsersByActivityOnComment(String id, String activity, int offset, int limit) {
        Optional<Comments> commentEntity = commentsRepository.findById(id);
        if (!commentEntity.isPresent()) return null;
        List<UserActivity> userActivityList;
        if (activity.equals("like"))
            userActivityList = commentEntity.orElseThrow().getLikesList();
        else if (activity.equals("dislike"))
            userActivityList = commentEntity.orElseThrow().getDislikesList();
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

    public Comments addCommentToComment(String id, CommentDto commentDto) {
        Optional<Comments> commentEntity = commentsRepository.findById(id);
        if (commentEntity.isPresent()) {
            Comments commentToUpdate = commentEntity.orElseThrow();
            // validate comment
            Comments comment = new Comments();
            comment.setContent(commentDto.getContent());
            comment.setUserId(commentDto.getUserId());
            comment.setPostId(commentToUpdate.getPostId());
            comment.setParentId(commentToUpdate.getId());
            Long now = System.currentTimeMillis();
            comment.setDateCreated(now);
            comment.setLastUpdated(now);
            Comments savedEntity = commentsRepository.save(comment);

            commentToUpdate.getReplies().add(savedEntity.getId());
            commentsRepository.save(commentToUpdate);
            return savedEntity;
        }
        return null;
    }

    public Comments updateUserActivity(String id, String activity, String userId) {
        Optional<Comments> commentEntity = commentsRepository.findById(id);
        if (commentEntity.isPresent()) {
            Comments commentToUpdate = commentEntity.orElseThrow();
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
                for (UserActivity like : commentToUpdate.getLikesList()) {
                    if (like.getUserId().equals(userId)) {
                        return commentToUpdate;
                    }
                }
                UserActivity dislikeByUserId = null;
                for (UserActivity dislike : commentToUpdate.getDislikesList()) {
                    if (dislike.getUserId().equals(userId)) {
                        dislikeByUserId = dislike;
                    }
                }
                if (dislikeByUserId != null)
                    commentToUpdate.getDislikesList().remove(dislikeByUserId);
                commentToUpdate.getLikesList().add(userActivity);
            }
            else {
                for (UserActivity dislike : commentToUpdate.getDislikesList()) {
                    if (dislike.getUserId().equals(userId)) {
                        return commentToUpdate;
                    }
                }
                UserActivity likeByUserId = null;
                for (UserActivity like : commentToUpdate.getLikesList()) {
                    if (like.getUserId().equals(userId)) {
                        likeByUserId = like;
                    }
                }
                if (likeByUserId != null)
                    commentToUpdate.getLikesList().remove(likeByUserId);
                commentToUpdate.getDislikesList().add(userActivity);
            }
            return commentsRepository.save(commentToUpdate);
        }
        return null;
    }

    public Comments getCommentById(String id) {
        return commentsRepository.findById(id).orElseThrow();
    }
}
