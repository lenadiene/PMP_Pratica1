package com.example.weatherapp.db.fb

import com.example.weatherapp.model.User

class FBUser {
    var name: String? = null
    var email: String? = null
}

// Agora é uma função de extensão (fora da classe)
fun FBUser.toUser(): User {
    return User(name ?: "[sem nome]", email ?: "[sem email]")
}

fun User.toFBUser(): FBUser {
    val fbUser = FBUser()
    fbUser.name = this.name
    fbUser.email = this.email
    return fbUser
}
