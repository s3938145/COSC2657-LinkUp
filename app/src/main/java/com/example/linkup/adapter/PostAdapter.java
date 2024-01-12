package com.example.linkup.adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.service.FirebaseService;
import com.example.linkup.utility.ConfirmationDialog;
import com.example.linkup.utility.NavigationHelper;
import com.example.linkup.utility.PostDiffCallback;
import com.example.linkup.utility.UserProfileHeaderHandler;
import com.example.linkup.view.fragments.NewsFeedFragmentDirections;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private List<Post> allPosts; // Added to hold all posts
    private UserViewModel userViewModel;
    private PostViewModel postViewModel;
    private FirebaseService firebaseService;
    private LifecycleOwner lifecycleOwner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault());

    public PostAdapter(UserViewModel userViewModel, LifecycleOwner lifecycleOwner, PostViewModel postViewModel, FirebaseService firebaseService) {
        this.postList = new ArrayList<>();
        this.allPosts = new ArrayList<>(); // Initialize allPosts
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.postViewModel = postViewModel;
        this.firebaseService = firebaseService;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
        updateUserProfileViews(holder, post.getPosterId());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setPostList(List<Post> newPostList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(this.postList, newPostList));
        this.postList.clear();
        this.postList.addAll(newPostList);
        diffResult.dispatchUpdatesTo(this);
    }

    // Method to filter posts
    public void filter(String query) {
        query = query.toLowerCase();
        List<Post> filteredList = new ArrayList<>();
        for (Post post : allPosts) {
            if (post.getPostContent().toLowerCase().contains(query)) {
                filteredList.add(post);
            }
        }
        postList = filteredList;
        notifyDataSetChanged();
    }

    // Call this method when you set posts initially
    public void setAllPosts(List<Post> posts) {
        allPosts = new ArrayList<>(posts); // Store all posts
        setPostList(posts); // This sets and displays the posts
    }

    private void updateUserProfileViews(PostViewHolder holder, String userId) {
        View rootView = holder.itemView;
        UserProfileHeaderHandler.updateUserProfileViews(userViewModel, userId, rootView, lifecycleOwner);
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textPostContent;
        TextView textPostDate;
        TextView textLikeCount;
        ImageView imageLikeIcon;
        ImageView buttonOptions;

        public PostViewHolder(View itemView) {
            super(itemView);
            textPostContent = itemView.findViewById(R.id.textPostContent);
            textPostDate = itemView.findViewById(R.id.textPostDate);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            imageLikeIcon = itemView.findViewById(R.id.imageLikeIcon);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);

            buttonOptions.setOnClickListener(v -> showPopupMenu(getAdapterPosition()));
            imageLikeIcon.setOnClickListener(v -> onLikeClicked(getAdapterPosition()));
        }

        void bind(Post post) {
            textPostContent.setText(post.getPostContent());
            textPostDate.setText(dateFormat.format(new Date(post.getPostDate())));
            textLikeCount.setText(String.valueOf(post.getPostLikes()));
        }

        private void showPopupMenu(int position) {
            Post post = postList.get(position);
            String currentUserId = firebaseService.getCurrentUser().getUid();
            boolean isCurrentUser = currentUserId != null && post.getPosterId().equals(currentUserId);

            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), buttonOptions);
            popupMenu.getMenuInflater().inflate(R.menu.post_options_menu, popupMenu.getMenu());

            MenuItem updateMenuItem = popupMenu.getMenu().findItem(R.id.menu_update);
            MenuItem deleteMenuItem = popupMenu.getMenu().findItem(R.id.menu_delete);

            updateMenuItem.setVisible(isCurrentUser);
            deleteMenuItem.setVisible(isCurrentUser);

            updateMenuItem.setOnMenuItemClickListener(item -> {
                if (position != RecyclerView.NO_POSITION) {
                    NewsFeedFragmentDirections.ActionNewsFeedFragmentToUpdatePostFragment action =
                            NewsFeedFragmentDirections.actionNewsFeedFragmentToUpdatePostFragment(post.getPostId());
                    NavigationHelper.navigateToFragment(itemView, action);
                    return true;
                }
                return false;
            });

            deleteMenuItem.setOnMenuItemClickListener(item -> {
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteConfirmationDialog(position, post.getPostId());
                }
                return false;
            });

            popupMenu.show();
        }

        private void onLikeClicked(int position) {
            if (position != RecyclerView.NO_POSITION) {
                Post post = postList.get(position);
                postViewModel.toggleLikeOnPost(post.getPostId());
            }
        }

        private void showDeleteConfirmationDialog(int position, String postId) {
            ConfirmationDialog.showConfirmationDialog(
                    itemView.getContext(),
                    "Confirm Deletion",
                    "Are you sure you want to delete this post?",
                    "Yes",
                    "No",
                    (dialog, which) -> postViewModel.deletePost(postId),
                    (dialog, which) -> dialog.dismiss()
            );
        }
    }
}










