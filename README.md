# AndroidAssetImporter
An Android Studio plugin designed to facilitate importing assets into your Android project in an easy and painless manner. No more copy and pasting!
- Find out more from the [Intellij Plugin Repo](https://plugins.jetbrains.com/plugin/8023)

#Usage
- Select an Android Project top level folder 
- Select a folder with appropriate assets, following the agreed naming convention. 
- Rename your asset. 
- Select densities from list of available ones. 
- Import! (if folders don't exist they will be created automatically)

#IMPORTANT
- If you're getting a PluginException in your IDE when first trying to run the plugin, please make sure that you are running against some version of JDK 1.8.
- Steps for this are: 
- Download the latest [JDK version](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and install it.
- Edit your JAVA_HOME environment variable for Windows or as appropriate for other OS's to point at the new JDK directory.
- Restart the IDE.

#License
This project is licensed under the terms of the [Apache 2.0 License] (https://github.com/davy307/AndroidAssetImporter/blob/master/LICENSE.md)

#Screenshots
- Welcome Dialog:

![Welcome dialog](https://github.com/david-serrano/AndroidAssetImporter/blob/master/screenshots/sample_info.png?raw=true "Welcome Dialog")

- Main Screen:

![Main Screen](https://github.com/david-serrano/AndroidAssetImporter/blob/master/screenshots/sample_main.png?raw=true "Main Screen")

- Extension Selection:

![Extension Selection](https://github.com/david-serrano/AndroidAssetImporter/blob/master/screenshots/sample_extensions.png?raw=true "Extension Selection")

- Compliance Checking:

![Compliance Checking](https://github.com/david-serrano/AndroidAssetImporter/blob/master/screenshots/sample_error.png?raw=true "Compliance Checking")

- Sequential Multiple Imports:

![Sequential Multiple Imports](https://github.com/david-serrano/AndroidAssetImporter/blob/master/screenshots/sample_success.png?raw=true "Sequential Multiple Imports")
