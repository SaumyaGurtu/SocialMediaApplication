package com.socialmedia.SocialMediaApplication;

import com.socialmedia.SocialMediaApplication.dto.CommentDto;
import com.socialmedia.SocialMediaApplication.dto.PostDto;
import com.socialmedia.SocialMediaApplication.models.Comments;
import com.socialmedia.SocialMediaApplication.models.Posts;
import com.socialmedia.SocialMediaApplication.controller.CommentsService;
import com.socialmedia.SocialMediaApplication.controller.PostsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.Queue;

@SpringBootTest
public class ScaleCreationTest {

    @Autowired
    PostsService postsService;
    @Autowired
    CommentsService commentsService;

    @Test
    void testSetCreation() {
        Posts post = postsService.createPost(new PostDto("saumya", "Today is a good day."));
        // 1000s of comments in table
        Queue<String> commentIds = new LinkedList<>();
        for (int level=1; level<5; level++) {
            for (int child = 1; child < 50; child++) {
                String parentId;
                Comments comment;
                if (level == 1) {
                    parentId = null;
                    comment = postsService.addCommentToPost(post.getId(), new CommentDto(post.getUserId(), post.getId(), parentId, "Today is a good day level: " + Integer.toString(level) + " child: " + Integer.toString(child)));
                } else {
                    parentId = commentIds.poll();
                    comment = commentsService.addCommentToComment(parentId, new CommentDto(post.getUserId(), post.getId(), parentId, "Today is a good day level: " + Integer.toString(level) + " child: " + Integer.toString(child)));
                }
                commentIds.add(comment.getId());
            }
        }
        // 100s of nesting levels in comments
        String parentId = null;
        for (int level=1; level<100; level++) {
            Comments comment = commentsService.addCommentToComment(parentId, new CommentDto(post.getUserId(), post.getId(), parentId, "Today is a good day level: " + Integer.toString(level) + " child: " + Integer.toString(1)));
            parentId = comment.getId();
        }
    }
}
