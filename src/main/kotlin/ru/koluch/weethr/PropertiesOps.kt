package ru.koluch.weethr

import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.EntityNotFoundException
import com.google.appengine.api.datastore.KeyFactory
import kotlin.collections.get

/**
 * Copyright (c) 2016 Nikolai Mavrenkov <koluch@koluch.ru>
 *
 * Distributed under the MIT License (See accompanying file LICENSE or copy at http://opensource.org/licenses/MIT).
 *
 * Created: 31.01.2016 12:48
 */

fun java.util.Properties.hasProperty(name: String): Boolean {
    return this.keys.contains(name)
}

