# Project Description 
This project builds on an application "Currency Converter" which helps users convert between two currencies using the latest exchange rate. 
There are two types of users, one is the normal users who can get the most popular currencies and the exchange rates shown in a table with signs showing if the rate increases or decreases, then choose the two currencies to convert. 
And the other one is the admin users who can do more operations than the normal users. They can update the exchange rates daily and add new currency types and its exchange rate. 
The history of rate changes is stored in a JSON file and the users can get a summary of the conversion rates of two currencies during a period. 
This project interacts with the users through command lines. 
# How to Run the Project
This project needs Java and Gradle installed to run. For Java, it requires a Java JDK version 17 to run. To check the Java version in your machine, run the command "java -version". For Gradle, it requires version 7.4. To check the Gradle version, run the command "Gradle -version". 
With Java and Gradle successfully installed, run the command "gradle run" in the terminal to run the application. 
# How to Test:
We utilise Junit to test. We create a test file for each class except the UserInterface. With "static org.junit.jupiter.api.Assertions.assertEquals" and "org.junit.jupiter.api.Test" libraries imported in the test files and dependencies and tasks regarding to the Junit test added in the build.gradle file, run the test command "gradle clean test" to test.  