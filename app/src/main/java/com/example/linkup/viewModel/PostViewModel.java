package com.example.linkup.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.linkup.model.Post;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.service.FirebaseService;
import java.util.List;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.linkup.model.Post;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.service.FirebaseService;
import java.util.List;

public class PostViewModel extends AndroidViewModel {

    private final PostRepository postRepository;
    private final MutableLiveData<List<Post>> postsLiveData;
    private final MutableLiveData<String> errorMessage;
    private long lastLoadedPostDate = Long.MAX_VALUE;
    private static final int POST_LOAD_LIMIT = 10;

    public PostViewModel(@NonNull Application application) {
        super(application);
        FirebaseService firebaseService = new FirebaseService(application.getApplicationContext());
        postRepository = new PostRepository(firebaseService);
        postsLiveData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        loadPosts();
    }

    public void loadPosts() {
        postRepository.getAllPosts(new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {
                if (posts.isEmpty()) {
                    lastLoadedPostDate = posts.get(posts.size() - 1).getPostDate();
                }
                postsLiveData.postValue(posts);
            }

            @Override
            public void DataIsInserted() {
                // Not used in this context
            }

            @Override
            public void DataIsUpdated() {
                // Not used in this context
            }

            @Override
            public void DataIsDeleted() {
                // Not used in this context
            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        }, lastLoadedPostDate, POST_LOAD_LIMIT);
    }

    public void addPost(String postContent) {
        postRepository.addPost(postContent, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {

            }

            @Override
            public void DataIsInserted() {
                loadPosts();
            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }

            // Implement other DataStatus methods as necessary
        });
    }

    public void updatePost(Post post) {
        postRepository.updatePost(post, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                loadPosts();
            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }

            // Implement other DataStatus methods as necessary
        });
    }

    public void deletePost(String postId) {
        postRepository.deletePost(postId, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {
                loadPosts();
            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }

            // Implement other DataStatus methods as necessary
        });
    }

    public void toggleLikeOnPost(String postId) {
        postRepository.toggleLikeOnPost(postId, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {
                loadPosts();
            }

            @Override
            public void DataIsDeleted() {

            }

            @Override
            public void DataOperationFailed(Exception e) {
                errorMessage.postValue(e.getMessage());
            }

        });
    }

    public LiveData<List<Post>> getPostsLiveData() {
        return postsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Additional methods as needed...
}








