package $package$

import munit.FunSuite

final class SampleIntegrationSpec extends FunSuite:

  test("example integration test that succeeds"):
    val obtained = 42
    val expected = 42
    assertEquals(obtained, expected)
