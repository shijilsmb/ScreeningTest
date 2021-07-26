package com.example.screeningtest.gdriverest

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DriveServiceHelper(private val mDriveService: Drive) {

    fun uploadSyncFile(
        localFile: java.io.File,
        mimeType: String?,
        folderId: String?
    ): File {
        val root: List<String>
        root = folderId?.let { listOf(it) } ?: listOf("root")
        val metadata = File()
            .setParents(root)
            .setMimeType(mimeType)
            .setName(localFile.name)
        val fileContent = FileContent(mimeType, localFile)
        return mDriveService.files().create(metadata, fileContent).execute()
    }


    companion object {

        fun getGoogleDriveService(
            context: Context?,
            account: GoogleSignInAccount,
            appName: String?
        ): Drive {
            val credential = GoogleAccountCredential.usingOAuth2(
                context, setOf(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = account.account
            return Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential
            )
                .setApplicationName(appName)
                .build()
        }

        private const val TAG = "DriveServiceHelper"
    }
}