# Detect DDOS with ESPER CEP Engine with java code and netflow traffic

this writeup shows you how to build a CEP(Complex Event Processing) engine for your network
![alt text](https://raw.githubusercontent.com/Alikhoshkholgh/ESPER_CEP_Engine_java_netflow/main/CEP%20engine%20based%20on%20esper.jpeg)

+ this program only works with netflow traffic, at first you need to generate netflow traffic, and create a json format for your netflow traffic and then pass this to my application

+ ## About ESPER:
  + **think of as a reversed database** and a in-memory database, what do i mean by that? in regular databases you have your information staticly stored in some kind of memory, and then you can send a request for this information and after that you get back your information from database, BUT **in this case we have our requests(EPL) stored somewhere and then having these requests we can pass our stream of data to this requests and catch our prefered information**, Exactly like fishes that flowing through river

  + 1- "EPL" is the way we call this stored requests 
  + 2- think of netflow traffic as that information which is flowing through this rules(EPL) and we can catch some statstics from this stream and after that measure an intrusion based on this


+ ## Setup:
  + **well you need to change some things in the code**:
    + 1- in **[javaCode/CallbackHandler.java](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/javaCode/CallbackHandler.java)**, you need to specify the **syslog server IP address** and also the **port** that it listening to
    + 2- in **[javaCode/FirstEsper.java](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/javaCode/FirstEsper.java)**, you need to specify the **port** number that you want to listen for **netflow traffic**
    + 3- in **[javaRun.sh](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/javaRun.sh)**, you need to specify the **path** that your project is currently stored in it
  + **create EPL**:
    + in EPL file you need to write your EPLs and i have already placed some EPLs here as an example for you. 
        + for more information about EPLs [click this](http://esper.espertech.com/release-7.1.0/esper-reference/html/gettingstarted.html#gettingstarted_steps_4)
    
+ ## Run:
  + **option-1(from source code)**: there are two bash scripts you can use them to compile and run the code:
    + Compile: javaCompile.sh
    + run: javaRun.sh
    
  + **option-2(docker file)**: you can run with docker file
    + Setup_with_docker.docker

+ ## References:
  + [medium](https://medium.com/@bruno.felix/complex-event-processing-with-esper-core-concepts-f97394b39c07)
  + [Esper github](https://github.com/espertechinc/esper)
  + [learning StepByStep](http://esper.espertech.com/release-7.1.0/esper-reference/html/gettingstarted.html)
