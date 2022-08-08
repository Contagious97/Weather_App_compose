package com.example.weather_app_compose

import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val APPROVEDTIME = "approvedTime"
private const val TIMESERIES = "timeSeries"
private const val VALIDTIME = "validTime"
private const val PARAMETERS = "parameters"
private const val NAME = "name"
private const val WSYMB2 = "Wsymb2"
private const val T = "t"
private const val VALUES = "values"
private const val GEOMETRY = "geometry"
private const val COORDINATES = "coordinates"

class WeatherParser {

    companion object{
        fun parseWeather(response: JSONObject): Pair<List<WeatherInfo>, String>{
            var timeTemp = response.getString("approvedTime").replace("T", " ").split(":")
            var approvedTime = timeTemp[0] + ":" + timeTemp[1]


            val coordArray = response.getJSONObject(GEOMETRY).getJSONArray(COORDINATES).getJSONArray(0)
            val coord: Pair<Float, Float> = Pair(coordArray.get(0).toString().toFloat(), coordArray.get(1).toString().toFloat())

            val timeSeries: List<WeatherInfo> = getTimeSeries(response.getJSONArray(TIMESERIES))

            return Pair(timeSeries,approvedTime);
        }

        private fun getTimeSeries(jsonArray: JSONArray): List<WeatherInfo> {
            val timeSeries = ArrayList<WeatherInfo>()

            for (i in 0 until jsonArray.length()) {
                val validTimeTemp: String = jsonArray.getJSONObject(i).getString(VALIDTIME)
                val paramArr = jsonArray.getJSONObject(i).getJSONArray(PARAMETERS)

                var tempTemp = 0.0
                var wsymb_icon = ""

                for (j in 0 until paramArr.length()) {
                    val param = paramArr.getJSONObject(j)
                    if (param.getString(NAME).equals(T)) {
                        tempTemp = param.getJSONArray(VALUES).getDouble(0)
                    } else if (param.getString(NAME).equals(WSYMB2)) {
                        if (validTimeTemp.split("T")[1] < "05:00" || validTimeTemp.split("T")[1] > "18:00") {
                            wsymb_icon = "day_" + param.getJSONArray("values").getInt(0).toString()
                        } else {
                            wsymb_icon = "night_" + param.getJSONArray(VALUES).getInt(0).toString()
                        }
                    }
                }
                val temp = validTimeTemp.replace("T", " ").split(":")
                val validTime = temp[0]+":"+temp[1]
                timeSeries.add(WeatherInfo(tempTemp, validTime, wsymb_icon))
            }
            return timeSeries
        }
    }

}
