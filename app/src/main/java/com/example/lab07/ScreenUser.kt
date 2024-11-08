package com.example.lab07

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var db: UserDatabase
    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser = remember { mutableStateOf("") }

    db = crearDatabase(context)
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Gestión de Usuarios", fontSize = 20.sp) }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            val user = User(0, firstName, lastName)
                            coroutineScope.launch {
                                AgregarUsuario(user = user, dao = dao)
                            }
                            firstName = ""
                            lastName = ""
                        },
                        modifier = Modifier
                            .padding(2.dp)
                            .shadow(1.dp, RoundedCornerShape(5.dp))
                    ) {
                        Text("Agregar Usuarios")
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val data = getUsers(dao = dao)
                                dataUser.value = data
                            }
                        },
                        modifier = Modifier
                            .padding(2.dp)
                            .shadow(1.dp, RoundedCornerShape(5.dp))
                    ) {
                        Text("Listar Usuarios")
                    }
                }
            }
        },
        content = { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(20.dp))
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID (solo lectura)") },
                    readOnly = true,
                    singleLine = true
                )
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name: ") },
                    singleLine = true
                )
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name:") },
                    singleLine = true
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            EliminarUltimoUsuario(dao = dao)
                            val data = getUsers(dao = dao)
                            dataUser.value = data
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                ) {
                    Text("Eliminar Último Usuario", fontSize = 16.sp)
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = dataUser.value,
                    fontSize = 20.sp
                )
            }
        }
    )
}

@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    var rpta: String = ""
    val users = dao.getAll()
    users.forEach { user ->
        val fila = "${user.firstName} - ${user.lastName}\n"
        rpta += fila
    }
    return rpta
}

suspend fun AgregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error: insert: ${e.message}")
    }
}

suspend fun EliminarUsuario(user: User, dao: UserDao) {
    try {
        dao.delete(user)
    } catch (e: Exception) {
        Log.e("User", "Error: delete: ${e.message}")
    }
}

suspend fun EliminarUltimoUsuario(dao: UserDao) {
    try {
        val users = dao.getAll()
        if (users.isNotEmpty()) {
            val lastUser = users.last()
            dao.delete(lastUser)
        }
    } catch (e: Exception) {
        Log.e("User", "Error: delete: ${e.message}")
    }
}

