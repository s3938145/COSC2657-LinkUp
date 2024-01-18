package com.example.linkup.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.linkup.model.Post;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.service.FirebaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostViewModel extends AndroidViewModel {

    private final PostRepository postRepository;
    private final MutableLiveData<List<Post>> postsLiveData;
    private final MutableLiveData<String> errorMessage;
    private long lastLoadedPostDate = Long.MAX_VALUE;
    private static final int POST_LOAD_LIMIT = 10;
    private boolean isFetchingPosts = false;
    private String currentFilter = "Default";
    private String currentSearchQuery = "";
    private final List<Post> allFetchedPosts = new ArrayList<>();
    private String currentUserId;

    public PostViewModel(@NonNull Application application) {
        super(application);
        FirebaseService firebaseService = new FirebaseService(application.getApplicationContext());
        currentUserId = firebaseService.getCurrentUser().getUid();
        postRepository = new PostRepository(firebaseService);
        postsLiveData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        loadPosts();
    }

    public void setCurrentFilter(String filter) {
        this.currentFilter = filter;
        applyCurrentFilterAndSearch();
    }

    public void setCurrentSearchQuery(String query) {
        this.currentSearchQuery = query;
        applyCurrentFilterAndSearch();
    }

    public void loadPosts() {
        lastLoadedPostDate = Long.MAX_VALUE;
        allFetchedPosts.clear();
        fetchData();
    }

    public void loadMorePosts() {
        if (isFetchingPosts) return;
        fetchData();
    }

    private void fetchData() {
        if (isFetchingPosts) {
            return;
        }
        isFetchingPosts = true;

        postRepository.getAllPosts(lastLoadedPostDate, POST_LOAD_LIMIT, new FirebaseService.FirebaseCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> newPosts) {
                if (!newPosts.isEmpty()) {
                    lastLoadedPostDate = newPosts.get(newPosts.size() - 1).getPostDate();
                    allFetchedPosts.addAll(newPosts);
                    applyCurrentFilterAndSearch();
                }
                isFetchingPosts = false;
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.postValue(e.getMessage());
                isFetchingPosts = false;
            }
        });
    }

    private void applyCurrentFilterAndSearch() {
        List<Post> filteredPosts = filterPosts(allFetchedPosts, currentFilter, currentSearchQuery);
        postsLiveData.postValue(filteredPosts);
    }

    private List<Post> filterPosts(List<Post> posts, String filter, String searchQuery) {
        return posts.stream()
                .filter(post -> matchesFilterAndSearch(post, filter, searchQuery))
                .collect(Collectors.toList());
    }

    private boolean matchesFilterAndSearch(Post post, String filter, String searchQuery) {
        boolean matchesFilter = filter.equals("Default") ||
                (filter.equals("Liked") && post.getLikedByUsers().contains(currentUserId)) ||
                (filter.equals("My Posts") && post.getPosterId().equals(currentUserId));

        boolean matchesSearch = post.getPostContent().toLowerCase().contains(searchQuery.toLowerCase());

        return matchesFilter && matchesSearch;
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








