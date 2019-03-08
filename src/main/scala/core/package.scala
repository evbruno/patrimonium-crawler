import java.net.URL
import java.time.Instant

package object core {

  case class Acao(
    ticker: String,
    nome: String,
    descricao: Option[String] = None,
    href: Option[URL] = None,
    mercado: Option[String] = None,
    isin: Option[String] = None
  ) extends Ordered[Acao] {

    override def compare(that: Acao) =
      this.ticker compareTo that.ticker

  }

  sealed abstract class CotacaoSpec {
    def acao: Acao
    def data: Instant
    def preco: Double
    def variacao: Double
  }

  case class Cotacao(
    acao: Acao,
    data: Instant,
    preco: Double,
    variacao: Double
  ) extends CotacaoSpec
    with Ordered[Cotacao] {

    override def compare(that: Cotacao) =
      this.data compareTo that.data
  }

  case class CotacaoIntraday(
    val acao: Acao,
    data: Instant,
    preco: Double,
    maximo: Double,
    minimo: Double,
    variacao: Double,
    volume: Long
  ) extends CotacaoSpec
    with Ordered[CotacaoIntraday] {

    override def compare(that: CotacaoIntraday) =
      this.data compareTo that.data

  }

}
