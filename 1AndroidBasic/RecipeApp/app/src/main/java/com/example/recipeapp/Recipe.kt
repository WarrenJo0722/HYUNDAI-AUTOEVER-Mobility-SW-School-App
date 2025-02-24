package com.example.recipeapp

import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val name: String,
    val imageUri: String?,
    val description: String,
    val steps: List<String>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: arrayListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(imageUri)
        parcel.writeString(description)
        parcel.writeStringList(steps)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}
