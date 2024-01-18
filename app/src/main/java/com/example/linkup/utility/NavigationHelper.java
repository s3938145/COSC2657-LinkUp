package com.example.linkup.utility;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class NavigationHelper {

    // Navigate without arguments
    public static void navigateToFragment(View view, @IdRes int destinationId) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(destinationId);
    }

    // Navigate with Safe Args
    public static void navigateToFragment(View view, NavDirections directions) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(directions);
    }
}

