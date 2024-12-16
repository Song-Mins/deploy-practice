package com.example.deploy.domain.comment.model.response;


import com.example.deploy.global.model.response.PagedResponse;
import java.util.List;

public record CommentsAndRepliesResponse(
	PagedResponse<CommentResponse> comments, List<CommentResponse> replies) {}
