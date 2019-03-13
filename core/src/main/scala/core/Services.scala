package patrimonium.core

import scala.concurrent.{ ExecutionContext, Future }

object CrawlingServices {

  sealed trait GetCommand[T] {

    def query(implicit ec: ExecutionContext): Future[T]

  }

  trait GetAcoes extends GetCommand[Seq[_ <: Acao]]

  trait GetCotacoesIntraday extends GetCommand[Seq[_ <: Cotacao]] {
    val acao: Acao
  }

  trait GetCotacoesInterday extends GetCommand[Seq[_ <: Cotacao]] {
    val acao: Acao
  }

}
