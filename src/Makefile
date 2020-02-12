JC = javac
JFLAGS = -g
.SUFFIXES: .java .class

sources = $(wildcard *.java)
classes = $(sources:.java=.class)

all: $(classes)

%.class : %.java
	$(JC) $(JFLAGS) $<

clean:
	$(RM) *.class
