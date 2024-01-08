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
import com.example.linkup.viewModel.UserViewModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private UserViewModel userViewModel;
    private LifecycleOwner lifecycleOwner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault());

    public PostAdapter(UserViewModel userViewModel, LifecycleOwner lifecycleOwner) {
        this.postList = new ArrayList<>();
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;
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
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setPostList(List<Post> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView textPostContent;
        TextView textPostDate;
        TextView textLikeCount;
        TextView textPosterName;
        TextView textPosterEmail;
        ImageView imagePosterProfile;
        ImageView imageLikeIcon;
        ImageView buttonOptions;

        public PostViewHolder(View itemView) {
            super(itemView);
            textPostContent = itemView.findViewById(R.id.textPostContent);
            textPostDate = itemView.findViewById(R.id.textPostDate);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            textPosterName = itemView.findViewById(R.id.textPosterName);
            textPosterEmail = itemView.findViewById(R.id.textPosterEmail);
            imagePosterProfile = itemView.findViewById(R.id.imagePosterProfile);
            imageLikeIcon = itemView.findViewById(R.id.imageLikeIcon);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
        }

        void bind(Post post) {
            textPostContent.setText(post.getPostContent());
            textPostDate.setText(dateFormat.format(post.getPostDate()));
            textLikeCount.setText(String.valueOf(post.getPostLikes()));

            // Observe user details based on posterId
            userViewModel.getUserLiveData().observe(lifecycleOwner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null && user.getUserId().equals(post.getPosterId())) {
                        textPosterName.setText(user.getFullName());
                        textPosterEmail.setText(user.getEmail());
                        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                            Picasso.get().load(user.getProfileImage()).into(imagePosterProfile);
                        }
                    }
                }
            });
            // Fetch user details for the current post
            userViewModel.getUser(post.getPosterId());
        }
    }
}



