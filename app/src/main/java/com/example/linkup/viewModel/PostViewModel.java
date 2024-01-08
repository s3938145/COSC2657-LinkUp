package com.example.linkup.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkup.model.Post;
import com.example.linkup.repository.PostRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.linkup.model.Post;
import com.example.linkup.repository.PostRepository;

import java.util.List;

public class PostViewModel extends ViewModel {

    private PostRepository postRepository;
    private MutableLiveData<List<Post>> postsLiveData;
    private MutableLiveData<String> errorMessage;
    private long lastLoadedPostDate = Long.MAX_VALUE; // Initially load the newest posts
    private static final int POST_LOAD_LIMIT = 10; // Number of posts to load each time

    public PostViewModel() {
        postRepository = new PostRepository();
        postsLiveData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        loadPosts();
    }

    public void loadPosts() {
        postRepository.getAllPosts(new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {
                if (!posts.isEmpty()) {
                    lastLoadedPostDate = posts.get(posts.size() - 1).getPostDate();
                }
                postsLiveData.postValue(posts);
            }

            @Override
            public void DataIsInserted() {
                // Not used here
            }

            @Override
            public void DataIsUpdated() {
                // Not used here
            }

            @Override
            public void DataIsDeleted() {
                // Not used here
            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        }, lastLoadedPostDate, POST_LOAD_LIMIT);
    }

    public void addPost(Post post) {
        postRepository.addPost(post, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {
                // Not used here
            }

            @Override
            public void DataIsInserted() {
                getAllPosts(); // Refresh the list after adding a post
            }

            @Override
            public void DataIsUpdated() {
                // Not used for addPost
            }

            @Override
            public void DataIsDeleted() {
                // Not used for addPost
            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void updatePost(Post post) {
        postRepository.updatePost(post, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {
                // Not used for updatePost
            }

            @Override
            public void DataIsInserted() {
                // Not used for updatePost
            }

            @Override
            public void DataIsUpdated() {
                getAllPosts(); // Refresh the list after updating a post
            }

            @Override
            public void DataIsDeleted() {
                // Not used for updatePost
            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void deletePost(String postId) {
        postRepository.deletePost(postId, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {
                // Not used for deletePost
            }

            @Override
            public void DataIsInserted() {
                // Not used for deletePost
            }

            @Override
            public void DataIsUpdated() {
                // Not used for deletePost
            }

            @Override
            public void DataIsDeleted() {
                getAllPosts(); // Refresh the list after deleting a post
            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    public void getAllPosts() {
        postRepository.getAllPosts(new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {
                if (!posts.isEmpty()) {
                    lastLoadedPostDate = posts.get(posts.size() - 1).getPostDate();
                    postsLiveData.postValue(posts);
                }
            }

            @Override
            public void DataIsInserted() {
                // Not used here
            }

            @Override
            public void DataIsUpdated() {
                // Not used here
            }

            @Override
            public void DataIsDeleted() {
                // Not used here
            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        }, lastLoadedPostDate, POST_LOAD_LIMIT);
    }

    public LiveData<List<Post>> getPostsLiveData() {
        return postsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}




