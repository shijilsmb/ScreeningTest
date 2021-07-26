package com.example.screeningtest

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.screeningtest.gdriverest.DriveServiceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class UploadService  : Service() {

    private val CHANNEL_ID = "ForegroundService Kotlin"
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private var mDriveServiceHelper: DriveServiceHelper? = null

    companion object {
        var uploadingStarted = false
        fun startService(context: Context, filePath: String) {
            val startIntent = Intent(context, UploadService::class.java)
            startIntent.putExtra("filePath", filePath)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, UploadService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        mDriveServiceHelper = DriveServiceHelper(
            DriveServiceHelper.getGoogleDriveService(
                applicationContext,
                account!!,
                "ScreenTest"
            )
        )
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val filePath = intent?.getStringExtra("filePath")

        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Uploading file")
            //.setContentText(input)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //stopSelf();

        if (!uploadingStarted){
            GlobalScope.launch (dispatcher)
            {
                uploadFile(filePath!!)
            }
        }

        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        uploadingStarted = false
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
    fun uploadFile(path:String){
        val directory = File(path)
        val files = directory.listFiles()
        Log.d("Files", "Size: " + files.size)
        for (i in files.indices) {
            Log.d("Files1", "FileName:" + files[i].name)

            if (!files[i].isDirectory){
                uploadingStarted = true
                Log.d("Files2", "FileName:" + files[i].name)
                val file = Uri.fromFile(files[i])
                val extension = MimeTypeMap.getFileExtensionFromUrl(file.toString())
                val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

                mDriveServiceHelper!!.uploadSyncFile(files[i],
                    type,
                    null
                )
            }
        }

        uploadingStarted = false
        stopSelf()
    }
}