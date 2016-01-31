package ru.koluch.weethr

import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.koluch.wordlist.Servlet
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.github.salomonbrys.kotson.*
import javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR
import javax.servlet.http.HttpServletResponse.SC_OK
import kotlin.collections.listOf
import kotlin.collections.map

/**
 * Copyright (c) 2016 Nikolai Mavrenkov <koluch@koluch.ru>
 *
 * Distributed under the MIT License (See accompanying file LICENSE or copy at http://opensource.org/licenses/MIT).
 *
 * Created: 31.01.2016 00:03
 */
class Autocomplete: Servlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doGet(req, resp)

        val googleApiKey = ""
        val owmApiKey = ""

        val gson = Gson()

        if(req.parameterMap.containsKey("q")) {
            val q = req.getParameter("q")

            val url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + URLEncoder.encode(q, "UTF-8") + "&key=" +URLEncoder.encode(googleApiKey, "UTF-8") + "&language=en&types=(cities)";
            val dataJson: JsonObject = gson.fromJson(fetch(url))
            if(dataJson.get("status").string == "OK") {
                val result: List<String> = dataJson.get("predictions").array.map { prediction ->
                    prediction.get("terms").array[0].get("value").string
                }
                resp.setStatus(SC_OK)
                resp.setHeader("Content-Type", "application/json")
                resp.writer.write(gson.toJson(result))
            }
            else if(dataJson.get("status").string == "ZERO_RESULTS") {
                resp.setStatus(SC_OK)
                resp.setHeader("Content-Type", "application/json")
                resp.writer.write(gson.toJson(listOf<String>()))
            }
            else {
                resp.sendError(SC_INTERNAL_SERVER_ERROR, "Error: " + dataJson.get("error_message").string)
            }
        }
        else {
            resp.setStatus(SC_OK)
            resp.setHeader("Content-Type", "application/json")
            resp.writer.write(gson.toJson(listOf<String>()))
        }
    }




}