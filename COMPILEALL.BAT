cd bin
del /s *.class
cd ..
set CLASSPATH=.
javac -target 1.1 -deprecation -d bin *.java
