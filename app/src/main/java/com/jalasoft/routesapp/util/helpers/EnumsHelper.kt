package com.jalasoft.routesapp.util.helpers

enum class FirebaseCollections {
    Users,
    Countries,
    CityRoute,
    Cities
}
enum class UserType(val int: Int) {
    NORMAL(0),
    ADMIN(1),
}
enum class UserTypeLogin(val int: Int) {
    NORMAL(1),
    FACEBOOK(2),
    GOOGLE(3)
}
