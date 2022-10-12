# Detect DDOS with ESPER CEP Engine with java code and netflow traffic

this writeup shows you how to build a CEP(Complex Event Processing) engine for your network
![alt text](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/CEP%20engine%20based%20on%20esper.jpeg)

+ this program only works with netflow traffic, at first you need to generate netflow traffic, and create a json format for your netflow traffic and then pass this to my application

+ ## About ESPER:
  + **think of as a reversed database** and a in-memory database, what do i mean by that? in regular databases you have your information staticly stored in some kind of memory, and then you can send a request for this information and after that you get back your information from database, BUT **in this case we have our requests(EPL) stored somewhere and then having these requests we can pass our stream of data to this requests and catch our prefered information**, Exactly like fishes that flowing through river

  + 1- "EPL" is the way we call this stored requests 
  + 2- think of netflow traffic as that information which is flowing through this rules(EPL) and we can catch some statstics from this stream and after that measure an intrusion based on this

+ ## DDOS detection Considerations:
  + with this approach, because of the netflow traffic that we sent to the esper engine, **we dont have any information about applicatoin layer data**, note that application layer data does not sent with netflow traffic, so **we are not able to detect any DDOS attack at application layer**. so we can say that this approach only **works for DDOS attacks at protocol and volumetric layer**.


+ ## Setup:
  + Collector: you should send the netflow traffic from your netflow agent to the collector, all you need to have about collector is in **[./IPfix-Collector2](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/tree/main/IPfix-Collector2)**, in this case i used [ipfixcol2](https://github.com/CESNET/ipfixcol2). you can modify the conf.xml file in this directory and simply set it up with this [dockerfile](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/IPfix-Collector2/dockerfile.docker).
  
  + **source code**: there is some variables in **[javaClasses/configuration.xml](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/javaClasses/configuration.xml)** that you need to change it: 
    + 1- core: 
      + program-path: set this to the path that your project is placed in it
      + listening-port: listening port for receiving netflow traffic
      + module="EPL-filename": in 'EPL' Folder you have your EPL modules, with this variable you can choose wich one do you want to imported to the engine
    
    + 2- database:
      + record: true/false, whether you want to record infomation in database or not
      + database-path: absolute database path
      + table-name: arbitrary names
      + database-name: arbitrary names
     
    + 3- syslog
      + send-syslog: true/false, whether you want send records to syslog server or not
      + destinationIP: syslog ip address
      + destinationPort: syslog port number
         
  + you need to change the **'path'** variable in [./javaRun.sh](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/javaRun.sh)   
    
  + **create EPL**:
    + in EPL Folder you need to write your EPLs and i have already placed some EPLs here as an example for you. 
        + for more information about EPLs [click this](http://esper.espertech.com/release-7.1.0/esper-reference/html/gettingstarted.html#gettingstarted_steps_4)
    + after writing your EPLs in a file in the [EPL](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/tree/main/EPLs) Folder, you need to specify the EPL file name that you want to be imported in the Esper Engine at the [target-EPL-names](https://github.com/Alikhoshkholgh/DDOS-detection-ESPER-CEP-Engine/blob/main/EPLs/target-EPL-names)
    
    
+ ## Run:
  + **option-1(from source code)**: there are two bash scripts you can use them to compile and run the code:
    + Compile: javaCompile.sh
    + run: javaRun.sh
    
  + **~~option-2(docker file)~~**: ~~you can run with docker file~~
    + ~~Setup_with_docker.docker~~

+ ## References:
  + [medium](https://medium.com/@bruno.felix/complex-event-processing-with-esper-core-concepts-f97394b39c07)
  + [Esper github](https://github.com/espertechinc/esper)
  + [learning StepByStep](http://esper.espertech.com/release-7.1.0/esper-reference/html/gettingstarted.html)
