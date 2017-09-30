Grader: 
	javac -cp sheets/libs/*:sheets/*:. Grader.java
run:
	MY_ENV=bla java -cp sheets/libs/*:sheets/*:. Grader nopush
push:
	java -cp sheets/libs/*:sheets/*:. Grader push
clean:
	rm Grader.class
