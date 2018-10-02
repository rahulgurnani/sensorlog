# SensorApp

This app has been developed for collecting raw sensor data in android devices. 

It listens to changes in the following sensors:

* Accelerometer sensor
* Light sensor
* Screen on/off
* Battery charging/discharging
* Power on/off

Various optimizations on the client were also used on client to bring down the battery consumptions. (It's less than 2% per 24hrs in most phones)

The app uses FirebaseDB, so inorder to make it run, you will need to configure FIREBASE_URL in strings.xml, 
add google-servies.json, as mentioned in the link https://firebase.google.com/docs/android/setup?authuser=0

Currently the app syncs data upon it's launch and all the data is not synced rather the data is contracted as specified in classes like AccDataContracted.java etc. 

TODOs : 
* Code commenting and cleaning
* instead of just supporting firebase-db, support rest endpoint where the user of the sdk can push the sensor data
* Turn this more into an sdk
