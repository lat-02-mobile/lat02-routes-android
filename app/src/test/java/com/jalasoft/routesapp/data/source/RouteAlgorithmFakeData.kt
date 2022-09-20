package com.jalasoft.routesapp.data.source

import android.location.Location
import android.location.LocationManager
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LineRoutePath

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

    // points for test 1 and for
    private val points4_1 = listOf(
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

    private val stops4_1 = listOf(
        listOf(-16.52130602845841, -68.12417648825397),
        listOf(-16.521670987319112, -68.12320625310048),
        listOf(-16.522451435494332, -68.12218135682076),
        listOf(-16.523780248849537, -68.12235510114752),
        listOf(-16.524285569842718, -68.12298370418992)
    )

    private val points4_2_1 = listOf(
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

    private val stops4_2_1 = listOf(
        listOf(-16.5206262, -68.1227148),
        listOf(-16.5216239, -68.1231976),
        listOf(-16.5232053, -68.1245996),
        listOf(-16.5244779, -68.1253892)
    )

    private val points4_2_2 = listOf(
        listOf(-16.5255314,	-68.1254204),
        listOf(-16.5247497,	-68.1251629),
        listOf(-16.5247755,	-68.1246533),
        listOf(-16.5251612,	-68.1243314),
        listOf(-16.5251046,	-68.1238218),
        listOf(-16.5246006,	-68.1232156)
    )

    private val stops4_2_2 = listOf(
        listOf(-16.5255314, -68.1254204),
        listOf(-16.5246006,	-68.1232156)
    )
    private const val idLine1 = "aqafwegtfe12"
    private const val idLine2 = "gtgglvmsbvk1"
    private const val idLine3 = "vmbrormfkjg9"

    val result1_4 = mutableListOf(
        AvailableTransport(
            null,
            mutableListOf(
                LineRoutePath(
                    "1",
                    "Line 1",
                    null,
                    "Bus",
                    idLine1,
                    arrayToMutableListOfLocation(points4_1).toList(),
                    coordinatesToLocation(-16.52035351419114, -68.12580890707301),
                    coordinatesToLocation(-16.524285569842718, -68.12298370418992),
                    arrayToMutableListOfLocation(stops4_1).toList()
                )
            )
        ),
        AvailableTransport(
            3,
            mutableListOf(
                LineRoutePath(
                    "2",
                    "LA",
                    null,
                    "Metro",
                    idLine2,
                    arrayToMutableListOfLocation(points4_2_1).toList(),
                    coordinatesToLocation(-16.5206262, -68.1227148),
                    coordinatesToLocation(-16.5244779, -68.1253892),
                    arrayToMutableListOfLocation(stops4_2_1).toList()
                ),
                LineRoutePath(
                    "3",
                    "246",
                    null,
                    "Mini",
                    idLine3,
                    arrayToMutableListOfLocation(points4_2_2).toList(),
                    coordinatesToLocation(-16.5255314, -68.1254204),
                    coordinatesToLocation(-16.5241937, -68.1204527),
                    arrayToMutableListOfLocation(stops4_2_2).toList()
                )
            )
        )
    )

    // points for test 2
    private val points2_1_1 = listOf(
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

    private val stops2_1_1 = listOf(
        listOf(-16.52130602845841, -68.12417648825397),
        listOf(-16.521670987319112, -68.12320625310048),
        listOf(-16.522451435494332, -68.12218135682076),
        listOf(-16.523780248849537, -68.12235510114752),
        listOf(-16.524285569842718, -68.12298370418992)
    )

    private val points2_1_2 = listOf(
        listOf(-16.5246006,	-68.1232156),
        listOf(-16.5245543,	-68.1218155),
        listOf(-16.5247286,	-68.1216115),
        listOf(-16.5241937,	-68.1204527)
    )

    private val stops2_1_2 = listOf(
        listOf(-16.5246006,	-68.1232156),
        listOf(-16.5241937,	-68.1204527)
    )

    private val points2_2_1 = listOf(
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

    private val stops2_2_1 = listOf(
        listOf(-16.5216239, -68.1231976),
        listOf(-16.5232053, -68.1245996),
        listOf(-16.5244779, -68.1253892)
    )

    val result2 = mutableListOf(
        AvailableTransport(
            4,
            mutableListOf(
                LineRoutePath(
                    "4",
                    "Line 1",
                    null,
                    "Bus",
                    idLine1,
                    arrayToMutableListOfLocation(points2_1_1).toList(),
                    coordinatesToLocation(-16.52035351419114, -68.12580890707301),
                    coordinatesToLocation(-16.524285569842718, -68.12298370418992),
                    arrayToMutableListOfLocation(stops2_1_1).toList()
                ),
                LineRoutePath(
                    "5",
                    "246",
                    null,
                    "Mini",
                    idLine1,
                    arrayToMutableListOfLocation(points2_1_2).toList(),
                    coordinatesToLocation(-16.5255314,	-68.1254204),
                    coordinatesToLocation(-16.5241937,	-68.1204527),
                    arrayToMutableListOfLocation(stops2_1_2).toList()
                )
            )
        ),
        AvailableTransport(
            2,
            mutableListOf(
                LineRoutePath(
                    "6",
                    "LA",
                    null,
                    "Metro",
                    idLine1,
                    arrayToMutableListOfLocation(points2_2_1).toList(),
                    coordinatesToLocation(-16.5206262, -68.1227148),
                    coordinatesToLocation(-16.5244779, -68.1253892),
                    arrayToMutableListOfLocation(stops2_2_1).toList()
                ),
                LineRoutePath(
                    "7",
                    "246",
                    null,
                    "Mini",
                    idLine1,
                    arrayToMutableListOfLocation(points2Array).toList(),
                    coordinatesToLocation(-16.5255314,	-68.1254204),
                    coordinatesToLocation(-16.5241937,	-68.1204527),
                    arrayToMutableListOfLocation(stops2Array).toList()
                )
            )
        )
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

    fun compareResults(result: MutableList<AvailableTransport>, resultToCompare: MutableList<AvailableTransport>): Boolean {
        if (result.size != resultToCompare.size) return false
        result.forEachIndexed { i, availableTransport ->
            if (availableTransport.connectionPoint != resultToCompare[i].connectionPoint ||
                availableTransport.transports.size != resultToCompare[i].transports.size
            ) return false
            availableTransport.transports.forEachIndexed { j, linePath ->
                if (!compareLocations(linePath.end, resultToCompare[i].transports[j].end) ||
                    !compareLocations(linePath.start, resultToCompare[i].transports[j].start) ||
                    linePath.category != resultToCompare[i].transports[j].category ||
                    linePath.name != resultToCompare[i].transports[j].name ||
                    linePath.routePoints.size != resultToCompare[i].transports[j].routePoints.size ||
                    linePath.stops.size != resultToCompare[i].transports[j].stops.size
                ) return false
                linePath.routePoints.forEachIndexed { k, location ->
                    if (!compareLocations(location, resultToCompare[i].transports[j].routePoints[k])) return false
                }
                linePath.stops.forEachIndexed { k, location ->
                    if (!compareLocations(location, resultToCompare[i].transports[j].stops[k])) return false
                }
            }
        }
        return true
    }

    private fun compareLocations(location1: Location?, location2: Location?): Boolean {
        return location1?.latitude == location2?.latitude && location1?.longitude == location2?.longitude
    }
}
