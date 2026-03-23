package edu.up.isgc.juegos_rlg

sealed class Screen (val route: String){

    object Home: Screen("home")

    object Buscaminas: Screen("Buscaminas")

    object EncuentraTopo: Screen("EncuentraTopo")
}