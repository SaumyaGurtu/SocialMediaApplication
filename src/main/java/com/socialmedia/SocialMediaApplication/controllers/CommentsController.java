package com.socialmedia.SocialMediaApplication.controllers;

import com.socialmedia.SocialMediaApplication.dto.CommentDto;
import com.socialmedia.SocialMediaApplication.models.Comments;
import com.socialmedia.SocialMediaApplication.controller.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable("id") String id)
    {
        Comments comment = commentsService.getCommentById(id);
        if (comment == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    // get latest 10 comments first for a parent comment
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getCommentsByParentCommentId(@PathVariable("id") String id, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit)
    {
        Pageable paging = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "lastUpdated"));
        Page<Comments> response = commentsService.getAllCommentsByParentCommentId(id, paging);
        if (response == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // get 10 users that liked or disliked the comment - oldest first
    @GetMapping("/{id}/users/{activity}") // activity value -> like or dislike
    public ResponseEntity<?> getUsersByActivity(@PathVariable("id") String id, @PathVariable("activity") String activity, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit)
    {
        List<String> userIds = commentsService.getUsersByActivityOnComment(id, activity, offset, limit);
        if (userIds == null || userIds.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(userIds, HttpStatus.OK);
    }

    // add a reply to a comment
    @PostMapping("/{id}/reply")
    public ResponseEntity<?> addReply(@PathVariable("id") String id, @RequestBody CommentDto commentDto)
    {
        Comments comment = commentsService.addCommentToComment(id, commentDto);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // like a comment
    // dislike a comment
    @PutMapping("/{id}/{activity}/userId/{userId}")
    public ResponseEntity<?> updateUserActivity(@PathVariable("id") String id, @PathVariable("activity") String activity, @PathVariable("userId") String userId)
    {
        Comments updatedComment = commentsService.updateUserActivity(id, activity, userId);
        if (updatedComment == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(updatedComment,HttpStatus.ACCEPTED);
    }
}
