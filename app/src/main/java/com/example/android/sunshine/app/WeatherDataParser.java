package com.example.android.sunshine.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        JSONObject forecastJson = new JSONObject(weatherJsonStr);
        JSONArray list = forecastJson.getJSONArray("list");
        if(dayIndex < 0 || dayIndex >= list.length())
        {
            return -1;
        }
        JSONObject day = list.getJSONObject(dayIndex);
        JSONObject temps = day.getJSONObject("temps");

        return temps.getDouble("max");
    }

}
