Issues1. When you run your app and rotate the device/emulator are the method displayed in the TextView consistent with methods called in the log? If not explain why.

Answer: No

This is because, when orientation changes at runtime, Android destroys the current activity and creates a new one and hence all the data in TextView is lost.
 
To solve this problem, we need to save method names/data into the bundle in onSaveInstanceState method. These method names are can then be retrieved in onCreate.

