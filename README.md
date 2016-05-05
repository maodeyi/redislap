Java redislap tool
Support jedis currently
============================
   
Getting started. 

1. package this project with maven

2. run the benchmark

    java -jar redislap-1.0-SNAPSHOT-jar-with-dependencies.jar
        -h 127.0.0.1 -p 6379 -t 100 -r 10000 -b 15 -c 100 -op get -opTimeout 5000

3. help

    java -jar redislap-1.0-SNAPSHOT-jar-with-dependencies.jar -help