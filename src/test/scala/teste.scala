import org.scalatest._
import Matchers._

object teste extends App{

  val PATTERN = """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] ("\S+ \S+ \S+") (\d{3}) ((\d+)|(\S+))""".r

  val logText = """in24.inetnebr.com - - [01/Aug/1995:00:00:01 -0400] "GET /shuttle/missions/sts-68/news/sts-68-mcc-05.txt HTTP/1.0" 200 1839"""
  val logPattern = PATTERN.findFirstMatchIn(logText)

  logPattern.isEmpty should be (false)


}