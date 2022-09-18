![](https://img.shields.io/badge/Java-17-blue)
![](https://img.shields.io/badge/Gradle-7.4%2B-blue)

This project builds on an application "Currency Converter" which helps users convert between two currencies using the latest exchange rate. 

There are two types of users, one is the normal users who can get the most popular currencies and the exchange rates shown in a table with signs showing if the rate increases or decreases, then choose the two currencies to convert. 
And the other one is the admin users who can do more operations than the normal users. They can update the exchange rates daily and add new currency types and its exchange rate. 
The history of rate changes is stored in a JSON file and the users can get a summary of the conversion rates of two currencies during a period. 

This project interacts with the users through command lines.

# How to Run the Project

This project needs Java and Gradle installed to run. 

For Java, it requires Java JDK version 17 to run. To check the Java version in your machine, run the command `java -version`. 

For Gradle, it requires version 7.4 or higher. To check the Gradle version, run the command `gradle -version`. 

With Java and Gradle successfully installed, run the command `gradle run` in the terminal to run the application.

# How to Use the Project

First, a login menu shows up. You need an account to use the converter, so either register if you are a new user or log in if you are an existing user. 

Then select your user type from Admin or Normal User and input your username. The system will verify it. 

If you are a normal user, you can get the table of the popular currencies and the exchange rates between them, then you will be asked to input the code of the currency you want to convert to, the amount of that, and the code of the currency you want to convert from. 

If you are an admin user, besides the above operations, you can update the exchange rate daily of existing currencies and add currency types and its rate.

# How to Test

We utilise JUnit to test. We create a test file for each class except the UserInterface class. 

Firstly, `import static org.junit.jupiter.api.Assertions.*`
and `import org.junit.jupiter.api.Test`
to import the libraries needed for JUnit testing in the test files. 

Then add 
`testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'` into dependencies in the build.gradle file, and a JUnit test task 

```groovy
tasks.named('test') {
    useJUnitPlatform()
}
```

into the `build.gradle` file. 

Run the test command `gradle clean test` to test. 

# How to Contribute

## Before you start

Before starting to work on something, please open an issue and discuss it with the group members to decide whether it works.  

## Set Up your development environment

* Java JDK version 17 or higher
* Gradle version 7.4 or higher
* A text editor or IDE. We recommend IntelliJ and VSCode. 
* Git and a GitHub account within [the University of Sydney Enterprise](https://github.sydney.edu.au/), [SOFT2412/COMP9412 2022 Organization](https://github.sydney.edu.au/SOT2412-COMP9412-2022S2).

## Making your change

1. Fork the repository to your local machine.
2. Create your own branch. Do not make changes in the master branch at the beginning.
3. Write your code, and create test cases using JUnit for new classes or methods.

## Submitting your change

1. Commit the code changes to your branch with a specific commit message.
2. Open a pull request.
3. Respond to the review messages given by the team members and make corresponding code changes.
