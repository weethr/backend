package ru.koluch.weethr

import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URLEncoder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.listOf
import kotlin.collections.map

/**
 * Copyright (c) 2016 Nikolai Mavrenkov <koluch@koluch.ru>
 *
 * Distributed under the MIT License (See accompanying file LICENSE or copy at http://opensource.org/licenses/MIT).
 *
 * Created: 31.01.2016 13:00
 */
class WeatherServlet : Servlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doGet(req, resp)
        resp.characterEncoding = "UTF-8"

        if(!req.parameterMap.containsKey("q")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'q' is mandatory")
            return
        }

        val q = req.getParameter("q")
        val owmApiKey: String = env.getSecret(SECRET_OWM_API_KEY) ?: throw RuntimeException("Google api key is not defined")

        var url = "http://api.openweathermap.org/data/2.5/weather?q=${URLEncoder.encode(q, "UTF-8")}&appid=$owmApiKey&units=metric";

        val responseText = fetch(url)
        resp.setStatus(HttpServletResponse.SC_OK)
        resp.setHeader("Content-Type", "application/json")
        resp.writer.write(responseText)

    }
}