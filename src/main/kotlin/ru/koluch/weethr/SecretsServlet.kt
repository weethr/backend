package ru.koluch.weethr

import com.google.appengine.api.datastore.*
import com.google.appengine.api.users.UserService
import com.google.appengine.api.users.UserServiceFactory
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.forEach
import kotlin.collections.listOf

/**
 * Copyright (c) 2016 Nikolai Mavrenkov <koluch@koluch.ru>
 *
 * Distributed under the MIT License (See accompanying file LICENSE or copy at http://opensource.org/licenses/MIT).
 *
 * Created: 31.01.2016 12:17
 */

const val SECRET_GOOGLE_API_KEY = "googleApiKey"
const val SECRET_OWM_API_KEY = "owmApiKey"

const val SECRET_KIND = "SECRET"
const val SECRET_PROP_VALUE = "value"

class SecretsServlet : HttpServlet() {


    val SERVLET_URL = "/secrets"

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val userService = UserServiceFactory.getUserService();
        if(!userService.isUserLoggedIn) {
            resp.sendRedirect(userService.createLoginURL(SERVLET_URL))
            return
        }
        else if(!userService.isUserAdmin) {
            resp.writer.write("<p>You need admin rights to use this servlet</p>")
            resp.writer.write("<p><a href='"+userService.createLogoutURL(SERVLET_URL)+"'>Logout</a><p>")
            return;
        }

        val datastore = DatastoreServiceFactory.getDatastoreService()

        val writer = resp.writer
        writer.write("<html><body><form method='POST' action='/secrets'>")

        listOf(SECRET_GOOGLE_API_KEY, SECRET_OWM_API_KEY).forEach { key ->
            val keyValue: String;
            try {
                keyValue = datastore.get(KeyFactory.createKey(SECRET_KIND, key)).getProperty(SECRET_PROP_VALUE) as String
            } catch(e: EntityNotFoundException) {
                keyValue = ""
            }
            writer.write("<p>")
            writer.write(key)
            writer.write(": ")
            writer.write("<input name='$key' value='$keyValue' autocomplete='off' size='80'>")
            writer.write("</p>")
        }

        writer.write("<button type='submit'>Save</button></form>")
        writer.write("<p><a href='"+userService.createLogoutURL(SERVLET_URL)+"'>Logout</a><p>")
        writer.write("</body></html>")
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val userService = UserServiceFactory.getUserService();
        if(!userService.isUserLoggedIn) {
            resp.sendRedirect(userService.createLoginURL(SERVLET_URL))
            return
        }
        else if(!userService.isUserAdmin) {
            resp.writer.write("<p>You need admin rights to use this servlet</p>")
            resp.writer.write("<p><a href='"+userService.createLogoutURL(SERVLET_URL)+"'>Logout</a><p>")
            return;
        }

        listOf(SECRET_GOOGLE_API_KEY, SECRET_OWM_API_KEY).forEach { key ->
            if(req.getParameter(key) == null) {
                throw RuntimeException("Parameter $key is mandatory")
            }

            val datastore = DatastoreServiceFactory.getDatastoreService()
            val entity: Entity;
            try {
                entity = datastore.get(KeyFactory.createKey(SECRET_KIND, key))
            } catch(e: EntityNotFoundException) {
                entity = Entity(SECRET_KIND, key)
            }
            entity.setProperty(SECRET_PROP_VALUE, req.getParameter(key))
            datastore.put(entity)
        }

        resp.sendRedirect(SERVLET_URL)
    }
}


fun java.util.Properties.getSecret(name: String): String? {
    if(this.hasProperty(name)) {
        return this.getProperty(name)
    }
    val datastore = DatastoreServiceFactory.getDatastoreService()
    try {
        val secret = datastore.get(KeyFactory.createKey(SECRET_KIND, name)).getProperty(SECRET_PROP_VALUE) as String
        this.setProperty(name, secret)
        return secret
    } catch(e: EntityNotFoundException) {
        return null
    }
}