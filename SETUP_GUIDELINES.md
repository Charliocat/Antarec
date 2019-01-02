## Configuration

To use the app or contribute you should clone the project and open it with:
- Android Studio 3.2
- Kotlin version 1.3.0
- Gradle version 3.2.1

Then you must setup the project to use Firebase and Firestorage, an add the google-services.json file

Also you are going to need a Google API key, change the file [google_api_key.xml](https://github.com/Charliocat/Antarec/blob/develop/app/src/main/res/values/google_api_key.xml)

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_maps_api_key" translatable="false" templateMergeStrategy="preserve">
        "Your key goes here"
    </string>
</resources>
```

Once you have all the previous done, you can build and run the application, either to an Android Emulator or to a Real Device.
Minimum Android version needed is Android 5.1
