package com.mac.ecomadminphp.ClientArea.ManageOrders.Models

data class ManageOrderModel(
    val id: String,
    val orderId: String,
    val productId: String,
    val productQuantity: String,
    val productAddress: String,
    val productToPay: String,
    val productPaymentMode: String,
    val productPaymentStatus: String,
    val productTrackingStatus: String,
    val productUid: String,
    val productDeliveryDate: String,
    val productName: String,
    val productImage: String,
    val productOrderDate: String,
    val productDescription: String,
    val productRefundStatus: String,
    val total: String,
    val statusValue: String,
    val page: String
)
