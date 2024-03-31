package com.example.go4lunch24kotlin.workManager

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.WorkMateRestaurantChoice
import com.example.go4lunch24kotlin.ui.RestaurantDetailsActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDate
import java.util.concurrent.ExecutionException

class UploadWorker (
    private val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "notif"
    }

    private var restaurantName: String? = null
    private var restaurantAddress: String? = null
    private var restaurantId: String? = null

    override fun doWork(): Result {
        var usersWithRestaurant: List<WorkMateRestaurantChoice?>? = null
        try {
            usersWithRestaurant = getUsersWithRestaurantChoice()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if (usersWithRestaurant != null) {
            if (isCurrentUserHasChosenRestaurant(usersWithRestaurant)) {

                val workmates = mutableListOf<String>()

                var userId = context.getString(R.string.user_init_value)
                if (FirebaseAuth.getInstance().currentUser != null) {
                    userId = FirebaseAuth.getInstance().currentUser!!.uid
                }
                for (userWithRestaurant in usersWithRestaurant) {
                    if (userWithRestaurant!!.restaurantName.equals(restaurantName)
                        && userWithRestaurant.userId.equals(userId)
                    ) {
                        workmates.add(userWithRestaurant.userName!!.toString())
                    }
                }
                notificationMessage(workmates)
            }
        }
        return Result.success()
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun getUsersWithRestaurantChoice(): List<WorkMateRestaurantChoice> {
        val usersWithRestaurant: MutableList<WorkMateRestaurantChoice> = ArrayList()
        Tasks.await(getDayCollection().get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                for (document in task.result) {
                    usersWithRestaurant.add(document.toObject(
                        WorkMateRestaurantChoice::class.java))
                }
            })
        return usersWithRestaurant
    }

    private fun getDayCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(LocalDate.now().toString())
    }

    private fun isCurrentUserHasChosenRestaurant(usersWithRestaurant: List<WorkMateRestaurantChoice?>): Boolean {
        var gotRestaurant = false
        val user = FirebaseAuth.getInstance().currentUser
        for (restaurant in usersWithRestaurant) {
            if (restaurant!!.userId == user!!.uid) {
                restaurantName = restaurant.restaurantName
                restaurantAddress = restaurant.restaurantAddress
                restaurantId = restaurant.restaurantId
                gotRestaurant = true
            }
        }
        return gotRestaurant
    }

    private fun notificationMessage(allWorkmate: List<String>) {
        val notification: String
        val stringBuilderWorkmates = StringBuilder()
        notification = if (allWorkmate.isNotEmpty()) {
            for (i in allWorkmate.indices) {
                stringBuilderWorkmates.append(allWorkmate[i])
                if (i < allWorkmate.size - 2) stringBuilderWorkmates.append(context.getString(R.string.separate))
                    .append(" ") else if (i == allWorkmate.size - 2) stringBuilderWorkmates.append(" ")
                    .append(context.getString(R.string.and)).append(" ")
            }
            (restaurantName
                    + " "
                    + restaurantAddress
                    + " " + stringBuilderWorkmates)
        } else {
            (restaurantName
                    + " "
                    + restaurantAddress)
        }
        displayNotification(notification)
    }

    @SuppressLint("MissingPermission")
    private fun displayNotification(notification: String) {
        // SET THE VIEW WHERE USER MUST GO WHEN TYPE ON NOTIFICATION
        val intent = Intent(context, RestaurantDetailsActivity::class.java)
        intent.putExtra(RestaurantDetailsActivity.RESTAURANT_ID, restaurantId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(intent)
        @SuppressLint("UnspecifiedImmutableFlag") val pendingIntent
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        // CREATE NOTIFICATION
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_go4lunch)
            .setContentTitle(context.getString(R.string.notification_title))
            //.setContentText(notification)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(notification))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // SHOW THE NOTIFICATION
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}