package uk.gov.hmcts.reform.sendletter.actions

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._
import io.gatling.http.Predef._

object LettersService {

  private val uuidFeeder = Iterator.continually(Map("uuid" -> UUID.randomUUID.toString))

  val create: ChainBuilder =
    feed(uuidFeeder)
      .exec(
        http("Create letter")
          .post("/letters")
          .headers(Map(
            "ServiceAuthorization" -> "Bearer ${service_token}",
            ContentType -> ApplicationJson
          ))
          .body(StringBody(
            """
              |{
              |  "documents": [
              |    {
              |      "template": "<html>hello {{foo}}</html>",
              |      "values": {
              |        "foo": "${uuid}"
              |      }
              |    },
              |    {
              |      "template": "<html>hello again {{foo}}</html>",
              |      "values": {
              |        "foo": "bar"
              |      }
              |    }
              |  ],
              |  "type": "someType"
              |}
            """.stripMargin
          ))
          .check(
            status.is(200),
            jsonPath("$.letter_id").saveAs("id")
          )
      )

  val checkStatus: ChainBuilder =
    exec(
      http("Check letter status")
        .get("/letters/${id}")
        .headers(Map(
          "ServiceAuthorization" -> "Bearer ${service_token}",
          ContentType -> ApplicationJson
        ))
    )
}
