package com.example.feedapp

import android.os.Parcel
import android.os.Parcelable

data class Feed(
    val content: String,
    val imageUri: String?,
    val userName: String,
    val userImageUri: String?,
    val likes: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt(),
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(content)
        dest.writeString(imageUri)
        dest.writeString(userName)
        dest.writeString(userImageUri)
        dest.writeInt(likes)
    }

    companion object CREATOR : Parcelable.Creator<Feed> {
        override fun createFromParcel(parcel: Parcel): Feed {
            return Feed(parcel)
        }

        override fun newArray(size: Int): Array<Feed?> {
            return arrayOfNulls(size)
        }
    }
}