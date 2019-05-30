//const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
'use strict';
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.updateAttendingUpCount = functions.database.ref('/{TypeOfEvent}/{eventID}/attending')
    .onWrite((snapshot) => {
      const ref = snapshot.after.ref.parent.child('attendees');
      return ref.set(snapshot.after.numChildren());
});

exports.deleteFriend = functions.database.ref('admin/Users/{UserID}/friends/{FriendID}')
    .onDelete((snapshot) => {
        var deletedFriend = snapshot.key;
        var delter = snapshot.ref.parent.parent.key;
        return snapshot.ref.root.child('admin').child('Users').child(deletedFriend).child('friends').child(delter).remove();
});

exports.createFriendGroup = functions.database.ref('friendGroups/{FriendGroupID}')
    .onCreate((snapshot) => {
        var groupID = snapshot.key;
        snapshot.forEach(function(child) {
          console.log(child.key)
        });
});