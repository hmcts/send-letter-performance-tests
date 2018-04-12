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

  private val otpFeeder = Iterator.continually(Map("otp" -> authenticator.getTotpPassword(config.getString("service.pass"))))

  val leaseServiceToken: ChainBuilder =
    feed(otpFeeder)
      .exec(
        sendletter.applyOptionalProxy(
          http("Lease service token")
            .post(config.getString("s2sUrl") + "/lease")
            .body(StringBody("""{"microservice":"""" + config.getString("service.name") + """","oneTimePassword":"${otp}"}"""))
            .asJSON
            .check(bodyString.saveAs("service_token"))
        )
      )
}
