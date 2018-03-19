package uk.gov.hmcts.reform.sendletter.actions

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.HeaderValues._
import io.gatling.http.HeaderNames._

object LettersService {

  val create: ChainBuilder =
    exec(
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
            |      "template": "abc",
            |      "values": {
            |        "a": "b"
            |      }
            |    },
            |    {
            |      "template": "xyz",
            |      "values": {
            |        "x": "y"
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
