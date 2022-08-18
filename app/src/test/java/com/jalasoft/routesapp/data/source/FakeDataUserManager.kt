package com.jalasoft.routesapp.data.source

import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.managers.UserRepository
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.UserTypeLogin

class FakeDataUserManager(var users: MutableList<User> = mutableListOf()) : UserRepository {

    override suspend fun validateEmailUser(email: String): Response<MutableList<User>> {
        return Response.Success(users)
    }

    override suspend fun createUser(name: String, email: String, typeLogin: UserTypeLogin): Response<String> {
        return Response.Success(name)
    }

    override suspend fun createUserAuth(email: String, password: String): Response<String> {
        return Response.Success(email)
    }

    override suspend fun signInWithCredential(credential: AuthCredential): Response<String> {
        return Response.Success(credential.toString())
    }

    override fun signOut() {
        users.removeAll(users)
    }
}
