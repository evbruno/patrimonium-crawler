package core

import scala.concurrent.{ ExecutionContext, Future }

object CrawlingServices {

  sealed trait GetCommand[T] {

    def query(implicit ec: ExecutionContext): Future[T]

  }

  trait GetAcoes extends GetCommand[Seq[_ <: Acao]]

  trait GetCotacaoDiaAnterior extends GetCommand[Cotacao]

  trait GetCotacoesIntraday extends GetCommand[Seq[_ <: CotacaoIntraday]] {
    val acao: Acao
  }

}
