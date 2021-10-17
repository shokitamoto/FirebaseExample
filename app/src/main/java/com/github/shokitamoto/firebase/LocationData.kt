package com.github.shokitamoto.firebase

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

open class LocationData : RealmObject() {
    @PrimaryKey
    open var id: String = UUID.randomUUID().toString()
    open var createdAt: Long = System.currentTimeMillis()
    open var latitude: Double? = 0.0
    open var longitude: Double? = 0.0

    companion object {
        fun insertOrUpdate(data: LocationData) {
            CoroutineScope(Dispatchers.IO).launch {
                Realm.getDefaultInstance().use {
                    it.executeTransaction { realm ->
                        realm.insertOrUpdate(data)
                    }
                }
            }
        }

        fun findAll(): List<LocationData> =
            Realm.getDefaultInstance().use {  realm -> // realmはRealmのインスタンス realm ->とすることで、インスタンス名を指定することができる。指定しなければインスタンス名はit
                realm.where(LocationData::class.java).findAll()
                    .let {
                        realm.copyFromRealm(it)
                    }
            }
    }
}