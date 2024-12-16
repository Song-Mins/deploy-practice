package com.example.deploy.domain.post.event;


import com.example.deploy.domain.post.model.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostReadEvent {
    private final Post post;
}
