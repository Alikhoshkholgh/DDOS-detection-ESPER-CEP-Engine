#!/bin/bash

path="/opt/service"

dependency_list="$path/jarDependency/hamcrest-core-1.1.jar:$path/jarDependency/junit-4.10.jar:$path/jarDependency/antlr4-runtime-4.7.2.jar:$path/jarDependency/commons-compiler-3.1.0.jar:$path/jarDependency/esper-common-8.7.0.jar:$path/jarDependency/esper-compiler-8.7.0.jar:$path/jarDependency/esper-runtime-8.7.0.jar:$path/jarDependency/janino-3.1.0.jar:$path/jarDependency/log4j-1.2.17.jar:$path/jarDependency/slf4j-log4j12-1.7.30.jar:$path/jarDependency/slf4j-api-1.7.30.jar:$path/jarDependency/esper-7.1.0.jar:$path/jarDependency/json-20210307.jar:./jarDependency/sqlite-jdbc-3.7.2.jar" 


java -classpath $dependency_list:$path/javaClasses/ FirstEsper 
