
Overview:
In this example we will see how services are deployed
How Sbb handle DiameterRo events like : RoCreditControlRequest, RoCreditControlAnswer


Prerequisites:
  ++ Deploy Diameter-base Resource Adaptor
  ++ Deploy Diameter-Ro Resource Adaptor
  ++ Deploy Diameter Stack
  ++ Download seagull to simulate diameter traffic
  
How to deploy this example:
 ++ Go to jain-slee.diameter/example/diameter-ro and enter "mvn clean install" .
 ++ Copy the jar file to JBOSS deployment directory.
 ++ Check and confirm the SBB is installed successfullly to JAIN-SLEE through console http://localhost:8080/slee-management-console/
 
How to configure diameter stack at jain-slee:

 ++ cp jain-slee.diameter/example/diameter-ro/diameter-stack-config* <JBOSS_WILDFLY_DIR>/modules/system/layers/base/org/restcomm/diameter/lib/main/config/
 ++ start jain-slee
 
How to generate diameter traffic by seagull:

 ++ Download seagull and unzip at jain-slee.diameter/example/diameter-ro/seagull
 ++ Go to jain-slee.diameter/example/diameter-ro/seagull/scenario
 ++ export LD_LIBRARY_PATH=<pathto>jain-slee.diameter/example/diameter-ro/seagull/
 ++ mkdir <pathto>jain-slee.diameter/example/diameter-ro/seagull/logs
 ++ touch ../scenario/msisdn.csv
 ++ ../seagull -bg -conf ../config/mem_conf.client.xml -dico ../config/base_cc.xml -scen ../scenario/ccr-cca.long.client.xml -log ../logs/car-caa.client.log -llevel T -ctrl 127.0.0.1:9090 -ctrldicopath ../config
 
 

