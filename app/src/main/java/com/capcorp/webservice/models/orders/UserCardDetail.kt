package com.capcorp.webservice.models.orders

import java.io.Serializable

class UserCardDetail(
    var _id: String?,
    var updatedAt: String?,
    var createdAt: String?,
    var expMonth: Int?,
    var expYear: Int?,
    var cardHolderName: String?,
    var stripeCustomerId: String?,
    var cardId: String?,
    var fingerPrint: String?,
    var last4: Int?,
    var brand: String?,
    var userId: String?,
    var tokenId: String?,
    var country: String?,
    var iso: String?,
    var isDeleted: Boolean?,
    var isDefault: Boolean?,
    var funding: String?,
    var __v: Int?
) : Serializable
