FROM spark-hadoop

ARG spark_jars=jars

RUN set -ex && \
   apk upgrade --no-cache && \
   mkdir -p $SPARK_HOME/work-dir && \
   touch $SPARK_HOME/RELEASE && \
   rm /bin/sh && \
   ln -sv /bin/bash /bin/sh

ADD https://repo1.maven.org/maven2/org/apache/kafka/kafka_2.12/3.2.1/kafka_2.12-3.2.1.jar $SPARK_HOME/jars/
ADD https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.2.1/kafka-clients-3.2.1.jar $SPARK_HOME/jars/
ADD https://repo1.maven.org/maven2/io/spray/spray-json_2.12/1.3.2/spray-json_2.12-1.3.2.jar $SPARK_HOME/jars/
ADD https://repo1.maven.org/maven2/org/apache/spark/spark-streaming-kafka-0-10_2.12/3.3.1/spark-streaming-kafka-0-10_2.12-3.3.1.jar $SPARK_HOME/jars/
ADD https://repo1.maven.org/maven2/org/apache/spark/spark-token-provider-kafka-0-10_2.12/3.3.1/spark-token-provider-kafka-0-10_2.12-3.3.1.jar $SPARK_HOME/jars/
ADD https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.12/3.3.1/spark-sql-kafka-0-10_2.12-3.3.1.jar $SPARK_HOME/jars/
ADD https://repo1.maven.org/maven2/org/apache/commons/commons-pool2/2.11.1/commons-pool2-2.11.1.jar $SPARK_HOME/jars/

ADD run.sh /opt/app/run.sh
ADD src/main/resources/log4j.properties /opt/app/log4j.properties
ADD target/scala-2.12/spark-stream_2.12-1.0.jar /opt/app/target/spark-stream_2.12-1.0.jar

ENTRYPOINT ["/opt/app/run.sh"]
