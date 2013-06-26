To make the database connection work you must add the connection driver (mysql-connectior-java-bin.jar) to your CLASS PATH.

JDEVELOPER: 
	Application > Default Project Properties
	Libraries and Classpath
	Click "Add Library"
	Click "New"
	Click "Add Entry..."
	Select "mysql-connectior-java-bin.jar"
	Give it the name mysql-connector and press okay to everything

WEBLOGIC: 
	This is the default web server jdeveloper launches to. 
	copy the .jar file to 
		C:\Users\<USERNAME>\AppData\Roaming\JDeveloper\system<RANDOM NUMBERS>\DefaultDomain\lib
		
Good luck!