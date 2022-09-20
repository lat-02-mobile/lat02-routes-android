package com.jalasoft.routesapp.ui.routes.viewModel

import android.location.Location
import android.location.LocationManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.Route
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LineCategoryIcons
import com.jalasoft.routesapp.data.remote.interfaces.DirectionsRepository
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.algorithm.RouteCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel
@Inject
constructor(private val repository: RouteRepository, private val gDirectionsRepository: DirectionsRepository) : ViewModel() {

    var _routesList: MutableLiveData<List<LineInfo>> = MutableLiveData()
    val routesList: LiveData<List<LineInfo>> = _routesList
    var originalList: List<LineInfo> = listOf()

    private var _possibleRoutesList: MutableLiveData<List<AvailableTransport>> = MutableLiveData()
    val possibleRoutesList: LiveData<List<AvailableTransport>> = _possibleRoutesList
    var possibleRoutesOriginalList: List<AvailableTransport> = listOf()
    private var _directionsList: MutableLiveData<List<Route>> = MutableLiveData()
    var directionsList: LiveData<List<Route>> = _directionsList

    fun fetchLines(context: Context) = viewModelScope.launch {
        _routesList.value = repository.getAllLines(context)
        originalList = _routesList.value ?: listOf()
    }

    fun filterLines(criteria: String): Int {
        _routesList.value = originalList.filter { line ->
            line.name.lowercase().contains(criteria.lowercase())
        }
        return _routesList.value?.size ?: 0
    }

    fun fetchDirections(startLocation: StartLocation, endLocation: EndLocation) = viewModelScope.launch {
        val fetchedPlacesResponse = gDirectionsRepository.getDirections(startLocation, endLocation)
        fetchedPlacesResponse.data?.let {
            _directionsList.value = it
            directionsList = _directionsList
        }
    }

    // TODO("Change this method when joining locations with the routes")
    fun getPossibleRoutes() = viewModelScope.launch {
        val start1 = RouteAlgorithmFakeData.coordinatesToLocation(-16.52035351419114, -68.12580890707301)
        val end1 = RouteAlgorithmFakeData.coordinatesToLocation(-16.524285569842718, -68.12298370418992)
        val points1 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.points1Array)
        val stops1 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops1Array)

        val lineCategoryIcons1 = LineCategoryIcons(
            "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fbus_white.png?alt=media&token=980b407c-2fc7-4fd2-b8da-a5504a7c7f1c",
            "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fbus_black.png?alt=media&token=21c3ba52-27ed-499a-933a-a31c8f2062ba"
        )
        val line1 = LineRoutePath("", "Line 1", "Bus", "1", points1, start1, end1, stops1, lineCategoryIcons1, "#67F5ED", 3.0)

        val start2 = RouteAlgorithmFakeData.coordinatesToLocation(-16.5255314, -68.1254204)
        val end2 = RouteAlgorithmFakeData.coordinatesToLocation(-16.5241937, -68.1204527)
        val points2 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.points2Array)

        val stops2 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops2Array)
        val lineCategoryIcons2 = LineCategoryIcons(
            "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fmini_bus_white.png?alt=media&token=e67646cd-457d-4563-a12f-55a17c478150",
            "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fmini_bus_black.png?alt=media&token=cdda76ab-ec71-42b7-a10a-a665d6b03155"
        )

        val line2 = LineRoutePath("", "246", "Mini", "2", points2, start2, end2, stops2, lineCategoryIcons2, "#6495ED", 3.2)

        val start3 = RouteAlgorithmFakeData.coordinatesToLocation(-16.5206262, -68.1227148)
        val end3 = RouteAlgorithmFakeData.coordinatesToLocation(-16.5244779, -68.1253892)
        val points3 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(
            RouteAlgorithmFakeData.points3Array
        )

        val stops3 = RouteAlgorithmFakeData.arrayToMutableListOfLocation(RouteAlgorithmFakeData.stops3Array)
        val lineCategoryIcons3 = LineCategoryIcons(
            "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fsubway_white.png?alt=media&token=c3fe8f8e-7696-4879-b042-52e710f94842",
            "https://firebasestorage.googleapis.com/v0/b/routes-app-8c8e4.appspot.com/o/lineCategories%2Fsubway_black.png?alt=media&token=7f8c755c-da68-4b85-8bc1-1df26ecb92d8"
        )

        val line3 = LineRoutePath("", "LA", "Metro", "3", points3, start3, end3, stops3, lineCategoryIcons3, "#533483", 3.4)

        val lines = listOf(line1, line2, line3)

        val originPoint = RouteAlgorithmFakeData.coordinatesToLocation(-16.52078, -68.12344)
        val destinationPoint = RouteAlgorithmFakeData.coordinatesToLocation(-16.52455, -68.12269)
        val minDistance = 200.0
        val minDistanceBtwStops = 200.0

        val result = RouteCalculator.calculate(lines, destinationPoint, originPoint, minDistance, minDistanceBtwStops)
        _possibleRoutesList.value = result
        possibleRoutesOriginalList = _possibleRoutesList.value!!
    }
}

