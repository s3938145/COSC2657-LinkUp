package com.example.linkup.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.linkup.R;
import com.example.linkup.adapter.PostAdapter;
import com.example.linkup.service.FirebaseService;
import com.example.linkup.viewModel.PostViewModel;
import com.example.linkup.viewModel.UserViewModel;

public class NewsFeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private PostViewModel postViewModel;
    private UserViewModel userViewModel;
    private FirebaseService firebaseService;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        setupSwipeRefreshLayout();

        searchView = view.findViewById(R.id.searchView);
        setupSearchView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        postViewModel = new ViewModelProvider(requireActivity(), factory).get(PostViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);

        firebaseService = new FirebaseService(getContext());

        postAdapter = new PostAdapter(userViewModel, getViewLifecycleOwner(), postViewModel, firebaseService);
        recyclerView.setAdapter(postAdapter);

        Button filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterOptions());

        postViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), posts -> {
            postAdapter.setAllPosts(posts);
            swipeRefreshLayout.setRefreshing(false);
        });

        setupRecyclerViewScrollListener();

        postViewModel.loadPosts();
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                postViewModel.setCurrentSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postViewModel.setCurrentSearchQuery(newText);
                return true;
            }
        });
    }

    private void setupRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1)) { // Check if scrolling to the bottom
                    postViewModel.loadMorePosts();
                }
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            postViewModel.loadPosts();
            updateFilterButtonText("Default");
        });
    }

    private void showFilterOptions() {
        String[] filterOptions = {"Default", "Liked", "My Posts"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Filter")
                .setItems(filterOptions, (dialog, which) -> {
                    String selectedFilter = filterOptions[which];
                    postViewModel.setCurrentFilter(selectedFilter);
                    updateFilterButtonText(selectedFilter);
                });
        builder.create().show();
    }
    private void updateFilterButtonText(String filter) {
        Button filterButton = getView().findViewById(R.id.filterButton);
        filterButton.setText(filter);
    }
}







