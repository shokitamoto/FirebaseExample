package com.github.shokitamoto.firebase

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LocationData : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var title: String = ""
    var note: String = ""

}