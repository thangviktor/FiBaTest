package com.j.fibatest1.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataFirebase(
        val name: String ?= "",
        val age: Int ?= 0,
        val favorite: String ?= ""
) : Parcelable
