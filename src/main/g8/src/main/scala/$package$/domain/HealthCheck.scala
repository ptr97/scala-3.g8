package $package$.domain

trait HealthCheck[F[_]]:
  def healthCheck: F[Unit]
