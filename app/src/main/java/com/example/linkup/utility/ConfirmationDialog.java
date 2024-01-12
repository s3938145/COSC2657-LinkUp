package com.example.linkup.utility;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class ConfirmationDialog {

    public static void showConfirmationDialog(
            Context context,
            String title,
            String message,
            String positiveButtonText,
            String negativeButtonText,
            DialogInterface.OnClickListener onPositiveClick,
            DialogInterface.OnClickListener onNegativeClick
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set title and message
        builder.setTitle(title);
        builder.setMessage(message);

        // Add a positive button with custom text
        builder.setPositiveButton(positiveButtonText, onPositiveClick);

        // Add a negative button with custom text
        builder.setNegativeButton(negativeButtonText, onNegativeClick);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
