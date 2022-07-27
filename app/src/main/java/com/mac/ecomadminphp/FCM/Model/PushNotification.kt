package com.mac.ecomadminphp.FCM.Model

class PushNotification {
    private var data: NotificationData? = null
    var to: String? = null

    constructor() {}
    constructor(data: NotificationData?, to: String?) {
        this.data = data
        this.to = to
    }

    fun getData(): NotificationData? {
        return data
    }

    fun setData(data: NotificationData?) {
        this.data = data
    }
}