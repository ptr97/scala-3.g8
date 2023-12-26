package $package$.infrastructure.http

import $package$.common.*

import BaseHttp.ErrorResponse
import cats.effect.Async
import cats.syntax.all.*
import com.softwaremill.tagging.@@
import com.softwaremill.tagging.Tagger
import org.typelevel.log4cats.LoggerFactory
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.generic.auto.SchemaDerivation

trait BaseHttp[F[_]: Async](using loggerFactory: LoggerFactory[F]) extends JsonSupport with TapirSchemas:
  private val logger                         = loggerFactory.getLogger
  protected val apiContextPath: List[String] = List("api", "v1")

  private val jsonErrorOutOutput: EndpointOutput[ErrorResponse]       = jsonBody[ErrorResponse]
  private val failOutput: EndpointOutput[(StatusCode, ErrorResponse)] = statusCode.and(jsonErrorOutOutput)

  val baseEndpoint: PublicEndpoint[Unit, (StatusCode, ErrorResponse), Unit, Any] = endpoint.errorOut(failOutput)

  private val InternalServerError = (StatusCode.InternalServerError, "Internal server error")

  private val failToResponseData: Fail => (StatusCode, String) =
    case Fail.NotFound(msg)       => (StatusCode.NotFound, msg)
    case Fail.Conflict(msg)       => (StatusCode.Conflict, msg)
    case Fail.IncorrectInput(msg) => (StatusCode.BadRequest, msg)
    case _                        => InternalServerError

  extension [T](f: F[T])

    def toOut: F[Either[(StatusCode, ErrorResponse), T]] =
      f.map(t => t.asRight[(StatusCode, ErrorResponse)]).recoverWith { case f: Fail =>
        val (statusCode, message) = failToResponseData(f)
        logger.warn(s"Request fail: \$message") *> (statusCode, ErrorResponse(message)).asLeft[T].pure[F]
      }

object BaseHttp:

  final case class ErrorResponse(error: String)

trait TapirSchemas extends SchemaDerivation:
  given schemaForTagged[U, T](using uc: Schema[U]): Schema[U @@ T] = uc.asInstanceOf[Schema[U @@ T]]

  given taggedPlainCodec[U, T](using uc: PlainCodec[U]): PlainCodec[U @@ T] =
    uc.map(_.taggedWith[T])(identity)
