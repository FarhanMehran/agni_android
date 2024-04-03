package com.capcorp.ui.driver.stripe

data class StripeAccountData(
    val connectStripeAccounDetails: ConnectStripeAccounDetails
)

data class ConnectStripeAccounDetails(
    val capabilities: Capabilities,
    val country: String,
    val defaultCurrency: String,
    val email: String,
    val external_accounts: ExternalAccounts,
    val loginLink: String?,
    val payouts_enabled: Boolean,
    val stripeAccountId: String,
    val type: String
)

data class Capabilities(
    val card_payments: String,
    val transfers: String
)

data class ExternalAccounts(
    val `data`: List<Data>,
    val has_more: Boolean,
    val `object`: String,
    val total_count: Int,
    val url: String
)

data class Data(
    val account: String,
    val account_holder_name: String?,
    val account_holder_type: String?,
    val bank_name: String,
    val country: String,
    val currency: String,
    val default_for_currency: Boolean,
    val fingerprint: String,
    val id: String,
    val last4: String,
    val metadata: Metadata,
    val `object`: String,
    val routing_number: String,
    val status: String
)

class Metadata