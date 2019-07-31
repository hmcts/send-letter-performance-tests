package uk.gov.hmcts.reform.sendletter

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter.actions.{LettersService, S2s}

import scala.concurrent.duration._

class EmbeddedPdfsSimulation extends Simulation {

  val config: Config = ConfigFactory.load()

  setUp(
    scenario("Create letters v2")
      .exec(S2s.leaseServiceToken)
      .during(5.minutes)(
        exec(
          LettersService.createV2,
          LettersService.checkStatus,
          pause(1.seconds, 2.seconds)
        )
      )
      .inject(
        rampUsers(50).over(5.seconds)
      )
  ).protocols(http.baseURL(config.getString("baseUrl")))

}
