package com.example.go4lunch24kotlin.util

enum class PermissionsAction {

    // SINGLE LIVE EVENT FOR PERMISSION
    // FROM MainActivityViewModel to MainActivity
    PERMISSION_ASKED,
    PERMISSION_DENIED,

    // FROM SettingViewModel to SettingActivity
    NOTIFICATION_ENABLED,
    NOTIFICATION_DISABLED
}