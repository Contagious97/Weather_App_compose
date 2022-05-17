package com.example.weather_app_compose

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weather_app_compose.ui.theme.Weather_App_composeTheme

class MainActivity : ComponentActivity() {

    private var lastDownloaded: Long = 0;
    private lateinit var mRequestQueue: RequestQueue;
    private var weatherListViewModel: WeatherListViewModel = WeatherListViewModel();
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestQueue = Volley.newRequestQueue(this)
        fetchWeather(14.333,60.383)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContent {
            Weather_App_composeTheme {
                val list = weatherListViewModel.weatherList.value;
                val approvedTime = weatherListViewModel.approvedTime.value
                Scaffold(topBar = { TopAppBar(title = { Text(text = "Weather App") }) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Approvedtime(approvedTime = approvedTime)
                        LazyColumn(
                            Modifier.weight(1f,true)
                        ){
                            items(list.size){
                                    item -> WeatherCard(weather_info = list[item],resources.getIdentifier(list[item].wsymbIcon,"drawable",packageName))
                            }
                        }
                        BottomRow()
                    }
                }
            }
        }
    }

    fun fetchWeather(longitude: Double, latitude: Double){
        val url =
            "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/$longitude/lat/$latitude/data.json"
        val devUrl = "https://maceo.sth.kth.se/weather/forecast?lonLat=lon/14.333/lat/60.383"

        val connMgr: ConnectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.getActiveNetworkInfo()

        if (networkInfo != null && networkInfo.isConnected()) {
            val weatherRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    try {
                        val weatherList = WeatherParser.parseWeather(response)
                        weatherListViewModel.weatherList.value = weatherList.first;
                        weatherListViewModel.approvedTime.value = weatherList.second;

                        lastDownloaded = System.currentTimeMillis()
                        mRequestQueue.cancelAll(this)

                    } catch (e: Exception) {
                        Log.i("Parsing error", e.toString())
                    }
                },
                errorListener
            )
            weatherRequest.tag = this
            mRequestQueue.add(weatherRequest)
        } else {
            Log.i("Network error", "not connected?")
            createMsgDialog(
                "Network error",
                "Not connected to the internet\nUsing saved data"
            ).show()


            /*try {
                weatherList = gson.fromJson(json, ListOfWeathers::class.java)
                lastDownloaded = mPreferences.getLong("saved_lastDownload", 0L)

                mHeader.setText(R.string.header_saved_data)

                val timeTemp = weatherList.approvedTime.replace("T", " ").split(":")
                val approvedTime = timeTemp[0] + ":" + timeTemp[1]

                mApprovedText.text = approvedTime
                mWeatherListAdapter = WeatherListAdapter(this, weatherList.timeSeries)
                mRecyclerView.adapter = mWeatherListAdapter
            } catch (e: Exception) {
                Log.i("Resuming error", e.toString())
                createMsgDialog("Loading data error", "Error loading saved data").show()
                mHeader.setText(R.string.error_msg)
            }*/
        }

    }

    private val errorListener =
        Response.ErrorListener { error ->
            Log.i("Volley error", error.toString())
            createMsgDialog(
                "Network error",
                "Out of bounds"
            ).show()
        }

    private fun createMsgDialog(title: String, msg: String): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)

        builder.setPositiveButton(
            "Ok"
        ) { _, _ -> }

        return builder.create()
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
@Composable
fun TextFieldLongitude(){
    var text by remember { mutableStateOf("")}
    var width = LocalConfiguration.current.screenWidthDp
    TextField(
        value = text,
        onValueChange = {
            newText -> text = newText
        },
        placeholder = { Text(text = "Enter Longitude") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(width.dp/2 -25.dp)
    )

}
@Composable
fun TextFieldLatitude(){
    var text by remember { mutableStateOf("")}
    var width = LocalConfiguration.current.screenWidthDp
    TextField(
        value = text,
        onValueChange = {
                newText -> text = newText
        },
        placeholder = { Text(text = "Enter Latitude") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(width.dp/2 -25.dp)
    )
}

@Composable
fun BottomRow(){
    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        TextFieldLongitude()
        TextFieldLatitude()
        Button(onClick = { /*TODO*/ },Modifier.height(55.dp)) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "")
        }
    }
}
@Preview
@Composable
fun BottomRowPrev(){
    Weather_App_composeTheme {
        Row(horizontalArrangement = Arrangement.Center) {
            TextFieldLongitude()
            TextFieldLatitude()
            Button(onClick = { /*TODO*/ }, modifier = Modifier.height(55.dp)) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "")
            }
        }
    }

}

/*@Preview
@Composable
fun LazyList(){
    val list : MutableList<WeatherInfo> = mutableListOf()


    for (i in 0 until 10){
        list[i] = WeatherInfo(8.0,"heu","ss")
    }
    LazyColumn(){
        items(list.size){
                item -> WeatherCard(weather_info = list[item],resources.getIdentifier(list[item].wsymbIcon,"drawable",packageName)
        }
    }
}*/

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Weather_App_composeTheme {
        Greeting("Android")
    }
}

/*@Preview(showBackground = true)
@Composable
fun CardPreview() {
    val weatherinfo : WeatherInfo = WeatherInfo(8.0,"hey","asd")

    Weather_App_composeTheme {
        WeatherCard(weather_info = weatherinfo)
    }
}*/

@Composable
fun Approvedtime(approvedTime: String){
    Text(
        modifier = Modifier.height(40.dp),
        text = "Approved Time: $approvedTime",
    )
}

@Composable
fun WeatherCard(weather_info : WeatherInfo, id: Int){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)
            .height(90.dp),
        //elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = weather_info.approvedTime, modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherIcon(id)
                WeatherTemperature(weather_info.temperature)
            }
        }
    }
}

@Composable
fun WeatherIcon(id: Int){
    Image(painter = painterResource(id = id), contentDescription = "Day",
        modifier = Modifier.height(70.dp))
}

@Composable
fun WeatherTemperature(temperature : Double){
    Text(text = temperature.toString() + "\u2103")
}

