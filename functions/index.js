const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendLikeNotification = functions.database.ref('/posts/{postId}/likedByUsers/')
    .onWrite(async (change, context) => {
        const postId = context.params.postId;

        // Check if like was added or removed
        if (!change.after.exists()) {
            return null; // Like was removed, do nothing
        }

        // Retrieve the post
        const postSnapshot = await admin.database().ref(`/posts/${postId}`).once('value');
        const post = postSnapshot.val();

        // Retrieve the owner of the post
        const ownerSnapshot = await admin.database().ref(`/posts/${posterId}`).once('value');
        const owner = ownerSnapshot.val();

        // Check if the owner has an FCM token
        if (!owner.fcmToken) {
            console.log('No FCM Token for user, cannot send notification');
            return null;
        }

        // Create a notification message
        const notificationMessage = {
            notification: {
                title: 'Your post was liked!',
                body: 'Someone liked your post.',
                // Add other notification properties as needed
            },
            token: owner.fcmToken,
        };

        // Send the notification
        try {
            await admin.messaging().send(notificationMessage);
            console.log('Notification sent successfully');
        } catch (error) {
            console.error('Error sending notification:', error);
        }

        return null;
    });


