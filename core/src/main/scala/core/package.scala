package patrimonium

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

  case class Cotacao(
    acao: Acao,
    data: Instant,
    preco: Double,
    maximo: Double,     // option?
    minimo: Double,     // option?
    variacao: Double,   // option?
    volume: Long        // option?
  ) extends Ordered[Cotacao] {

    override def compare(that: Cotacao) =
      this.data compareTo that.data

  }

}
