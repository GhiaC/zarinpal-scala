package me.ghiasi.example.entites.request

final case class VerifyTransactionRequest(MerchantID: String,
                                          Amount: Long,
                                          Authority: String) extends Request
