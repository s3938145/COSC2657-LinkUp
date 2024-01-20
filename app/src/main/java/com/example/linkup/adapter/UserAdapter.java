package com.example.linkup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserClickListener onUserClickListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(List<User> userList, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
        holder.recentMessageDot.setVisibility(user.hasRecentMessage() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewUser;
        private TextView textViewUserName;
        public View recentMessageDot;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUser = itemView.findViewById(R.id.imageViewUser);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            recentMessageDot = itemView.findViewById(R.id.recentMessageDot);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onUserClickListener != null && position != RecyclerView.NO_POSITION) {
                    onUserClickListener.onUserClick(userList.get(position));
                }
            });

        }

        public void bind(User user) {
            textViewUserName.setText(user.getFullName());
            Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.ic_profile_place_holder).into(imageViewUser);
        }
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }
    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }
    public List<User> getUserList() {
        return userList;
    }

}
