package $package$
  
import $package$.app.Application

import cats.effect.*

object Main extends ResourceApp.Forever:

  override def run(args: List[String]): Resource[IO, Unit] = Application.run[IO]
