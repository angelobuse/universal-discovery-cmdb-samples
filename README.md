# Sample Code for CMS REST API

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
	- [Quick Start](#quick-start)
	- [Discovery](#discovery)
	- [Integration](#integration)

## Background

This is a project for users who are using UCMDB and want to know how to manipulate Discovery through REST API. 



## Install

1. Prerequisite: This project requires JDK 1.8 (or later) and Maven 3. 
   Make sure you already have them on your local machine. 
2. Clone the project so that you have a local copy. 
3. Go to the project root folder on your local mahine, open Command Prompt and run the following command to build the project into a JAR file:
   ```sh
   mvn package
   ```
   When the building process completes, you should see the 'BUILD SUCCESS' message at the end.
4. Go to the 'target' folder, you should find the JAR file you just built.



## Usage

This section explains how to use CMS REST API code samples. 

In order to run the code samples properly, make sure you have enabled the new zone-based discovery solution in CMS UI.

### Quick Start

This use case shows the easy usage of CMS REST API with a simple operation: Login. 

1. Go to the project root folder on your local mahine, open Command Prompt and run the following command:

   ```sh
   java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.quickstart.LogInToUCMDB
   ```
2. Provide necessary information when prompted, for example, hostname/IP of the UCMDB server, port of the UCMDB server, username, and password.
3. The response shows that the sample code has connected to the UCMDB server successfully, and you should see the 'LOG IN TO SERVER' as well as the token retured. 
   Then you can need this token for REST API operations.


### Discovery
* Create Zone for AWS Discovery  

  This use case shows how to create a zone for AWS discovery with REST API.  
  
  1. Go to the project root folder on your local mahine, and open the following file: 
     data\payload\CreateAWSZone_1.json
     
  2. Modify the AWS credential parameters' values as necessary, and save the file.
  
  3. From the project root folder, run the following command:
  
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CreateAWSZone
     ```
  4. Provide necessary information when prompted.
  
  5. When the sample code execution is completed, you should be able to see in the log what steps the sample has executed as well as execution result for each step: 
  
     a. LOG IN TO SERVER
     
     b. ENSURE ZONE BASED DISCOVERY IS ENABLED
     
     c. CREATE AWS CREDENTIAL
     
     d. CREATE AWS CREDENTIAL GROUP
     
     e. CREATE AWS JOB GROUP
     
     f. CERATE AWS ZONE
     
  6. Go to the Discovery module in CMS UI, you should see an AWSZONE is created successfully.  
  
    

* Create Zone for Inventory Discovery  

  This use case shows how to create a zone for inventory discovery on a Windows probe with REST API.
  
  1. Open each of the following files and modify values of zone configuration parameters as necessary:

     data\payload\CreateInventoryZoneForWindowsProbe_1.json - update the 'range' section

     data\payload\CreateInventoryZoneForWindowsProbe_2.json - update the 'ipRanges' section

     data\payload\CreateInventoryZoneForWindowsProbe_3.json - update the 'credential' section

  2. From the project root folder on your local machine, run the following command:
    
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CreateInventoryZoneForWindowsProbe
     ```


* Get communication log on trigger 

  This use case shows how to re-run a trigger of zone, wait until it finishes, and then retrieve communication log.

  From the project root folder on your local machine, run the following command:

  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.GetComlogOnTrigger
  ```

  

* Get zone result  

  This use case shows how to activate a zone, then get the statistics and result of the zone.

  From the project root folder on your local machine, run the following command:

  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.GetZoneResult
  ```

  

* Rerun failed triggers  

  This use case shows how to activate a zone, and then rerun the trigger if its status is Error. 

  From the project root folder on your local machine, run the following command:

  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.RerunFailedTriggers
  ```

    

* Delete zone  
  
  This use case shows how to delete a zone, if the used profile is not out-of-the-box (OOTB) and is only referenced by this zone, also deletes it.

  From the project root folder on your local machine, run the following command:
  
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.DeleteZoneWithAllReference
  ```

  

### Integration

* Job sync  

  This use case runs full sync and then delta sync of all jobs under inactive integration points.
  
  1. Create an integration point that includes both push and population jobs in it. Do not activate it immediately.
  
  2. From the project root folder on your local machine, run the following command:
    
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.JobSyncScenarioSample
     ```



* Troubleshooting  
  
  This use case performs a simple troubleshooting by getting connection status and job status.
    
  From the project root folder on your local machine, run the following command:
  
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.SimpleTroubleshootingScenarioSample
  ```


​    
* Viewing CI statistics  

  This use case views CI statistics for a specific job in a specific integration point.
    
  From the project root folder on your local machine, run the following command:
  
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewCIStatisticsScenarioSample
  ```


​    
* Viewing IntegrationPoints  

  This use case allows you to get the following information of specific sample points: status, statistics, details for each sample point. Then you can view the job  list and status of each job.
    
  From the project root folder on your local machine, run the following command:
    
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewIntegrationPointsScenarioSample
  ```

​    
