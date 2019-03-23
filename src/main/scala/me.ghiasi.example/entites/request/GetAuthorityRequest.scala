package me.ghiasi.example.entites.request

final case class GetAuthorityRequest(MerchantID: String,
                                     Email: String,
                                     Mobile: String,
                                     Amount: Int,
                                     CallbackURL: String,
                                     Description: String) extends Request