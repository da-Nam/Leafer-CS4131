package com.example.androidproject.model

import android.net.Uri
import com.google.errorprone.annotations.Keep

@Keep
data class PlantItem(var name : String? = null, var url : String? = null, var note : String? = null)
