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
      .during(60.seconds)(
        exec(
          LettersService.create,
          LettersService.checkStatus,
          pause(500.milliseconds)
        )
      )
      .inject(
        rampUsers(10).over(5.seconds)
      )
  ).protocols(http.baseURL(config.getString("baseUrl")))
}
