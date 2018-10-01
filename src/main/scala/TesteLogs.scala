import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

case class LogRecord(host: String, clientIdentity: String, user: String, dateTime: String, request:String, statusCode:String, bytesSent:Int )

object TesteLogs {
  val PATTERN = """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] ("\S+ \S+ \S+") (\d{3}) ((\d+)|(\S+))""".r

  def parseLogLine(log: String): LogRecord = {
    try {
      val res = PATTERN.findFirstMatchIn(log)

      if (res.isEmpty) {
        LogRecord("Empty", "-", "-", "", "", "", -1)
      }
      else {
        val m = res.get

        LogRecord(m.group(1), m.group(2), m.group(3), m.group(4),
          m.group(5), m.group(6), if( m.group(7).equals("-") ) 0  else m.group(7).toInt )
      }
    } catch {
      case e: Exception =>
        println("Exception on line:" + log + ":" + e.getMessage);
        LogRecord("Empty", "-", "-", "", "-", "", -1)
    }
  }

  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

    val sparkConf = new SparkConf().setAppName("Teste Spark Log")
    val sc = new SparkContext(sparkConf)

    val logFile = sc.textFile(args(0))
    //   val logFile = sc.textFile("src/main/resources/dataset/*")
    //   val logFile = sc.textFile("src/main/resources/access_log_Aug95")
    val accessLogs = logFile.map(parseLogLine).filter(!_.host.equals("Empty"))

    try {
      val sqlContext = new SQLContext(sc)
      import sqlContext.implicits._

      val dsLog = accessLogs.toDS().cache()

      println("Quantidade de Registros: " + dsLog.count())

      println("Qtd hosts únicos: " +  dsLog.groupBy("host").count().count() )

      val host404 = dsLog.filter(x => x.statusCode == "404")
      println("Qtd hosts 404: " +  host404.groupBy("host").count().count() )

      println("Os 5 URLs que mais causaram erro 404" )
      import org.apache.spark.sql.functions._
      dsLog
        .groupBy("host")
        .agg(count("host").as("qtd_hosts404"))
        .orderBy(col("qtd_hosts404").desc)
        .show(5,false)

      println("Quantidade de erros 404 por dia.")
      val errosPorDia =
        host404.rdd.map(x => (x.dateTime.substring(0,11),1))//.reduce( (a,b) => (a+b) )
          //          .reduceByKey(_+_)
          //          .foreach(println)
          .toDF("data","qtd")
          .groupBy("data")
          .count()

      errosPorDia.describe("count")show()

      errosPorDia.show(100)

      val qtdBytesRetornados = dsLog.rdd.map(x => x.bytesSent).reduce((a, b) => (a+b) )
      println("O total de bytes retornados: " +  qtdBytesRetornados  )

      dsLog.unpersist()
    } catch {
      case e: Exception =>
        e.printStackTrace();
    }

    sc.stop()

  }
}