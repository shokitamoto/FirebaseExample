package com.github.shokitamoto.firebase

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class LocationData : RealmObject() {
    @PrimaryKey
    open var id: String = UUID.randomUUID().toString()
    open var createdAt: Long = System.currentTimeMillis()
    open var latitude: Double? = 0.0
    open var longitude: Double? = 0.0

}