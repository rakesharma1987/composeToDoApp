@file:Suppress("UNCHECKED_CAST")

package com.sharmaapp.mytodoapp

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

const val FILE_NAME = "todo.dat"

fun writeData(items: SnapshotStateList<String>, context: Context){
    val fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
    val oos = ObjectOutputStream(fos)
    val itemList = ArrayList<String>()
    itemList.addAll(items)
    oos.writeObject(itemList)
    oos.close()
}

fun readData(context: Context): SnapshotStateList<String>{
    var itemList = ArrayList<String>()
    try {
        val fis = context.openFileInput(FILE_NAME)
        val ois = ObjectInputStream(fis)
        itemList = ois.readObject() as ArrayList<String>
    }catch (fnfe: FileNotFoundException){
        itemList = ArrayList()
    }

    val items = SnapshotStateList<String>()
    items.addAll(itemList)
    return items
}


