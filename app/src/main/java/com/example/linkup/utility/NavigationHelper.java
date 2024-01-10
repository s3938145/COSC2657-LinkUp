package com.example.linkup.utility;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class NavigationHelper {

    /**
     * Navigate to a specified fragment using NavController.
     *
     * @param view The current view to find NavController.
     * @param destinationId The ID of the destination fragment to navigate to.
     */
    public static void navigateToFragment(View view, @IdRes int destinationId) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(destinationId);
    }
}

