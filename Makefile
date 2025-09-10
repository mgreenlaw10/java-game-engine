
SOURCES = $(wildcard src/**/*.java)

MAIN_CLASS = src.Main

CFLAGS = -Xlint:unchecked

all: 
	javac $(CFLAGS) -d bin $(SOURCES) src/Main.java
	java -cp bin $(MAIN_CLASS)

.PHONY: norun
norun:
	javac $(CFLAGS) -d bin $(SOURCES) src/Main.java

.PHONY: clean
clean:
	rmdir /S /Q bin
