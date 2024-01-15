package com.socialmedia.SocialMediaApplication.controllers;

import com.socialmedia.SocialMediaApplication.dto.CommentDto;
import com.socialmedia.SocialMediaApplication.dto.PostDto;
import com.socialmedia.SocialMediaApplication.models.Comments;
import com.socialmedia.SocialMediaApplication.models.Posts;
import com.socialmedia.SocialMediaApplication.controller.CommentsService;
import com.socialmedia.SocialMediaApplication.controller.PostsService;
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
@RequestMapping(path = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostsController {

    @Autowired
    PostsService postsService;
    @Autowired
    CommentsService commentsService;

    @GetMapping("/home")
    public ResponseEntity<?> hello()
    {
        return new ResponseEntity<>("Welcome to my social media application!" ,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit)
    {
        Pageable paging = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "lastUpdated"));
        Page<Posts> response = postsService.getAllPosts(paging);
        if (response == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") String id)
    {
        Posts post = postsService.getPostById(id);
        if (post == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    // get latest 10 comments first for a post
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable("id") String id, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit)
    {
        Pageable paging = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "lastUpdated"));
        Page<Comments> response = postsService.getAllCommentsByPostId(id, paging);
        if (response == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // create post
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto)
    {
        Posts post = postsService.createPost(postDto);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    // addComment to post
    @PostMapping("/{id}/reply")
    public ResponseEntity<?> addReply(@PathVariable("id") String id, @RequestBody CommentDto commentDto)
    {
        Comments comment = postsService.addCommentToPost(id, commentDto);
        if (comment == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    // like post
    // dislike post
    @PutMapping("/{id}/{activity}/userId/{userId}")
    public ResponseEntity<?> updateUserActivity(@PathVariable("id") String id, @PathVariable("activity") String activity, @PathVariable("userId") String userId)
    {
        Posts updatedPost = postsService.updateUserActivity(id, activity, userId);
        if (updatedPost == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(updatedPost,HttpStatus.ACCEPTED);
    }

    // get 10 users that liked or disliked the post - oldest first
    @GetMapping("/{id}/users/{activity}") // activity value -> like or dislike
    public ResponseEntity<?> getUsersByActivity(@PathVariable("id") String id, @PathVariable("activity") String activity, @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit)
    {
        List<String> userIds = postsService.getUsersByActivityOnPost(id, activity, offset, limit);
        if (userIds == null || userIds.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(userIds, HttpStatus.OK);
    }
}
