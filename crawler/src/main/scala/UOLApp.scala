package patrimonium.crawler

import patrimonium.core.Acao
import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext }

import ExecutionContext.Implicits.global
import patrimonium.crawler.UOLCrawlerServices._

object UOLApp_GetTodasAcoes extends App {

  val timeout = 10.minutes
  val acoesFuture = new GetAcoesCmd().query
  val acoes = Await.result(acoesFuture, timeout)

  println(s">> Total acoes: ${acoes.size}")
  acoes.sorted.foreach(println)

}

object UOLApp_GetIntradayParaTupy3 extends App {

  val timeout = 10.minutes
  val subject = Acao("TUPY3.SA", "Tupy")

  val cotacoesFuture = new GetCotacoesIntradayCmd(subject).query
  val cotacoes = Await.result(cotacoesFuture, timeout)

  println(s">> Total cotacoes intraday: ${cotacoes.size} para $subject")
  cotacoes.sorted.foreach(println)

}

object UOLApp_GetInterdayParaTupy3 extends App {

  val timeout = 10.minutes
  val subject = Acao("TUPY3.SA", "Tupy")

  val cotacoesFuture = new GetCotacoesInterdayCmd(subject).query
  val cotacoes = Await.result(cotacoesFuture, timeout)

  println(s">> Total cotacoes interday: ${cotacoes.size} para $subject")
  cotacoes.sorted.reverse.foreach(println)

}


