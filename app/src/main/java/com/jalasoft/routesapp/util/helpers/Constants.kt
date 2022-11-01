package com.jalasoft.routesapp.util.helpers
object Constants {
    const val CODE_VERIFICATION_TIME_OUT = 60L
    const val BUNDLE_KEY_ROUTE_SELECTED_DATA = "routeSelected"
    const val BUNDLE_KEY_ROUTE_SELECTED_POSITION = "routeSelectedPosition"
    const val CURRENT_SPANISH_LANGUAGE = "spa"
    const val POLYLINE_PADDING = 250
    const val AVG_WALKING_PACE = 1.45288 // meters per sec
    const val WALK_CATEGORY = "Walk"
    const val MIN_DISTANCE_FROM_ORIGIN_DESTINATION = 200.0
    const val MIN_DISTANCE_BETWEEN_STOPS = 200.0
    const val DEFAULT_CATEGORY_WHITE_ICON = "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fbus_white.png?alt=media&token=980b407c-2fc7-4fd2-b8da-a5504a7c7f1c"
    const val DEFAULT_CATEGORY_BLACK_ICON: String = "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fbus_black.png?alt=media&token=21c3ba52-27ed-499a-933a-a31c8f2062ba"
    const val DIRECTIONS_MODE = "walking"
    const val MINIMUM_DISTANCE_ORIGIN_DESTINATION = 500
    const val MINIMUM_DISTANCE_TO_STILL_FAVORITE = 8
    const val DEFAULT_POLYLINE_COLOR = "#004696"
    const val BUNDLE_KEY_LINE_ADMIN_SELECTED_DATA = "lineAdminSelected"
    const val BUNDLE_KEY_LINE_ADMIN_IS_NEW = "lineAdminIsNew"
}
