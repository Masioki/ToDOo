package pwr.mobilne.l3.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*
import kotlin.random.Random.Default.nextInt

class OneTimeScheduleWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val builder = NotificationCompat.Builder(context, 0.toString())
            .setSmallIcon(
                context.resources.getIdentifier(
                    "icon_" + (inputData.getString("type")
                        ?.toLowerCase(Locale.ROOT) ?: "work"),
                    "drawable",
                    context.applicationInfo.packageName
                )
            )
            .setContentTitle(inputData.getString("title"))
            .setContentText(inputData.getString("text"))
            .setStyle(NotificationCompat.BigTextStyle().bigText(inputData.getString("text")))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            notify(nextInt(), builder.build())
        }

        return Result.success()
    }

}