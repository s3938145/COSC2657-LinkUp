package com.example.linkup.utility;

public class GenerateIdUtils {

    public static String generateChatId(String userIdOne, String userIdTwo) {
        // Compare the two user IDs lexicographically
        int compareResult = userIdOne.compareTo(userIdTwo);

        // If userIdOne is lexicographically less than userIdTwo,
        // concatenate userIdOne and userIdTwo, otherwise reverse the order
        if (compareResult < 0) {
            return userIdOne + userIdTwo;
        } else {
            return userIdTwo + userIdOne;
        }
    }
}
