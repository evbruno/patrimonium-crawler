package patrimonium.crawler

import com.themillhousegroup.scoup.Scoup
import java.net.URL
import org.jsoup.nodes.Element
import patrimonium.core.Acao
import patrimonium.core.CrawlingServices.GetAcoes
import patrimonium.crawler.GuiaInvestCrawlerServices.GetAcoesCmd
import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object GuiaInvestApp_GetTodasAcoes extends App {

  import ExecutionContext.Implicits.global

  val timeout = 10.minutes
  val acoesFuture = new GetAcoesCmd().query
  val acoes = Await.result(acoesFuture, timeout)

  println(s">> Total acoes: ${acoes.size}")
  acoes.sorted.foreach(println)
}


object GuiaInvestCrawlerServices {

  private val baseURL = "https://www.guiainvest.com.br/lista-acoes/default.aspx?listaacaopage=%d"
  private val totalPaginas = 34

  class GetAcoesCmd extends GetAcoes {

    override def query(implicit ec: ExecutionContext) = {
      val paginasF = (1 to totalPaginas) map pageToFuture
      Future.reduceLeft(paginasF)(_ union _)
    }

  }

  private def pageToFuture(page: Int)(implicit ec: ExecutionContext) = {
    val p1 = baseURL.format(page)

    Scoup parse (p1) map { doc =>
      doc select ("table.rgMasterTable > tbody tr") map rowToAcao
    }
  }

  private def rowToAcao(row: Element) = {
    val td = row.select("td")
    val a = td.get(0).select("a")
    val nome = a.html
    val href = new URL(a.attr("href"))
    val codigo = td.get(1).html

    Acao(codigo, nome, href = Some(href))
  }

}


//
//object GuiaInvestApp /* extends App */extends GuiaInvestCrawler {
//
//  println("...sleeping")
//  Thread.sleep(5000)
//  println("go...")
//
//  val initMs = System.currentTimeMillis()
//
//  implicit val _ec = crawlerEC
//
//  val futures: immutable.Seq[Future[Seq[(String, String, String)]]] = (1 to TotalPaginas) map pageToAcoes
//  val acoesF = Future.reduceLeft(futures)(_ union _)
//  val acoes = Await.result(acoesF, 10.minute)
//
//  println(s"Total de ações: ${acoes.size}")
//
//  val gridF: Future[Seq[AcaoDetalhes]] = Future.sequence(acoes map dadosDaAcao)
//  val grid = Await.result(gridF, 10.minute)
//
//  println(s"----- 2) Total de ações: ${grid.size}")
//  grid.sortBy(_._1).foreach(println)
//  println(s"----- 2) Total de ações: ${grid.size}, levou ${System.currentTimeMillis() - initMs} millis")
//
//}

//trait CrawlerExecution {
//
//  implicit val crawlerEC: ExecutionContext
//
//}
//
//trait GuiaInvestCrawler extends CrawlerExecution {
//
//  // override implicit val crawlerEC = ExecutionContext.Implicits.global
//  // override implicit val crawlerEC = ExecutionContext.fromExecutorService(newWorkStealingPool(8))
//  override implicit val crawlerEC = ExecutionContext.fromExecutorService(newFixedThreadPool(16))
//
//  type AcaoGrid = (String, String, String)
//  type AcaoDetalhes = (String, String, String, String, String, Int, String)
//
//  val Paginacao = "https://www.guiainvest.com.br/lista-acoes/default.aspx?listaacaopage=%d"
//  val TotalPaginas = 34
//
//  def pageToAcoes(page: Int = 1) = {
//    val url = Paginacao.format(page)
//    (Scoup parse url).map { doc =>
//      val grid = doc.select("table.rgMasterTable")
//      val table: Elements = grid select ("tbody")
//      tableToAcoes(table)
//    }
//  }
//
//  def tableToAcoes(table: Elements): Seq[AcaoGrid] = {
//    if (table.isEmpty)
//      Seq.empty
//    else {
//      val rows: Elements = table.last.getElementsByAttributeValueContaining("class", "Row")
//      val result = rows.asScala.map { row =>
//        val cols = row.select("td")
//
//        val firstCol = cols.get(0).select("a")
//        val nome = firstCol.html()
//        val href = firstCol.attr("href")
//
//        val ticker = cols.get(1).select("td").html()
//
//        (nome, ticker, href)
//      }
//      result
//    }
//  }
//
//  def dadosDaAcao(acao: AcaoGrid): Future[AcaoDetalhes] = {
//    val (nome, ticker, href) = acao
//
//    (Scoup parse href).map { doc =>
//      val id = doc.select("span[id=lbAcaoCodigo]").html()
//      val valor = doc.select(".perfil-titulo").select("span.newchangeinicial").html()
//
//      val oscilacao = doc.select("#liOscilacao span")
//
//      val (up, perc) =
//        if (oscilacao.hasClass("changeno")) (0, "0,00%")
//        else {
//          val perc = oscilacao.html.substring(8)
//          val up =
//            if (oscilacao.hasClass("changeUp")) 1
//            else -1
//          (up, perc)
//        }
//
//      val data = doc.select("span[title=Último Negócio]").html()
//
//      (nome, ticker, href, valor, perc, up, data)
//    } recover {
//      case _: Throwable =>
//        (nome, ticker, href, "R$0,00", "0,00%", 0, "00/00/00")
//    }
//  }
//}
