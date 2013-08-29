#SMS Tester App#

An application and a testing framework to test sms app.

Using this application you can test your sms based application on a real device.You can define your test cases in a simple csv file and execute all the test cases over multiple android phone without any supervision.
The application downloads the test file, executes all the test cases by sending sms and reports the results.

Example app:
* Twitter SMS app - We could define various test cases like to send a registraton message, accpet by replying code to a specific number, send a tweet, expect response message to have a success response.
The above test cases could be defined in 4 lines of csv file.

* Facebook SMS app - We could define test cases for accessing wall posts, read the message to find out specfic link letter that is to be used to send next message to post comment or like the post.
Again these test cases could be written in a couple of lines.

* txtWeb apps - Every apps on txtWeb could be tested by accessing the keyword and tested by expecting a response message pattern. Also a forms and links in the resonse can be extracted and sent in next message.

Good this about the framweork:
* Aims to be complete solution for testing any SMS service via a simple interface to describe test cases.
* Sends sms from a actual device and hence as good as manual testing but much more effecient since there is no manual intervention
* Understands the problem of SMS, It automatically retries by sending SMS whenever it does not receives a response within timeout and also gracefully handles all the duplicate messages created in the process.
* Runs literally on any android phone - built with minimum Android SDK version 2.2
* Testing framework is easily extendable and componenets are pluggable
    * Easy to add a new test type - example test for regex, test for endsWith/startWith etc.
    * Add new getMessage type - could send a static message or a specific part of message extracted by previous message etc.
    * It is easy to plug getMessage() and testTypes() to create various combination
* Can send message to specific number and expect message from specific address
* Handle invalid message received - example some spam message sent to device during testing wont crash the testing.

More documentation will be added soon on exactly how to use and extend testing framework.
Currently this is in a alpha version (a work in progress) - I am using it to test few SMS services I am currently maintaining mostly to test that stabilty of framework and imporve it.
If you want to give it a try see the sample csv file attached.
Go through TestExecutor.java once and you will be up and running in few minutes.

TODO's:
* Use xl file instead of csv
* Separarte getMessage() and executeTest()
* Dependancy chain

More details to follow soon...






