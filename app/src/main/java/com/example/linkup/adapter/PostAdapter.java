package com.example.linkup.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.utility.ConfirmationDialog;
import com.example.linkup.utility.UserProfileHeaderHandler;
import com.example.linkup.view.fragments.CreatePostFragment;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private UserViewModel userViewModel;
    private PostViewModel postViewModel;
    private LifecycleOwner lifecycleOwner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault());

    public PostAdapter(UserViewModel userViewModel, LifecycleOwner lifecycleOwner, PostViewModel postViewModel) {
        this.postList = new ArrayList<>();
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.postViewModel = postViewModel;
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

        // Update user profile views
        updateUserProfileViews(holder, post.getPosterId());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setPostList(List<Post> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged();
    }

    // Add this method to update user profile views
    private void updateUserProfileViews(PostViewHolder holder, String userId) {
        View rootView = holder.itemView; // Get the root view of the item
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
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), buttonOptions);
            popupMenu.getMenuInflater().inflate(R.menu.post_options_menu, popupMenu.getMenu());

            // Get the menu items by their IDs
            MenuItem updateMenuItem = popupMenu.getMenu().findItem(R.id.menu_update);
            MenuItem deleteMenuItem = popupMenu.getMenu().findItem(R.id.menu_delete);

            // Set click listeners for menu items
            updateMenuItem.setOnMenuItemClickListener(item -> {
                if (position != RecyclerView.NO_POSITION) {
                    Post post = postList.get(position);
                    // Handle the "Update" option
                    // You can open an update activity or perform the update action here
                    // For example, you can start an UpdatePostActivity with the post data
                    Intent intent = new Intent(itemView.getContext(), CreatePostFragment.class);
                    intent.putExtra("postId", post.getPostId());
                    itemView.getContext().startActivity(intent);
                    return true;
                }
                return false;
            });

            deleteMenuItem.setOnMenuItemClickListener(item -> {
                if (position != RecyclerView.NO_POSITION) {
                    Post post = postList.get(position);
                    // Show a confirmation dialog before deleting
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
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User clicked "Yes," perform the delete operation
                            postViewModel.deletePost(postId);
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User clicked "No," do nothing or handle as needed
                            dialog.dismiss();
                        }
                    }
            );
        }
    }
}










