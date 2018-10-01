# Analise de logs com spark

## Execução

###### Na IDE

```
Em VM Options colocar  o valor -Dspark.master=local[*]
Em Program Arguments colocar o valor src/main/resources/dataset/*

Se for windows
 Em Enviroment Variables criar a variável HADOOP_HOME e colocar onde esta o arquivo winutils
 HADOOP_HOME=C:\Users\tiago.silva.de.jesus\IdeaProjects\testespark\src\main\resources\winutils
```

###### NO Cluster

```
Criar o diretório /jobs/resources/dataset/
hdfs dfs -mkdir -p /jobs/resources/dataset

Colocar o jar testespark-1.0.jar dentro de /jobs e os logs em /jobs/resources/dataset

Executar o comando em /jobs
spark-submit --master yarn --class TesteLogs testespark-1.0.jar hdfs://jobs/dataset/resources/dataset/*
```


####Referências:
```
http://spark.apache.org/
https://community.hortonworks.com/articles/34362/parsing-apache-log-files-with-spark.html
```
