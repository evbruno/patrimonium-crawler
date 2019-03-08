package crawlers

import com.themillhousegroup.scoup.Scoup
import core.CrawlingServices.{ GetAcoes, GetCotacoesIntraday }
import core._
import java.net.URL
import java.time.format.DateTimeFormatter
import java.time.{ Instant, ZoneId }
import org.jsoup.nodes.Element
import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext }

object UOLCrawlerApp extends App {

  import ExecutionContext.Implicits.global
  import UOLCrawlerServices._

  val timeout = 10 minutes

  val acoesFuture = new GetAcoesCmd().query
  val acoes = Await.result(acoesFuture, timeout)

  println(s">> total acoes: ${acoes.size}")
  acoes.sorted.foreach(println)

  val ticker = acoes find(_.ticker == "TUPY3.SA") getOrElse Acao("TUPY3.SA", "Tupy")
  val cotacoesFuture = new GetCotacoesIntradayCmd(ticker).query
  val cotacoes = Await.result(cotacoesFuture, timeout)

  println(s">> total cotacoes intraday: ${cotacoes.size}")
  cotacoes.sorted.foreach(println)
}

object UOLCrawlerServices {

  private val baseURL = "http://cotacoes.economia.uol.com.br"
  private val listagem = s"$baseURL/acoes-bovespa.html?exchangeCode=.BVSP&page=1&size=2000"
  private val intraday = s"$baseURL/acao/cotacoes-diarias.html?codigo=%s&size=2000&page=1"

  private lazy val formatter = DateTimeFormatter
    .ofPattern("d/M/yyyy H:mm")
    .withZone(ZoneId.of("UTC"))

  private implicit def elementToDouble(el: Element) =
    el.html.replace(",", ".").toDouble

  private implicit def elementToLong(el: Element) =
    el.html.replace(".", "").toLong

  private implicit def elementToInstant(el: Element) =
    Instant.from(formatter.parse(el.html))

  class GetAcoesCmd extends GetAcoes {

    override def query(implicit ec: ExecutionContext) = {
      Scoup parse (listagem) map {
        _ select ("div#resultado-busca") flatMap {
          _ select ("a") map (rowToAcao)
        }
      }
      //TODO recover ?
    }

  }

  class GetCotacoesIntradayCmd(val acao: Acao) extends GetCotacoesIntraday {

    override def query(implicit ec: ExecutionContext) = {
      val url = intraday.format(acao.ticker)
      Scoup parse (url) map {
        _ select ("table#tblIntraday") flatMap {
          _ select ("tbody tr") map rowToCotacaoIntraday(acao)
        }
      }
    }

  }

  private def extractTicker(href: String) = {
    val idx = href.indexOf("=")
    href.substring(idx + 1)
  }

  private implicit def toURL(href: String) = Some(new URL(s"$baseURL/$href"))

  private def rowToAcao(row: Element) = {
    val href = row.attr("href")
    val ticker = extractTicker(href)
    val nome = row.html()

    Acao(ticker, nome, href = href)
  }

  private def rowToCotacaoIntraday(acao: Acao)(row: Element) = {
    val td = row.select("td")

    CotacaoIntraday(acao,
      data = td.get(0),
      preco = td.get(1),
      variacao = td.get(2),
      maximo = td.get(4),
      minimo = td.get(5),
      volume = td.get(6)
    )
  }

}
