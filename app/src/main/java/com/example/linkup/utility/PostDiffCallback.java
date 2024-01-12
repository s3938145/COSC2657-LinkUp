package com.example.linkup.utility;

import androidx.recyclerview.widget.DiffUtil;

import com.example.linkup.model.Post;

import java.util.List;

public class PostDiffCallback extends DiffUtil.Callback {

    private final List<Post> oldPosts;
    private final List<Post> newPosts;

    public PostDiffCallback(List<Post> oldPosts, List<Post> newPosts) {
        this.oldPosts = oldPosts;
        this.newPosts = newPosts;
    }

    @Override
    public int getOldListSize() {
        return oldPosts.size();
    }

    @Override
    public int getNewListSize() {
        return newPosts.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldPosts.get(oldItemPosition).getPostId().equals(newPosts.get(newItemPosition).getPostId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Post oldPost = oldPosts.get(oldItemPosition);
        Post newPost = newPosts.get(newItemPosition);

        if (!oldPost.getPostContent().equals(newPost.getPostContent())) return false;
        if (oldPost.getPostDate() != newPost.getPostDate()) return false;
        if (oldPost.getPostLikes() != newPost.getPostLikes()) return false;
        return oldPost.getLikedByUsers().equals(newPost.getLikedByUsers());
    }
}

