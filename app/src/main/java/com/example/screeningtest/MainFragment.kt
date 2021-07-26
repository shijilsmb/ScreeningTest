package com.example.screeningtest

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.screeningtest.utils.SessionUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
import ir.androidexception.filepicker.dialog.DirectoryPickerDialog
import kotlinx.android.synthetic.main.fragment_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class MainFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private val TAG = "MainFragment"
    private val REQUEST_CODE_SIGN_IN = 100

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var filePath : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mGoogleSignInClient = buildGoogleSignInClient()

        txt_logout.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setMessage(R.string.alert_logout)
                .setPositiveButton(R.string.logout) { _, i ->
                    SessionUtils.saveSession(false)
                    mGoogleSignInClient!!.signOut()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()

                    if (mGoogleSignInClient != null) {
                        mGoogleSignInClient!!.signOut()
                    }

                }.setNegativeButton(R.string.cancel) { dialog, i -> }.show()

        }

        val account = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext)

        bt_select.setOnClickListener {
            if (account == null) {
                signIn()
            } else {
                //email.setText(account.email)
                val directoryPickerDialog = DirectoryPickerDialog(requireActivity(),
                    { Toast.makeText(requireActivity(), "Canceled!!", Toast.LENGTH_SHORT).show() }
                ) { files ->
                    /*Toast.makeText(
                        requireActivity(),
                        files.get(0).getPath(),
                        Toast.LENGTH_SHORT
                    ).show()*/
                    filePath =  files.get(0).getPath()

                    uploadFile()
                }
                directoryPickerDialog.show()
            }
        }

        setButtonView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == Activity.RESULT_OK && resultData != null) {
                handleSignInResult(resultData)
            }
            AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE -> {
                uploadFile()
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        uploadFile()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun setButtonView() {
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext)
        bt_select.text = if (account == null) "Select Drive Account" else "Select Folder"
    }

    private fun signIn() {
        startActivityForResult(
            mGoogleSignInClient!!.signInIntent,
            REQUEST_CODE_SIGN_IN
        )
    }

    private fun buildGoogleSignInClient(): GoogleSignInClient? {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity().applicationContext, signInOptions)
    }


    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleSignInAccount ->
                Log.d(TAG, "Signed in as " + googleSignInAccount.email)
                //email.setText(googleSignInAccount.email)
                setButtonView()
            }
            .addOnFailureListener { e -> Log.e(TAG, "Unable to sign in.", e) }
    }


    fun uploadFile() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            UploadService.startService(requireActivity(), filePath!!)

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.txt_storage_rationale),
             1, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            /*EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    this@MainFragment,
                    1,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                    .setRationale(R.string.txt_storage_rationale)
                    .setPositiveButtonText(R.string.ok)
                    .setNegativeButtonText(R.string.cancel)
                    //.setTheme(R.style.my_fancy_style)
                    .build()
            )*/
        }
    }
}