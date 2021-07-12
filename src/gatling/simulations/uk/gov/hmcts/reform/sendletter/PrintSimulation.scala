package uk.gov.hmcts.reform.sendletter

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter.actions.{PrintService, S2s}

import scala.concurrent.duration._

class PrintSimulation extends Simulation {

  val config: Config = ConfigFactory.load()

  private val duration = config.getInt("duration_in_minutes")
  private val serviceCount = config.getInt("service_count")

  setUp(
    scenario("Print letters")
      .exec(S2s.leaseServiceToken)
      .during(duration.minutes)(
        exec(
          PrintService.printV1,
          pause(5.seconds, 10.seconds)
        )
      )
      .inject(
        rampUsers(serviceCount).during(5.seconds)
      )
  ).protocols(http.baseUrl(config.getString("baseUrl")))

}
