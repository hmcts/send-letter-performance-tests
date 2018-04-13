package uk.gov.hmcts.reform.sendletter.actions

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.sendletter

import scala.io.Source

object LettersService {

  private val uuidFeeder = Iterator.continually(Map("uuid" -> UUID.randomUUID.toString))

  private val letterJson = Source.fromResource("letter.json").getLines().mkString

  val create: ChainBuilder =
    feed(uuidFeeder)
      .exec(
        sendletter.applyOptionalProxy(
          http("Create letter")
            .post("/letters")
            .headers(Map(
              "ServiceAuthorization" -> "Bearer ${service_token}",
              ContentType -> ApplicationJson
            ))
            .body(StringBody(letterJson))
            .check(
              status.is(200),
              jsonPath("$.letter_id").saveAs("id")
            )
        )
      )

  val checkStatus: ChainBuilder =
    exec(
      sendletter.applyOptionalProxy(
        http("Check letter status")
          .get("/letters/${id}")
          .headers(Map(
            "ServiceAuthorization" -> "Bearer ${service_token}",
            ContentType -> ApplicationJson
          ))
          .check(status.is(200))
      )
    )
}
