package uk.gov.hmcts.reform.sendletter.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter

import java.util.UUID
import scala.io.Source

object PrintService {

  private val uuidFeeder = Iterator.continually(Map("uuid" -> UUID.randomUUID.toString))
  private val printJson = Source.fromResource("print_job.json").getLines().mkString

  val printV1: ChainBuilder = create(printJson, "application/vnd.uk.gov.hmcts.letter-service.in.print-job.v1+json")

  private def create(payload: String, mediaType: String): ChainBuilder = {
    feed(uuidFeeder)
      .exec(
        sendletter.applyOptionalProxy(
          http("Create Print letter")
            .put("/print-jobs/${uuid}")
            .headers(Map(
              "ServiceAuthorization" -> "Bearer ${service_token}",
              ContentType -> mediaType
            ))
            .body(StringBody(payload))
            .ignoreDefaultChecks
            .check(
              status.is(200),
              jsonPath("$.print_job.id").saveAs("id")
            )
        )
      )
  }
}
