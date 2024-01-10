package com.example.linkup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.Post;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;
import com.squareup.picasso.Picasso;

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
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void setPostList(List<Post> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged();
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

            imageLikeIcon.setOnClickListener(v -> onLikeClicked(getAdapterPosition()));
        }

        void bind(Post post) {
            textPostContent.setText(post.getPostContent());
            textPostDate.setText(dateFormat.format(new Date(post.getPostDate())));
            textLikeCount.setText(String.valueOf(post.getPostLikes()));

            userViewModel.getUser(post.getPosterId());
            userViewModel.getUserLiveData().observe(lifecycleOwner, user -> {
                if (user != null && user.getUserId().equals(post.getPosterId())) {
                    // Update user info in post

                }
            });
        }

        private void onLikeClicked(int position) {
            if (position != RecyclerView.NO_POSITION) {
                Post post = postList.get(position);
                postViewModel.toggleLikeOnPost(post.getPostId());
            }
        }
    }
}






