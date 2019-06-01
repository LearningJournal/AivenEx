
  
## Kafka Demo Application for Aiven.IO Platform

 This project is a demo application for a Kafka Producer and Consumer.    
This demo application demonstrates the following.    
1. A Kafka Producer - *DemoProducer* reads *StockData* from CSV file and sends them to Kafka Topic as JSON.    
2. A Kafka Consumer - *DemoConsumer* reads JSON StockData from Kafka Topic and sends them to Postgres Database table *stock_data*.     
3. ShowPGTable - Reads data from the Postgres table and writes to console    
4. This example also contains a JSON Serializer and Deserializer Classes.    
    
The project contains the following directories    
1. src - This is the primary source code directory.    
2. data - This directory contains a sample data file CSV.    
3. scripts - This directory contains SQL script for creating PostgreSQL table   
3. configs - This directory contains the following files    
   * app.configs - This file contains application specific configurations    
   *  kafka.producer.properties - This file includes Kafka producer configurations    
   *  kafka.consumer.properties - This file includes Kafka consumer configurations    
   *  pgsql.configs - This file contains Postgres configuration   
        
## How to test this Demo Application
You can build and execute the demo application manually from your local machine. You can also load the project in your IDE such as IntelliJ IDEA and execute directly from the IDE. Use the following steps to execute the demo application.    

### Clone Project Files  
Clone or download the source code folder from GitHub.   

### Create PostgreSQL Database 
1. Login to Aiven.io console using https://console.aiven.io  
2. Create a new service for PostgreSQL 10  
3. Create a new PostgreSQL database using *Databases* tab in your Aiven PostgreSQL service page. This example creates a database named ***kafka-pg-demo***. We recommend following the same name to avoid confusion.  
4. Create a new PostgreSQL user using *Users* tab in your Aiven PostgreSQL service page. This example creates a user named ***prashant***. We recommend following the same name to avoid confusion.  

### Configure your project for PostgreSQL connection 
1. Modify ***configs/pgsql.configs*** file in your project folder.   
   * Change the ***jdbc.url*** value to reflect your PostgreSQL service ***hostname:port***. You can get these details from the Aiven PostgreSQL service overview page.   
   * Make sure that ***jdbc.url*** contains the correct database name. If you created a database as ***kafka-pg-demo***, you do not need to change the database name in the URL.  
   * Change the ***user*** and ***password*** values in the ***configs/pgsql.configs***. You can get these details from the Aiven PostgreSQL Service *Users* tab.  
   * Connect to your Aiven PostgreSQL database from your local machine using ***psql*** command line tool. You can use the Service URL for connecting to Aiven PostgreSQL database that you created. You may have to modify default *username:password* and *database* name in the service URL with correct values.  
   * Once connected, create ***stock_data*** table using ***scripts/create-table.sql*** in your download project folder.  
   * Verify your table is created. You should see the result, as shown below.  
        ``` sql  
	 kafka-pg-demo=> SELECT count(*) from stock_data; count         -------  
	 0 (1 row) 
	 ``` 
	* You are now ready with your PostgreSQL setup and configuration  
	
### Create Kafka Cluster  
1. Login to Aiven.io console using https://console.aiven.io  
2. Create a new service for Kafka 2.1  
3. Create a new Kafka Topic named ***kafka-pg-demo***. You can create a topic using Aiven Kafka Service *Topics* tab.   

### Configure your project for Kafka connection  
1. Download ***service.cert***, ***service.key***, and ***ca.pem*** file from *Aiven Kafka -> Service Overview -> Connection Parameters* section.  
2. Execute below command on your shell to create new key-store and trust-store for your project. All three downloaded files (*service.cert*, *service.key*, and *ca.pem*) must be in your current directory for the below commands to work correctly.  
    ```  
	 openssl pkcs12 -export -inkey service.key -in service.cert -out 	client.keystore.p12 -name service_key 
	 keytool -import -file ca.pem -alias CA -keystore client.truststore.jks 			
	 ```
3. Above command will ask for the password. This example sets password as *secret*.  
4. Once executed, these command will create ***client.keystore.p12*** and ***client.truststore.jks*** files in your current directory. Copy these new files in your *kafka-pg-demo/configs* directory of your project.  
5. Modify ***configs/kafka.producer.properties*** file in your project folder.  
   * Change ***bootstrap.servers*** to reflect your Aiven Kafka broker ***hostname:port***. You can get this information from Aiven Kafka Service Overview page.  
6. Modify ***configs/kafka.consumer.properties*** file in your project folder.  
   * Change ***bootstrap.servers*** to reflect your Aiven Kafka broker ***hostname:port***. You can get this information from Aiven Kafka Service Overview page.  
7. You are now ready to build and execute this example.  

### Build and execute your Project  
1. This Demo application is a Maven project. You can package an uber-jar using below command.    
    ```sh    
    $ mvn clean package    
    ```    
	 You should get something similar to below screen     
	```
	[INFO] ------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------
	[INFO] Total time:  5.439 s
	[INFO] Finished at: 2019-06-01T00:54:24+05:30
	[INFO] ------------------------------------------------------------  
	```
	 The build generates an uber-jar named ***kafka-pg-demo-1.0.jar*** 2. Copy ***kafka-pg-demo-1.0.jar*** from ***kafka-pg-demo/target*** directory to your project home directory. 
2. Execute Kafka Producer using below command.  
    ```  
	 java -cp kafka-pg-demo-1.0.jar io.aiven.examples.DemoProducer     

	[2019-06-01 14:02:51,605] INFO Start Sending Messages to Kafka... (io.aiven.examples.DemoProducer)  
	[2019-06-01 14:02:56,430] INFO Finished Sending Messages to Kafka (io.aiven.examples.DemoProducer) 
	```
3. Execute Kafka Consumer using below command.  
    ```  
	java -cp kafka-pg-demo-1.0.jar io.aiven.examples.DemoConsumer     
	
	[2019-06-01 14:06:06,326] INFO Start Reading Messages from Kafka... (io.aiven.examples.DemoConsumer)  
	[2019-06-01 14:06:07,143] TRACE Opening Connection to PG (io.aiven.examples.PGRepository) 
	[2019-06-01 14:06:07,635] TRACE Connection Established to PG (io.aiven.examples.PGRepository) 
	[2019-06-01 14:06:15,063] INFO Saved a batch of  500 Records (io.aiven.examples.PGRepository) 
	[2019-06-01 14:06:15,218] INFO Saved a batch of  500 Records (io.aiven.examples.PGRepository) 
	[2019-06-01 14:06:15,377] INFO Saved a batch of  500 Records (io.aiven.examples.PGRepository) 
	[2019-06-01 14:06:15,493] INFO Saved a batch of  407 Records (io.aiven.examples.PGRepository) 
	```
4. Once you see that some messages are successfully saved, you can press *CTRL+D* to terminate your Kafka Consumer.  
5. You can see data from your PostgreSQL table using below command.  
    ```  
	 java -cp kafka-pg-demo-1.0.jar io.aiven.examples.ShowPGTable 
	 ```
6. Alternatively, you can connect to PostgreSQL using psql and query the table.  
	 ```  
	kafka-pg-demo=> SELECT count(*) from stock_data; 
	count    
	-------  
	 1907 (1 row) 
	 ```
	 