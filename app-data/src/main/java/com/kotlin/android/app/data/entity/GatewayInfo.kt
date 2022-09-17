package com.kotlin.android.app.data.entity

data class GatewayInfo(var localDnsInfo: LocalDnsInfo) {
    data class LocalDnsInfo(var activityApiHost: String? = "",
                            var activityMktApiHost: String? = "",
                            var cardApiHost: String? = "",
                            var cinemaApiHost: String? = "",
                            var countApiHost: String? = "",
                            var couponApiHost: String? = "",
                            /**此属性暂时无用*/
                            var createTimpstamp: String? = "",
                            /**此属性暂时无用*/
                            var lastModifyTimpstamp: String? = "",
                            var logApiHost: String? = "",
                            var miscApiHost: String? = "",
                            var paymentApiHost: String? = "",
                            var snackApiHost: String? = "",
                            var ticketApiHost: String? = "",
                            var userApiHost: String? = "")
}