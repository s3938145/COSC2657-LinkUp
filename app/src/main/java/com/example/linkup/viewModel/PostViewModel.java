package com.example.linkup.viewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.linkup.model.Post;
import com.example.linkup.repository.PostRepository;
import com.example.linkup.service.FirebaseService;

import java.util.List;

public class PostViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final MutableLiveData<List<Post>> postsLiveData;
    private final MutableLiveData<String> errorMessage;
    private long lastLoadedPostDate = Long.MAX_VALUE;
    private static final int POST_LOAD_LIMIT = 10; // Set your desired load limit

    public PostViewModel(FirebaseService firebaseService) {
        postRepository = new PostRepository(firebaseService);
        postsLiveData = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        loadPosts();
    }

    private void loadPosts() {
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
                loadPosts(); // Reload the posts after a new post is added
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
        });
    }

    public void updatePost(Post post) {
        postRepository.updatePost(post, new PostRepository.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts) {
                // Not used here
            }

            @Override
            public void DataIsInserted() {
                // Not used here
            }

            @Override
            public void DataIsUpdated() {
                loadPosts(); // Reload the posts after a post is updated
            }

            @Override
            public void DataIsDeleted() {
                // Not used here
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
                // Not used here
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
                loadPosts(); // Reload the posts after a post is deleted
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





