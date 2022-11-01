package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.User

object FakeUserData {
    val user1 = User("1", "user", "user@gmail.com", "", 0, 0, 1.0, 1.0)
    val userList = listOf(user1)
}
