#Javac
JC = javac
JCFLAGS = -g

#Java
J = java
JFLAGS =

#JSON-Simple dependency
JSON-SIMPLE = lib/json-simple-1.1.1.jar

#Directories for source and output
SRCDIR = src
OUTPUTSRCDIR = bin/main/java
RESDIR = $(SRCDIR)/main/res
RESOUTPUTDIR = bin/main/

#The main class
MAIN = org.textfighter.TextFighter

#All of the .java files
OBJECTS = $(shell find $(SRCDIR) -name '*.java')

#Shell commands
MKDIR = mkdir
RM = rm -rf
CP = cp -r

#Javac
COMPILE = $(JC) $(JCFLAGS) -d $(OUTPUTSRCDIR) -cp $(JSON-SIMPLE) $(OBJECTS)

#Java
#RUN = cd bin/main/java/org; pwd; $(J) $(JFLAGS) -cp $(JSON-SIMPLE) $(MAIN) 
RUN = cd bin/main/java; $(J) -cp .:../../../$(JSON-SIMPLE) org.textfighter.TextFighter

#Compile the game
compile: setup $(OBJECTS) cpres
	$(JC) $(JFLAGS) -d $(OUTPUTSRCDIR) -cp $(JSON-SIMPLE) $(OBJECTS)
	$(RUN)

#Run the game
run:
	$(RUN)

#Copy the resources over to the output directory
cpres: $(RESDIR)
	$(CP) $(RESDIR) $(RESOUTPUTDIR) 

#Test new packs
test: $(OBJECTS)
	$(RUN) test

#Add the output directory
setup: 
	$(MKDIR) -p $(OUTPUTSRCDIR)

#Remove the output directory
clean:
	$(RM) $(OUTPUTSRCDIR)
