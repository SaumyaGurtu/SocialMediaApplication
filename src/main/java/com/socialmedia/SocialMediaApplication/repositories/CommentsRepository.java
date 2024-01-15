package com.socialmedia.SocialMediaApplication.repositories;

import com.socialmedia.SocialMediaApplication.models.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comments, String> {

    Page<Comments> findAllByParentId(String id, Pageable paging);

    Page<Comments> findAllByIdIn(List<String> replies, Pageable paging);

}
