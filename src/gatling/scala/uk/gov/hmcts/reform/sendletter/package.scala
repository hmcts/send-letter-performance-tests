package uk.gov.hmcts.reform

import com.typesafe.config.ConfigFactory
import io.gatling.http.Predef.Proxy
import io.gatling.http.request.builder.HttpRequestBuilder

package object sendletter {

  private val config = ConfigFactory.load()

  /**
    * Helper function to optionally apply a proxy if set in the config
    */
  def applyOptionalProxy(req: HttpRequestBuilder): HttpRequestBuilder =
    if (config.getString("proxy.host").isEmpty) req else
      req.proxy(Proxy(config.getString("proxy.host"), config.getInt("proxy.port")))
}
