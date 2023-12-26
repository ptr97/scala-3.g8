package $package$.common

import java.time.LocalDate

import com.softwaremill.tagging.@@
import io.circe.*
import io.circe.Decoder.Result
import io.circe.generic.AutoDerivation
import io.circe.syntax.*
import sttp.tapir.Tapir
import sttp.tapir.json.circe.TapirJsonCirce

trait JsonSupport extends AutoDerivation with Tapir with TapirJsonCirce:
  val noNullsPrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)

  given taggedStringEncoder[U]: Encoder[String @@ U] = Encoder.encodeString.asInstanceOf[Encoder[String @@ U]]
  given taggedStringDecoder[U]: Decoder[String @@ U] = Decoder.decodeString.asInstanceOf[Decoder[String @@ U]]

object JsonSupport:
  def unwrappedEncoder[T, U: Encoder](f: T => U): Encoder[T] = t => f(t).asJson
  def unwrappedDecoder[T, U: Decoder](f: U => T): Decoder[T] = hCursor => hCursor.value.as[U].map(f)

  def unwrappedCodec[T, U: Encoder: Decoder](f: U => T)(g: T => U): Codec[T] =
    Codec.from(unwrappedDecoder(f), unwrappedEncoder(g))

  given localDateCodec: Codec[LocalDate] = new Codec[LocalDate]:
    override def apply(c: HCursor): Result[LocalDate] = c.value.as[String].map(LocalDate.parse)
    override def apply(localDate: LocalDate): Json    = localDate.toString.asJson
