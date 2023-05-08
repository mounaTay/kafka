#!/bin/bash

"${SPARK_HOME}"/bin/spark-submit \
	--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:/opt/app/log4j.properties" \
	--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/opt/app/log4j.properties" \
	--class "$1" /opt/app/target/spark-stream_2.12-1.0.jar 
