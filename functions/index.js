
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


 // Import the Firebase SDK for Google Cloud Functions.
const functions = require('firebase-functions');
 // Import and initialize the Firebase Admin SDK.
const admin = require('firebase-admin');
admin.initializeApp();


exports.sendNotifications = functions.database.ref('/message').onWrite((change, context) => {

  //const snapshot = event.data;
  //const text = snapshot.val();

  // Notification details.

  if (!change.after.val()) {
      return console.log('Message un-changed');
  }

  const text = change.after.val();

  var payload = {
    notification: {
      title: `New message to the backend: `,
      body: 'New message push',
      //icon: '/images/firebase_ic.png'
    }
  };


// : ${text}

  //var tokens = ["eFsn6dxFOO4:APA91bE1HSFlHFvT7D31ym71V-vmQ_lGx6C3R1eD4qxEvCcfgEzRPHeOa4mlXCz4KsrviWbntAF_Dd9-TXQqbmsN7KbbSIy_He_dkcEppDgq9EdQVchq4M7Cf54lcn06RmCKg3ZcyYub",
               //"d4chLQIamxo:APA91bFiVwgIvvGwrvua_d4yh1tNtiL49M8AfJR68uhvWV1lXCXvVuaN6Rn5vmHw6tCp8GEN3EoAXhwNDqzYsd13BqF8cVfqAKbSbbv_4IHe4nMEKaskCjo0h1Tbo9HuEUWgUISdAPIM"];
  //const getDeviceTokensPromise1 = admin.database()
            //.ref(`/tokensdevices/${tokenkey}/${tokenvalue}`).once('value');
            //.ref(`/tokensdevices/token1`).once('value');

     //const getDeviceTokensPromise2 = admin.database()
               //.ref(`/tokensdevices/${tokenkey}/${tokenvalue}`).once('value');
               //.ref(`/tokensdevices/token2`).once('value');

        // The snapshot to the user's tokens.

      //var userId = admin.auth().currentUser.uid;
      //const getDeviceTokensPromise = admin.database().ref('/users/' + userId).once('value')
      const getDeviceTokensPromise = admin.database().ref('/tokensReg').once('value');
      //`/users/${followedUid}/notificationTokens`
      //const getDeviceTokensPromise = admin.database().ref('/tokensReg').once('value');

     //if (!getDeviceTokensPromise.hasChildren()) {
        // return console.log("No notification tokens to send to");
     //}


      return Promise.all([getDeviceTokensPromise]).then(results => {

      //const results = Promise.all([getDeviceTokensPromise]);

      let tokensSnapshot;

        // The array containing all the user's tokens.
      let tokens;

      //const results = await Promise.all([getDeviceTokensPromise]);
      tokensSnapshot = results[0];
      //tokensSnapshot = results;



           // Check if there are any device tokens.
      if (!tokensSnapshot.hasChildren()) {
             return console.log('There are no notification tokens to send to.');
      }
           // Listing all tokens as an array.

      console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
      console.log('tokensReg', Object.keys(tokensSnapshot.val()));


      tokens = Object.keys(tokensSnapshot.val());

      //tokens= "dTMpotBvRNGe7rnVL7MkVy:APA91bG_iAGbdBi2Iszl4HZBtlZeWKXjaYqcV4WFedg5QO89n3ErxF9s50Yy1sD6HPdhDSYlAHdmYZ-UibjyAQxPRH-OS1HPjcmv3y5m2Qff56gZiu2N74YwWSbpJjWZvhBceBn4rA60";

    return admin.messaging().sendToDevice(tokens, payload);
    });
  //return admin.messaging().sendToDevice(tokens, payload)
  });




// [START addMessage]
// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
// [START addMessageTrigger]//
exports.addMessage = functions.https.onRequest((req, res) => {
// [END addMessageTrigger]
  // Grab the text parameter.
  const original = req.query.text;
  // [START adminSdkPush]
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  return admin.database().ref('/messages').push({original: original}).then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
  return res.redirect(303, snapshot.ref.toString());
  });
  // [END adminSdkPush]
});
// [END addMessage]

// [START makeUppercase]
// Listens for new messages added to /messages/:pushId/original and creates an
// uppercase version of the message to /messages/:pushId/uppercase
exports.makeUppercase = functions.database.ref('/messages/{pushId}/original')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const original = snapshot.val();
      console.log('Uppercasing', context.params.pushId, original);
      const uppercase = original.toUpperCase();
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return snapshot.ref.parent.child('uppercase').set(uppercase);
    });
// [END makeUppercase]
// [END all]

//fcm-notifications

exports.sendFollowerNotification = functions.database.ref('/followers/{followedUid}/{followerUid}')
    .onWrite((change, context) => {
      const followerUid = context.params.followerUid;
      const followedUid = context.params.followedUid;
      // If un-follow we exit the function.
      if (!change.after.val()) {
        return console.log('User ', followerUid, 'un-followed user', followedUid);
      }
      console.log('We have a new follower UID:', followerUid, 'for user:', followerUid);

      // Get the list of device notification tokens.
      const getDeviceTokensPromise = admin.database()
          .ref(`/users/${followedUid}/notificationTokens`).once('value');

      // Get the follower profile.
      const getFollowerProfilePromise = admin.auth().getUser(followerUid);

      // The snapshot to the user's tokens.
      let tokensSnapshot;

      // The array containing all the user's tokens.
      let tokens;

      return Promise.all([getDeviceTokensPromise, getFollowerProfilePromise]).then(results => {
        tokensSnapshot = results[0];
        const follower = results[1];

        // Check if there are any device tokens.
        if (!tokensSnapshot.hasChildren()) {
          return console.log('There are no notification tokens to send to.');
        }
        console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
        console.log('Fetched follower profile', follower);

        // Notification details.
        const payload = {
          notification: {
            title: 'You have a new follower!',
            body: `${follower.displayName} is now following you.`,
            icon: follower.photoURL
          }
        };

        // Listing all tokens as an array.
        tokens = Object.keys(tokensSnapshot.val());
        // Send notifications to all tokens.
        return admin.messaging().sendToDevice(tokens, payload);
      }).then((response) => {
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', tokens[index], error);
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          }
        });
        return Promise.all(tokensToRemove);
      });
});



