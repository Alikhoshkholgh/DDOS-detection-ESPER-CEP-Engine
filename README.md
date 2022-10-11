# ESPER CEP Engine with java code and netflow traffic

this writeup shows you how to build a CEP(Complex Event Processing) engine for your network
![alt text](https://raw.githubusercontent.com/Alikhoshkholgh/ESPER_CEP_Engine_java_netflow/main/CEP%20engine%20based%20on%20esper.jpeg)

+ this program only works with netflow traffic, at first you need to generate netflow traffic, and create a json format for your netflow traffic and then pass this to my application

+ ## About Esper:
  + **think of as a reversed database** and a in-memory database, what do i mean by that? in regular databases you have your information staticly stored in some kind of memory, and then you can send a request for this information and after that you get back your information from database, BUT **in this case we have our requests(EPL) stored somewhere and then having these requests we can pass our stream of data to this requests and catch our prefered information**, Exactly like fishes that flowing through river

  + 1- "EPL" are the way we call this stored requests 
  + 2- think of netflow traffic as that information which is flowing through this rules(EPL) and we can catch some statstics from this stream and after that measure an intrusion based on this


+ ## How to use:
  + option-1(from source code): there are two bash scripts you can use them to compile and run the code:
    + Compile: javaCompile.sh
    + run: javaRun.sh
    
  +option-2(docker file): you can run with docker file
    + Setup_with_docker.docker
