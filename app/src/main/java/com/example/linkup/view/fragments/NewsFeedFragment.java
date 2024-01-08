package com.example.linkup.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linkup.R;
import com.example.linkup.adapter.PostAdapter;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;

import java.util.ArrayList;

public class NewsFeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private PostViewModel postViewModel;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the ViewModels
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize the adapter with ViewModels and LifecycleOwner
        postAdapter = new PostAdapter(userViewModel, this);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(postAdapter);

        // Observe the posts LiveData from the PostViewModel
        postViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), posts -> {
            // Update the adapter's data when the posts list changes
            postAdapter.setPostList(posts);
        });

        // Fetch posts when the fragment starts
        postViewModel.getAllPosts();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Additional operations, if needed
    }
}



