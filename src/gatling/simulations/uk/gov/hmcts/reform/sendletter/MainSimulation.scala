package uk.gov.hmcts.reform.sendletter

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter.actions.{LettersService, S2s}

import scala.concurrent.duration._

class MainSimulation extends Simulation {

  val config: Config = ConfigFactory.load()

  setUp(
    scenario("Create letters v1")
      .exec(S2s.leaseServiceToken)
      .during(40.minutes)(
        exec(
          LettersService.createV1,
          LettersService.checkStatus,
          pause(40.seconds, 60.seconds)
        )
      )
      .inject(
        rampUsers(1000).during(20.minutes)
      )
  ).protocols(http.baseUrl(config.getString("baseUrl")))
}
