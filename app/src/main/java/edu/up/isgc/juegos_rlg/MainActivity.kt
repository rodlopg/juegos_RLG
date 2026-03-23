package edu.up.isgc.juegos_rlg

import android.R.attr.navigationIcon
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.up.isgc.juegos_rlg.ui.theme.Juegos_rlgTheme
import java.util.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Juegos_rlgTheme() {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val canNavigateBack = navBackStackEntry?.destination?.route != Screen.Home.route

                Scaffold(modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(title = {Text("Juegos")},
                                navigationIcon = {
                                    if(canNavigateBack){
                                        IconButton(onClick = {navController.popBackStack()}){
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "regresar"
                                            )
                                        }
                                    }
                            })
                        }
                    ) {
                    innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ){
                        composable(route = Screen.Home.route){
                            Home(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                                navController
                            )
                        }
                        composable(route = Screen.Buscaminas.route){
                            Buscaminas(name = "Android",
                                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                                navController,
                                mensaje = { mensaje() })
                        }
                        composable(route = Screen.EncuentraTopo.route){
                            EncuentraTopo(modifier = Modifier.padding(innerPadding).fillMaxSize(), navController)
                        }
                    }
                }
            }
        }
    }
    fun mensaje(){
        Toast.makeText(this, "bien", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun Home(name: String, modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Button(onClick = {navController.navigate(Screen.Buscaminas.route)}){
            Text("Buscaminas")
        }
        Button(onClick = {navController.navigate(Screen.EncuentraTopo.route)}){
            Text("Encuentra el Topo")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    Juegos_rlgTheme() {
        val navController = rememberNavController()
        Home("Android", modifier = Modifier.fillMaxSize(),
            navController)
    }
}

@Composable
fun Buscaminas(name: String, modifier: Modifier = Modifier, navController: NavController, mensaje : () -> Unit) {
    val columnas = 7
    val filas = 14

    val estadoBotones = remember {
        List(filas * columnas) { mutableStateOf(true) }
    }

    val minas = remember {
        List(filas * columnas) { mutableStateOf(asignaMina()) }
    }

    val verAlerta = remember { mutableStateOf(false) }
    val victoria = remember { mutableStateOf(false) }
    val minasEncontradas = remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        for(i in 0 until filas){
            Row(modifier = Modifier.weight(1f)){
                for(j in 0 until columnas){
                    val index = i * columnas + j

                    Button(
                        onClick = {
                            if (!estadoBotones[index].value) return@Button

                            estadoBotones[index].value = false

                            if (minas[index].value){
                                verAlerta.value = true
                            } else {
                                val contiguas = contarMinas(i, j, filas, columnas, minas)
                                minasEncontradas.value++

                                if (minasEncontradas.value >= 10){
                                    victoria.value = true
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        shape = RectangleShape,
                        enabled = estadoBotones[index].value,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9500),
                            contentColor = Color.White
                        )
                    ){
                        val contiguas = contarMinas(i, j, filas, columnas, minas)

                        Text(
                            text = if (estadoBotones[index].value) ""
                            else if (minas[index].value) "#"
                            else if (contiguas > 0) contiguas.toString()
                            else "",
                            style = TextStyle(fontSize = 20.sp),
                            color = when {
                                minas[index].value -> Color.Red
                                contiguas == 1 -> Color(0xFF64B5F6)
                                contiguas == 2 -> Color(0xFF81C784)
                                contiguas >= 3 -> Color(0xFFE57373)
                                else -> Color.White
                            }
                        )
                    }
                }
            }
        }

        if(verAlerta.value){
            AlertDialog(
                onDismissRequest = { verAlerta.value = false },
                title = { Text("Perdiste") },
                text = { Text("Encontraste una mina") },
                confirmButton = {
                    Button(onClick = {
                        verAlerta.value = false
                        victoria.value = false
                        minasEncontradas.value = 0
                        estadoBotones.forEach { it.value = true }
                        minas.forEach { it.value = asignaMina() }
                    }) {
                        Text("Reiniciar juego")
                    }
                }
            )
        } else if (victoria.value){
            AlertDialog(
                onDismissRequest = { victoria.value = false },
                title = { Text("VICTORIA!!! Clickeaste ${minasEncontradas.value} NO-minas!!!") },
                text = { Text("Vini Vidi Vici") },
                confirmButton = {
                    Button(onClick = {
                        victoria.value = false
                        minasEncontradas.value = 0
                        estadoBotones.forEach { it.value = true }
                        minas.forEach { it.value = asignaMina() }
                    }) {
                        Text("Reiniciar juego")
                    }
                }
            )
        }
    }
}

fun contarMinas(i: Int, j: Int, filas: Int, columnas: Int, minas: List<MutableState<Boolean>>): Int {
    var count = 0

    for (x in -1..1){
        for (y in -1..1){
            if (x == 0 && y == 0) continue

            val ni = i + x
            val nj = j + y

            if (ni in 0 until filas && nj in 0 until columnas){
                val index = ni * columnas + nj
                if (minas[index].value) count++
            }
        }
    }

    return count
}

fun asignaMina(): Boolean{
    val numeroRandom = Random().nextInt(10)
    return (numeroRandom > 7)
}

@Composable
fun EncuentraTopo(modifier: Modifier, navController: NavController) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("EncuentraTopo")
    }
}






