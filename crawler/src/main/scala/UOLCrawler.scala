package patrimonium.crawler

import com.themillhousegroup.scoup.Scoup
import java.net.URL
import java.time.chrono.Chronology
import java.time.format.{ DateTimeFormatter, DateTimeFormatterBuilder }
import java.time.temporal.ChronoField
import java.time.{ Instant, ZoneId }
import org.jsoup.nodes.Element
import patrimonium.core.CrawlingServices._
import patrimonium.core._
import scala.concurrent.ExecutionContext

object UOLCrawlerServices {

  private val baseURL = "http://cotacoes.economia.uol.com.br"
  private val listagem = s"$baseURL/acoes-bovespa.html?exchangeCode=.BVSP&page=1&size=2000"
  private val intraday = s"$baseURL/acao/cotacoes-diarias.html?codigo=%s&size=2000&page=1"
  private val historico = s"$baseURL/acao/cotacoes-historicas.html?codigo=%s&size=365&page=1&period="

  private lazy val dateTimeFmt = DateTimeFormatter
    .ofPattern("d/M/yyyy H:mm")
    .withZone(ZoneId.of("UTC"))

  private lazy val dateFmt = new DateTimeFormatterBuilder()
    .appendPattern("d/M/yyyy")
    .parseDefaulting(ChronoField.SECOND_OF_DAY, 0)
    .toFormatter()
    .withZone(ZoneId.of("UTC"))

  private implicit def elementToDouble(el: Element) =
    el.html.replace(",", ".").toDouble

  private implicit def elementToLong(el: Element) =
    el.html.replace(".", "").toLong

  private implicit def elementToInstant(el: Element) =
    if (el.html.contains(" :"))
      Instant.from(dateTimeFmt.parse(el.html))
    else
      Instant.from(dateFmt.parse(el.html))

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

  class GetCotacoesInterdayCmd(val acao: Acao) extends GetCotacoesInterday {

    override def query(implicit ec: ExecutionContext) = {
      val url = historico.format(acao.ticker)
      Scoup parse (url) map {
        _ select ("table#tblInterday") flatMap {
          _ select ("tbody tr") map rowToCotacaoInterday(acao)
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

    Cotacao(acao,
      data = td.get(0),
      preco = td.get(1),
      variacao = td.get(2),
      maximo = td.get(4),
      minimo = td.get(5),
      volume = td.get(6)
    )
  }

  private def rowToCotacaoInterday(acao: Acao)(row: Element) = {
    val td = row.select("td")

    Cotacao(acao,
      data = td.get(0),
      preco = td.get(1),
      variacao = td.get(4),
      maximo = td.get(3),
      minimo = td.get(2),
      volume = td.get(6)
    )
  }

}
