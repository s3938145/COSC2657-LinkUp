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

public class NewsFeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private PostViewModel postViewModel;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ViewModel initialization moved to onViewCreated
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
        });

        postViewModel.loadPosts();
    }
}






