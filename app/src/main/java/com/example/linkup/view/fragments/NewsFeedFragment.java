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
import com.example.linkup.model.Post;
import com.example.linkup.model.User;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewsFeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private PostViewModel postViewModel;
    private UserViewModel userViewModel;
    private Map<String, User> userCache = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        postViewModel = new ViewModelProvider(requireActivity(), factory).get(PostViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);

        postAdapter = new PostAdapter(userViewModel, getViewLifecycleOwner(), postViewModel);
        recyclerView.setAdapter(postAdapter);

        postViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), posts -> {
            postAdapter.setPostList(posts);
            prefetchUserData(posts);
        });

        postViewModel.loadPosts();
    }

    private void prefetchUserData(List<Post> posts) {
        Set<String> userIds = new HashSet<>();
        for (Post post : posts) {
            userIds.add(post.getPosterId());
        }

        for (String userId : userIds) {
            if (!userCache.containsKey(userId)) {
                userViewModel.getUser(userId);
                userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        userCache.put(userId, user);
                        postAdapter.notifyDataSetChanged(); // Notify the adapter to refresh data
                    }
                });
            }
        }
    }
}





