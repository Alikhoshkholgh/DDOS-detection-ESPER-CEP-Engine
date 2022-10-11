#!/bin/bash


dependency_list='./jarDependency/antlr4-runtime-4.7.2.jar:./jarDependency/commons-compiler-3.1.0.jar:./jarDependency/esper-common-8.7.0.jar:./jarDependency/esper-compiler-8.7.0.jar:./jarDependency/esper-runtime-8.7.0.jar:./jarDependency/janino-3.1.0.jar:./jarDependency/log4j-1.2.17.jar:./jarDependency/slf4j-log4j12-1.7.30.jar:./jarDependency/slf4j-api-1.7.30.jar:./jarDependency/hamcrest-core-1.1.jar:./jarDependency/junit-4.10.jar:./jarDependency/esper-7.1.0.jar:./jarDependency/json-20210307.jar:./jarDependency/sqlite-jdbc-3.7.2.jar' 

javac -classpath $dependency_list -d ./javaClasses/ ./javaCode/*.java
