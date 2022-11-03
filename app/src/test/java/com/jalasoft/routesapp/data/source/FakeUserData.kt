package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.model.remote.User
import java.util.*

object FakeUserData {
    val user1 = User("1", "user", "user@gmail.com", "", 0, 0, Date(), Date())
    val userList = listOf(user1)
}
