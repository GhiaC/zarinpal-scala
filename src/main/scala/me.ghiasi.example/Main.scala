package me.ghiasi.example

import akka.actor.ActorSystem
import me.ghiasi.controller.ZarinExtension
import me.ghiasi.utils.transaction.models.{GetAuthorityResponse, VerifyTransactionResponse}

object Main {

  implicit val actorSystem = ActorSystem("zarin")
  implicit val ex = actorSystem.dispatcher
  protected val log = actorSystem.log

  private val zarin = ZarinExtension(actorSystem)

  def main(args: Array[String]): Unit = {
    exampleGetAuthority(1000, "test", "09360000000", "masoud@ghiasi.me")
  }

  private def exampleGetAuthority(amount: Int, description: String, mobile: String, email: String) = {
    val authority = zarin.getAuthorityID(amount, description, mobile, email)
    authority match {
      case Right(response: GetAuthorityResponse) =>
        log.info(response.toString)
        exampleVerify(amount, response.Authority)

      case Left(value) =>
        log.error(value.toString)
    }
  }

  private def exampleVerify(amount: Int, authority: String) = {
    val verify = zarin.verifyTransaction(amount, authority)
    verify match {
      case Right(response: VerifyTransactionResponse) =>
        log.info(response.toString)

      case Left(value) =>
        log.error(value.toString)
    }
  }
}
