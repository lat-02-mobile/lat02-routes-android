package com.jalasoft.routesapp.util.helpers

enum class FirebaseCollections {
    Users,
    Countries,
    Cities,
    Lines,
    LineRoute,
    LineCategories,
    Tourpoints,
    TourpointsCategory
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

enum class WalkDirection {
    TO_FIRST_STOP, TO_NEXT_STOP, TO_DESTINATION, IS_NOT_WALKING
}
