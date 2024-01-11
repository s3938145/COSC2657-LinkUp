package com.example.linkup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.model.User;
import com.example.linkup.utility.UserProfileHeaderHandler;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private UserViewModel userViewModel;
    private PostViewModel postViewModel;
    private LifecycleOwner lifecycleOwner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault());
    private Map<String, User> userCache = new HashMap<>();

    // Constructor
    public PostAdapter(UserViewModel userViewModel, LifecycleOwner lifecycleOwner, PostViewModel postViewModel) {
        this.postList = new ArrayList<>();
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.postViewModel = postViewModel;
        prefetchUserData();
    }

    // Create new views
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    // Return the size of the dataset
    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Update the post list
    public void setPostList(List<Post> newPostList) {
        this.postList = newPostList;
        prefetchUserData();
        notifyDataSetChanged();
    }

    // Prefetch user data for the posts
    private void prefetchUserData() {
        Set<String> userIds = new HashSet<>();
        for (Post post : postList) {
            userIds.add(post.getPosterId());
        }

        for (String userId : userIds) {
            userViewModel.getUser(userId);
            userViewModel.getUserLiveData().observe(lifecycleOwner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null && user.getUserId().equals(userId)) {
                        userCache.put(userId, user);
                    }
                }
            });
        }
    }

    // Inner class for the ViewHolder
    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textPostContent;
        TextView textPostDate;
        TextView textLikeCount;
        ImageView imageLikeIcon;
        ImageView buttonOptions;
        View userProfileHeader;

        // Constructor
        public PostViewHolder(View itemView) {
            super(itemView);
            textPostContent = itemView.findViewById(R.id.textPostContent);
            textPostDate = itemView.findViewById(R.id.textPostDate);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            imageLikeIcon = itemView.findViewById(R.id.imageLikeIcon);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
            userProfileHeader = itemView.findViewById(R.id.user_profile_header);

            imageLikeIcon.setOnClickListener(v -> onLikeClicked(getAdapterPosition()));
        }

        // Bind post data to the holder
        void bind(Post post) {
            textPostContent.setText(post.getPostContent());
            textPostDate.setText(dateFormat.format(new Date(post.getPostDate())));
            textLikeCount.setText(String.valueOf(post.getPostLikes()));

            // Check cache for user data
            User user = userCache.get(post.getPosterId());
            if (user != null) {
                UserProfileHeaderHandler.updateUserProfileViews(userViewModel, post.getPosterId(), userProfileHeader, lifecycleOwner);
            } else {
                // Fetch and update data
                userViewModel.getUser(post.getPosterId());
                userViewModel.getUserLiveData().observe(lifecycleOwner, newUser -> {
                    if (newUser != null && newUser.getUserId().equals(post.getPosterId())) {
                        userCache.put(post.getPosterId(), newUser);
                        UserProfileHeaderHandler.updateUserProfileViews(userViewModel, post.getPosterId(), userProfileHeader, lifecycleOwner);
                    }
                });
            }
        }

        // Handle like button click
        private void onLikeClicked(int position) {
            if (position != RecyclerView.NO_POSITION) {
                Post post = postList.get(position);
                postViewModel.toggleLikeOnPost(post.getPostId());
                // Optionally, update the UI to reflect the new like state
            }
        }
    }
}








