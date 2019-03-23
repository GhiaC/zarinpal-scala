package me.ghiasi.controller

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.ConfigFactory
import io.circe
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import scalaj.http._
import me.ghiasi.example.entites.request._
import me.ghiasi.utils.transaction.models.{GetAuthorityResponse, VerifyTransactionResponse}

import scala.concurrent.ExecutionContext

object ZarinExtension
  extends ExtensionId[ZarinExtension]
    with ExtensionIdProvider {

  override def lookup: ZarinExtension.type = ZarinExtension

  override def createExtension(system: ExtendedActorSystem) = new ZarinExtension(system)

  override def get(system: ActorSystem): ZarinExtension = super.get(system)
}

class ZarinExtension(system: ActorSystem) extends Extension {

  private val conf = ConfigFactory.load()
  private val paymentEndpoint: String =
    conf.getString("transaction.zarinpal.endpoints.payment-request")

  private val startpayEndpoint: String =
    conf.getString("transaction.zarinpal.endpoints.startpay")

  private val verificationEndpoint: String = conf.getString("transaction.zarinpal.endpoints.verification")
  private val merchantID: String =
    conf.getString("transaction.zarinpal.merchant-id")
  private val callbackUrl: String =
    conf.getString("transaction.zarinpal.callback-url")

  private val requestTimeOut: Int =
    conf.getInt("transaction.zarinpal.request-time-out-milisecond")

  implicit val ex: ExecutionContext = system.dispatcher

  private def sendJsonRequest(rawJson: String, endpoint: String) = {
    Http(endpoint)
      .postData(rawJson)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(requestTimeOut)).asString
  }

  def getAuthorityID(amount: Int, description: String, mobile: String, email: String)(implicit ex: ExecutionContext): Either[circe.Error, GetAuthorityResponse] = {
    val rawJsonRequest = GetAuthorityRequest(merchantID, email, mobile, amount, callbackUrl, description).asJson.toString()
    val response = sendJsonRequest(rawJsonRequest, paymentEndpoint)
    decode[GetAuthorityResponse](response.body)
  }

  def verifyTransaction(amount: Int, authority: String)(implicit ex: ExecutionContext): Either[circe.Error, VerifyTransactionResponse] = {
    val rawJsonRequest = VerifyTransactionRequest(merchantID, amount, authority).asJson.toString()
    val response = sendJsonRequest(rawJsonRequest, verificationEndpoint)
    decode[VerifyTransactionResponse](response.body)
  }
}