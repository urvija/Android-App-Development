This application has been developed at the request of one of my friends who is planning to start his own cab services company called “Cool Cab”. 

The flow of the app is: 

* Whenever app is started there is an “App Introduction” which User can ‘skip’.  * User needs to ‘sign up’ in order to use the app.  * Then User can request a ride i.e. submit pick-up and drop-off location  * There are 3 types of cabs available: economy, premium and limousine. economy cab’s rate    is $1.5/mile, premium cab’s rate is $2.5/mile and limousine's rate is $4.0/mile.  * User gets a quote i.e. approximate distance and fare  * Then User needs to select day and time for the ride  * When ride is reserved, user gets confirmation via email (the registered email)  
The project has been tested only on:  • Nexus 5X API 23 Emulator  
Third Party Libraries used are:  
1. Firebase API: 
    * To authenticate users with email and password      * As backend for storing user information (to a NoSQL cloud database).      * To retrieve data, set read/write permissions on the data and validate the data on 	the server, to remove data etc.  
2. Google's distance API     * To get distance between user specified two points/addresses.  
3. appintro     * For the app introduction, in the beginning, I have used paola retolo’s library 		called appintro by adding the library in gradle (compile 	‘com.github.paolorotolo:appintro:3.4.0')  

Issues: 

When user clicks on “reserve”, the app stores the data in Firebase however as I was  checking my app from emulator, I have to have at least one email client open in order to send the confirmation email to the user. 
