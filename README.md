## QAM2 — QAM2 Task 1: Java Application Development

### Purpose of application
The purpose of this application is to provide a GUI based scheduling desktop application.

### Author Information
name: Cody Grosskopf  
email: cgros22@wgu.edu  
application version: 1.0  
date: 12/27/2021

### IDE and java module Information
IntelliJ IDEA 2021.2.3 (Community Edition)  
Build #IC-212.5457.46, built on October 12, 2021  
Runtime version: 11.0.12+7-b1504.40 aarch64  
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Registry: documentation.show.toolbar=true  
Non-Bundled Plugins: com.intellij.javafx (1.0.3)

#### javafx: openjfx-11.0.2
#### mysql connector: mysql-connector-java-8.0.26

### Additional report

For the custom report, I chose to display the number of appointments per Country. I chose to have sql do all the work and created a query that contains inner joins which combines three tables and spits out two columns (Country and count). 
The query is complicated but made the java side was very simple and I was able to pull this information into a tableview to present the data. 

### How to run the program

As the program starts, a login screen is presented. The user will
be required to have a valid username and password that matches 
information in a mysql database. This program requires java 11 and
has not been tested with any other jvm. 