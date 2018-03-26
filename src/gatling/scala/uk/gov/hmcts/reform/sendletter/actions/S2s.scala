package uk.gov.hmcts.reform.sendletter.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import com.typesafe.config.ConfigFactory
import uk.gov.hmcts.reform.sendletter

object S2s {
  private val config = ConfigFactory.load()

  val leaseServiceToken: ChainBuilder = exec(
    sendletter.applyOptionalProxy(
      http ("Lease service token")
      .post(config.getString("s2sUrl") + "/lease")
      .body(StringBody("""{"microservice":"""" + config.getString("service.name") + """","oneTimePassword":"${otp}"}"""))
      .asJSON
      .check(bodyString.saveAs("service_token"))
    )
  )
}
