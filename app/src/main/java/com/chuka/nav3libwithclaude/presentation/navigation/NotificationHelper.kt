package com.chuka.nav3libwithclaude.presentation.navigation

// Updated NotificationHelper.kt with permission handling
import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.chuka.nav3libwithclaude.domain.models.ToastData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deepLinkHandler: DeepLinkHandler,
    private val permissionHandler: NotificationPermissionHandler
) {

    companion object {
        const val CHANNEL_ID = "nav_display_channel"
        const val NOTIFICATION_ID_BOY = 1001
        const val NOTIFICATION_ID_GIRL = 1002
        const val NOTIFICATION_ID_HUMAN = 1003
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Navigation Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for deep linking navigation"
            enableVibration(true)
            enableLights(true)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showBoyNotification(
        humanId: Long,
        humanName: String,
        onPermissionDenied: (() -> Unit)? = null
    ) {
        if (!permissionHandler.hasNotificationPermission(context)) {
            onPermissionDenied?.invoke()
            return
        }

        val deepLinkUri = deepLinkHandler.createDeepLink(
            NavigationRoute.BoyScreenRoute(humanId = humanId)
        ) + "?notification=true"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = deepLinkUri.toUri()
            setPackage(context.packageName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_BOY,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("View Boy Details")
            .setContentText("Tap to view details for $humanName")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("🚀 Tap to navigate to $humanName's details with custom animation!"))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BOY, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showGirlNotification(
        humanId: Long,
        humanName: String,
        onPermissionDenied: (() -> Unit)? = null
    ) {
        if (!permissionHandler.hasNotificationPermission(context)) {
            onPermissionDenied?.invoke()
            return
        }

        val deepLinkUri = deepLinkHandler.createDeepLink(
            NavigationRoute.GirlScreenRoute(humanId = humanId)
        ) + "?notification=true"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = deepLinkUri.toUri()
            setPackage(context.packageName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_GIRL,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("View Girl Details")
            .setContentText("Tap to view details for $humanName")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("✨ Tap to navigate to $humanName's details with custom animation!"))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_GIRL, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showGeneralNotification(onPermissionDenied: (() -> Unit)? = null) {
        if (!permissionHandler.hasNotificationPermission(context)) {
            onPermissionDenied?.invoke()
            return
        }

        val deepLinkUri = deepLinkHandler.createDeepLink(
            NavigationRoute.HumanScreenRoute(
                fromScreen = "notification",
                toastData = ToastData.create(
                    message = "Welcome back to NavDisplay!",
                    backgroundColor = 0xFF2196F3,
                    duration = ToastData.LENGTH_LONG
                )
            )
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = deepLinkUri.toUri()
            setPackage(context.packageName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_HUMAN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("NavDisplay App")
            .setContentText("Check out the latest updates!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("🎉 Experience our custom navigation with amazing animations!"))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_HUMAN, notification)
    }
}