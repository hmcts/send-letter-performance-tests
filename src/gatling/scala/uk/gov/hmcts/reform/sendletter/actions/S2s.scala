package uk.gov.hmcts.reform.sendletter.actions

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._

object S2s {
  private val url = ConfigFactory.load().getString("s2sUrl")

  val leaseServiceToken: ChainBuilder =
    exec(
      http("Lease service token")
        .post(url + "/testing-support/lease")
        .header(ContentType, ApplicationFormUrlEncoded)
        .formParam("microservice", "load-test-service")
        .check(bodyString.saveAs("service_token"))
    )
}
