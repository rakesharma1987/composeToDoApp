@file:OptIn(ExperimentalMaterial3Api::class)

package com.sharmaapp.mytodoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharmaapp.mytodoapp.ui.theme.MyTodoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainPage()
                }
            }
        }
    }
}


@Composable
fun MainPage(){
    val myContext = LocalContext.current

    val todoName = remember {
        mutableStateOf("")
    }

    val itemList = readData(myContext)

    val focusManager = LocalFocusManager.current

    val deleteDialogStatus = remember {
        mutableStateOf(false)
    }

    val clickedItemIndex = remember {
        mutableStateOf(0)
    }

    val updateDialogStatus = remember {
        mutableStateOf(false)
    }

    val clickedItem = remember {
        mutableStateOf("")
    }

    val textDialogStatus = remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            TextField(
                value = todoName.value,
                onValueChange = {
                    todoName.value = it
                },
                label = { Text("Enter Todo") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color.Green,
                    unfocusedLabelColor = Color.White,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.White,
                    cursorColor = Color.White
                    ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
                    .weight(7F)
                    .height(60.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    if (todoName.value.isNotEmpty()){
                        itemList.add(todoName.value)
                        writeData(itemList, myContext)
                        todoName.value = ""
                        focusManager.clearFocus()
                    }else{
                        Toast.makeText(myContext, "Please enter a todo", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(3F)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.btn_container_color),
                    contentColor = colorResource(R.color.white),
                ),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text("Add", fontSize = 20.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.padding(5.dp)
        ) {
            items(
                count = itemList.size,
                itemContent = { index: Int ->
                    val item = itemList[index]

                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(0.dp)
                    ){
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = item,
                                color = Color.White,
                                fontSize = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(250.dp).clickable {
                                    clickedItem.value = item
                                    textDialogStatus.value = true
                                }
                            )

                            Row {
                                IconButton(
                                    onClick = {
                                        updateDialogStatus.value = true
                                        clickedItemIndex.value = index
                                        clickedItem.value = item
                                    }
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "edit", tint = Color.White)
                                }

                                IconButton(
                                    onClick = {
                                        deleteDialogStatus.value = true
                                        clickedItemIndex.value = index
                                    }
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "delete", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            )
        }
        if (deleteDialogStatus.value){
            AlertDialog(
                onDismissRequest = { deleteDialogStatus.value = false },
                title = { Text("Delete") },
                text = { Text("Do you want to delete this item from the list") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            itemList.removeAt(clickedItemIndex.value)
                            writeData(itemList, myContext)
                            deleteDialogStatus.value = false
                            Toast.makeText(myContext, "Item is removed from the list", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {deleteDialogStatus.value = false}
                    ) {
                        Text("No")
                    }
                }
            )
        }

        if (updateDialogStatus.value){
            AlertDialog(
                onDismissRequest = { deleteDialogStatus.value = false },
                title = { Text("Update") },
                text = {
                    TextField(
                        value = clickedItem.value,
                        onValueChange = {
                            clickedItem.value = it
                        }
                    )
                       },
                confirmButton = {
                    TextButton(
                        onClick = {
                            itemList[clickedItemIndex.value] = clickedItem.value
                            writeData(itemList, myContext)
                            updateDialogStatus.value = false
                            Toast.makeText(myContext, "Item is updated", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {updateDialogStatus.value = false}
                    ) {
                        Text("No")
                    }
                }
            )
        }
        if (textDialogStatus.value){
            AlertDialog(
                onDismissRequest = { textDialogStatus.value = false },
                title = { Text("Todo Item") },
                text = {
                    Text(text = clickedItem.value)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            textDialogStatus.value = false
                        }
                    ) {
                        Text(text = "Ok")
                    }
                },
            )
        }
    }
}