package uk.gov.hmcts.reform.sendletter

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter.actions.{LettersService, S2s}

import scala.concurrent.duration._

class PdfWithCopiesSimulation extends Simulation {

  val config: Config = ConfigFactory.load()

  private val duration = config.getInt("duration_in_minutes")
  private val serviceCount = config.getInt("service_count")
  private val maxRetry = config.getInt("max_retry")

  setUp(
    scenario("Create letters v3")
      .exec(S2s.leaseServiceToken)
      .during(duration.minutes)(
        exec(
          LettersService.createV3,
          pause(5.seconds, 10.seconds)
        ).tryMax(maxRetry) {
            pause(2.seconds, 5.seconds)
            exec(LettersService.checkStatus)
        }.exitHereIfFailed
      )
      .inject(
        rampUsers(serviceCount).during(5.seconds)
      )
  ).protocols(http.baseUrl(config.getString("baseUrl")))

}
