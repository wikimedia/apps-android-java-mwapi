# Running tests

Run:

  $ mvn test


# Code coverage output

To run tests and produce a code coverage report, run:

  $ mvn cobertura:cobertura

Output will appear in target/sites/.

Note that on Mac OS X, if Apple Java 1.6 is installed you will get a failure here due to some sort of configuration dependency on particularities of Oracle Java's file layout which do not match Apple's version.
You can work around it like so:

  cd /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents
  sudo mkdir lib
  cd lib
  sudo ln -s ../Classes/classes.jar tools.jar

