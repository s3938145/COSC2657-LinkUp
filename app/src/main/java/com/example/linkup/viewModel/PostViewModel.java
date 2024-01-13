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
        postRepository.getAllPosts(lastLoadedPostDate, POST_LOAD_LIMIT, new FirebaseService.FirebaseCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (!posts.isEmpty()) {
                    lastLoadedPostDate = posts.get(0).getPostDate();
                }
                postsLiveData.postValue(posts);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }


    public void addPost(String postContent) {
        postRepository.addPost(postContent, new FirebaseService.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                lastLoadedPostDate = Long.MAX_VALUE;
                loadPosts();
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }


    public void updatePost(Post post) {
        postRepository.updatePost(post, new FirebaseService.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadPosts();
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }


    public void deletePost(String postId) {
        postRepository.deletePost(postId, new FirebaseService.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadPosts();
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }


    public void toggleLikeOnPost(String postId) {
        postRepository.toggleLikeOnPost(postId, new FirebaseService.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadPosts();
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }


    public LiveData<Post> getPostById(String postId) {
        return postRepository.getPostById(postId);
    }

    public LiveData<List<Post>> getPostsLiveData() {
        return postsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Additional methods as needed...
}








