package com.bringmetheapp.worldmonuments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
//import com.mapbox.api.geocoding.v5.MapboxGeocoding
//import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import org.json.JSONException
import java.net.URISyntaxException
import javax.security.auth.callback.Callback


class SitesMapFragment : Fragment(R.layout.fragment_sites_map), OnMapReadyCallback,
    PermissionsListener {

    private val ID_ICON_CASTLE = "Castle"
    private val ID_ICON_CHURCH = "Church"
    private val ID_ICON_MUSEUM = "Museum"
    private val ID_ICON_HISTORICAL_SITE = "Historical Site"
    private val ID_ICON_PYRAMID = "Pyramid"
    private val ID_ICON_FORT = "Fort"
    private val ID_ICON_TEMPLE = "Temple"


    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap


    private val args: SitesMapFragmentArgs by navArgs()


    private lateinit var mQueue: RequestQueue

    private var featureCollection: FeatureCollection? = null
    private lateinit var listOfFeatures: ArrayList<Feature>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)



        mQueue = Volley.newRequestQueue(context)

        jsonParse()

        val buttonSitesList = view.findViewById<Button>(R.id.mapButtonSitesList)

        buttonSitesList.setOnClickListener {
            Log.d("Feature", featureCollection.toString())
            val action = SitesMapFragmentDirections.actionSitesMapFragmentToSitesListFragment(
                args.country,
                args.categories,
                args.relevance
            )

            findNavController().navigate(action)

        }



        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS)
        { style: Style ->
            addSiteIconToStyle(style, ID_ICON_CASTLE, R.drawable.ic_castle)
            addSiteIconToStyle(style, ID_ICON_CHURCH, R.drawable.ic_church)
            addSiteIconToStyle(style, ID_ICON_MUSEUM, R.drawable.ic_museum)
            addSiteIconToStyle(
                style,
                ID_ICON_HISTORICAL_SITE,
                R.drawable.ic_historical_site
            )
            addSiteIconToStyle(style, ID_ICON_PYRAMID, R.drawable.ic_pyramid)
            addSiteIconToStyle(style, ID_ICON_FORT, R.drawable.ic_fort)
            addSiteIconToStyle(style, ID_ICON_TEMPLE, R.drawable.ic_temple)


            //User location
            enableLocationComponent(style)


        }


        //findNavController().popBackStack(R.id.sitesMapFragment, true)


        /*

        val mapboxGeocoding = MapboxGeocoding.builder()
            .accessToken(getString(R.string.mapbox_access_token))
            .query("DE")
            .geocodingTypes("country")
            .build()

         */

        val position = CameraPosition.Builder()
            .target(LatLng(51.50550, -0.07520))
            .zoom(10.0)
            .tilt(20.0)
            .build()


    }


    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())

                /*
                .pulseEnabled(true)
                .pulseColor(Color.BLUE)
                .pulseAlpha(0.2f)

                */
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }

        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }


    //Needed for user location permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //Needed for user location permission
    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        //Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }


    //Needed for user location permission
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            //Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
            //finish()
        }
    }





    fun jsonParse() {
        val url = "https://world-monuments.herokuapp.com/sites?" +
                "country=" + args.country + "&category=" + args.categories + "&relevance=" + args.relevance
        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {


                    listOfFeatures = ArrayList<Feature>()

                    val symbolManager = mapboxMap.style?.let {
                        SymbolManager(
                            mapView, mapboxMap,
                            it
                        )
                    }

                    symbolManager?.iconAllowOverlap = true
                    symbolManager?.iconIgnorePlacement = true


                    val jsonArray = response

                    for (i in 0 until jsonArray.length()) {
                        val site = jsonArray.getJSONObject(i)
                        val geonameId = site.getInt("geonameId")
                        val name = site.getString("name")
                        val longitude = site.getDouble("longitude")
                        val latitude = site.getDouble("latitude")
                        val category = site.getString("category")
                        val country = site.getString("country")
                        val countryIso = site.getString("countryIso")
                        val admin1Code = site.getString("admin1Code")
                        val admin2Code = site.getString("admin2Code")
                        val link = site.getString("link")
                        val relevance = site.getInt("relevance")
                        val description = site.getString("description")
                        val imageLink = site.getString("imageLink")

                        var properties = JsonObject()


                        properties.addProperty("geonameId", geonameId)
                        properties.addProperty("name", name)
                        properties.addProperty("category", category)
                        properties.addProperty("country", country)
                        properties.addProperty("countryIso", countryIso)
                        properties.addProperty("admin1Code", admin1Code)
                        properties.addProperty("admin2Code", admin2Code)
                        properties.addProperty("link", link)
                        properties.addProperty("relevance", relevance)
                        properties.addProperty("description", description)
                        properties.addProperty("imageLink", imageLink)


                        val siteFeature = Feature.fromGeometry(
                            Point.fromLngLat(
                                longitude.toDouble(),
                                latitude.toDouble()
                            ), properties
                        )
                        listOfFeatures.add(siteFeature)
                        // Create a symbol at the specified location.
                        var symbol = symbolManager?.create(
                            SymbolOptions()
                                .withLatLng(
                                    LatLng(
                                        latitude.toDouble(),
                                        longitude.toDouble()
                                    )
                                )
                                .withIconImage(
                                    when (category) {
                                        "Castle" -> ID_ICON_CASTLE
                                        "Fort" -> ID_ICON_FORT
                                        "Church", "Monastery" -> ID_ICON_CHURCH
                                        "Temple" -> ID_ICON_TEMPLE
                                        "Museum" -> ID_ICON_MUSEUM
                                        "Pyramid" -> ID_ICON_PYRAMID
                                        "Historical Site", "Archaeological Site", "Monument", "Ruin", "Building", "Palace" -> ID_ICON_HISTORICAL_SITE
                                        else -> {
                                            ID_ICON_HISTORICAL_SITE
                                        }


                                    }
                                )
                                .withIconSize(0.7f)
                                .withData(properties)
                                .withTextField(name)
                                .withTextSize(11f)
                                .withTextOffset(arrayOf(0f, -1.5f))


                        )
                    }




                    featureCollection = FeatureCollection.fromFeatures(listOfFeatures)

                    symbolManager?.addClickListener { symbol ->
                        val geonameId = symbol.data?.asJsonObject?.get("geonameId")?.asInt
                        val name = symbol.data?.asJsonObject?.get("name")?.asString
                        val longitude = symbol.latLng.longitude
                        val latitude = symbol.latLng.latitude
                        val category = symbol.data?.asJsonObject?.get("category")?.asString
                        val country = symbol.data?.asJsonObject?.get("country")?.asString
                        val countryIso =
                            symbol.data?.asJsonObject?.get("countryIso")?.asString
                        val admin1Code =
                            symbol.data?.asJsonObject?.get("admin1Code")?.asString
                        val admin2Code =
                            symbol.data?.asJsonObject?.get("admin2Code")?.asString
                        val link = symbol.data?.asJsonObject?.get("link")?.asString
                        val relevance = symbol.data?.asJsonObject?.get("relevance")?.asInt
                        val description =
                            symbol.data?.asJsonObject?.get("description")?.asString
                        val imageLink =
                            symbol.data?.asJsonObject?.get("imageLink")?.asString




                        Log.d("Esempio", category.toString())

                        /*

                        symbol.textField = symbol.data?.asJsonObject?.get("name").toString()
                        symbol.textSize = 10f
                        symbolManager.update(symbol)

                         */


                        val action =
                            relevance?.let {
                                geonameId?.let { it1 ->
                                    SitesMapFragmentDirections.actionSitesMapFragmentToSingleSiteFragment(
                                        it1,
                                        name.toString(),
                                        longitude.toFloat(),
                                        latitude.toFloat(),
                                        category.toString(),
                                        country.toString(),
                                        countryIso.toString(),
                                        admin1Code.toString(),
                                        admin2Code.toString(),
                                        link.toString(),
                                        it,
                                        description.toString(),
                                        imageLink.toString()
                                    )
                                }
                            }


                        if (action != null) {
                            findNavController().navigate(action)
                        }







                        false


                    }


                    //Layers
                    //mapboxMap.style?.let { createGeoJsonSource(it) }
                    //mapboxMap.style?.let { addPointsLayer(it) }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue.add(request)


    }


    private fun addPointsLayer(loadedMapStyle: Style) {
        val individualCirclesLayer = CircleLayer("sitePoints", "sitesSource")
        individualCirclesLayer.setProperties(
            PropertyFactory.circleColor(Color.RED),
            PropertyFactory.circleRadius(5f)
        )
        loadedMapStyle.addLayer(individualCirclesLayer)
    }

    private fun createGeoJsonSource(loadedMapStyle: Style) {
        try {
            Log.d("Dentro", featureCollection.toString())
            loadedMapStyle.addSource(
                GeoJsonSource(
                    "sitesSource",
                    featureCollection
                )
            )
        } catch (exception: URISyntaxException) {
            Toast.makeText(context, "Can't create GeoJson source", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addSiteIconToStyle(
        loadedMapStyle: Style,
        iconString: String,
        iconDrawable: Int
    ) {

        loadedMapStyle.addImage(
            iconString,
            BitmapUtils.getBitmapFromDrawable(resources.getDrawable(iconDrawable, null))!!,
            true
        )

    }

}

