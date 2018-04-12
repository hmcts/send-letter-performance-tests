package uk.gov.hmcts.reform.sendletter

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter.actions.{LettersService, S2s}

import scala.concurrent.duration._

class MainSimulation extends Simulation {

  val config: Config = ConfigFactory.load()

  setUp(
    scenario("Create letters")
      .exec(S2s.leaseServiceToken)
      .during(40.minutes)(
        exec(
          LettersService.create,
          LettersService.checkStatus,
          pause(40.seconds, 60.seconds)
        )
      )
      .inject(
        rampUsers(1000).over(20.minutes)
      )
  ).protocols(http.baseURL(config.getString("baseUrl")))
}
