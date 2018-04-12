package uk.gov.hmcts.reform.sendletter.actions

import com.typesafe.config.ConfigFactory
import com.warrenstrange.googleauth.GoogleAuthenticator
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter

object S2s {
  private val config = ConfigFactory.load()

  private val authenticator: GoogleAuthenticator = new GoogleAuthenticator()

  private val credsFeeder =
    Iterator.continually(
      Map(
        "otp" -> authenticator.getTotpPassword(config.getString("service.pass")),
        "name" -> config.getString("service.name")
      )
    )

  val leaseServiceToken: ChainBuilder =
    feed(credsFeeder)
      .exec(
        sendletter.applyOptionalProxy(
          http("Lease service token")
            .post(config.getString("s2sUrl") + "/lease")
            .body(StringBody(
              """
                |{
                |  "microservice": "${name}",
                |  "oneTimePassword": "${otp}"
                |}
              """.stripMargin))
            .asJSON
            .check(bodyString.saveAs("service_token"))
        )
      )
}
