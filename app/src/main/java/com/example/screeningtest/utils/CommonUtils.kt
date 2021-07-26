package com.example.screeningtest.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Patterns
import android.util.TypedValue
import android.widget.Toast
import java.net.URISyntaxException


class CommonUtils {
    companion object {


        fun isEmailValid(email: String?): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isPhoneValid(phone: String?): Boolean {
            return Patterns.PHONE.matcher(phone).matches()
        }


        fun dpToPixel(dp:Float,context: Context)=
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

        fun copyToClipboard(context: Context,text:String){
            val clipboard: ClipboardManager? =  context.getSystemService(Context.CLIPBOARD_SERVICE)  as ClipboardManager?
            //  getSystemService<Any>(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("Promo code", text)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context,"Copied",Toast.LENGTH_SHORT).show()
        }
    }
}