// TODO("Replace this fake data by fetching the lines from firebase")
object RouteAlgorithmFakeData {

    val points1Array = listOf(
        listOf(-16.520939322501413, -68.12557074070023),
        listOf(-16.521062847351256, -68.12514516472181),
        listOf(-16.52130602845841, -68.12417648825397),
        listOf(-16.521670987319112, -68.12320625310048),
        listOf(-16.52197231180913, -68.12260107624422),
        listOf(-16.522451435494332, -68.12218135682076),
        listOf(-16.523261825566387, -68.12214426533951),
        listOf(-16.523703514803486, -68.1221403609752),
        listOf(-16.523780248849537, -68.12235510114752),
        listOf(-16.524002964559173, -68.12266159393164),
        listOf(-16.524285569842718, -68.12298370418992)
    )

    val stops1Array = listOf(
        listOf(-16.520939322501413, -68.12557074070023),
        listOf(-16.52130602845841, -68.12417648825397),
        listOf(-16.521670987319112, -68.12320625310048),
        listOf(-16.522451435494332, -68.12218135682076),
        listOf(-16.523780248849537, -68.12235510114752),
        listOf(-16.524285569842718, -68.12298370418992)
    )

    val points2Array = listOf(
        listOf(-16.5255314,	-68.1254204),
        listOf(-16.5247497,	-68.1251629),
        listOf(-16.5247755,	-68.1246533),
        listOf(-16.5251612,	-68.1243314),
        listOf(-16.5251046,	-68.1238218),
        listOf(-16.5246006,	-68.1232156),
        listOf(-16.5245543,	-68.1218155),
        listOf(-16.5247286,	-68.1216115),
        listOf(-16.5241937,	-68.1204527)
    )

    val stops2Array = listOf(
        listOf(-16.5255314, -68.1254204),
        listOf(-16.5246006,	-68.1232156),
        listOf(-16.5241937,	-68.1204527)
    )

    val points3Array = listOf(
        listOf(-16.5206262, -68.1227148),
        listOf(-16.5209862, -68.1229079),
        listOf(-16.5212999, -68.1231064),
        listOf(-16.5216239, -68.1231976),
        listOf(-16.5220662, -68.1233478),
        listOf(-16.5226807, -68.1237467),
        listOf(-16.5230562, -68.1242724),
        listOf(-16.5232053, -68.1245996),
        listOf(-16.5235817, -68.1248782),
        listOf(-16.5237617, -68.124964),
        listOf(-16.5241114, -68.1251303),
        listOf(-16.5244779, -68.1253892)
    )

    val stops3Array = listOf(
        listOf(-16.5206262, -68.1227148),
        listOf(-16.5216239, -68.1231976),
        listOf(-16.5232053, -68.1245996),
        listOf(-16.5244779, -68.1253892)
    )

    fun coordinatesToLocation(lat: Double, lon: Double): Location {
        val location = Location(LocationManager.NETWORK_PROVIDER)
        location.longitude = lon
        location.latitude = lat
        return location
    }

    fun arrayToMutableListOfLocation(list: List<List<Double>>): MutableList<Location> {
        val points: MutableList<Location> = mutableListOf()
        list.forEach {
            val location = coordinatesToLocation(it[0], it[1])
            points.add(location)
        }
        return points
    }
}
