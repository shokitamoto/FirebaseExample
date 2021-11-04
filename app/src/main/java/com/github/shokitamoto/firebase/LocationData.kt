package com.github.shokitamoto.firebase

import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

open class LocationData : RealmObject() {
    @PrimaryKey
    open var id: String = UUID.randomUUID().toString() // インスタンスを作った瞬間idは生成される。
    open var createdAt: Long = System.currentTimeMillis() // インスタンスを作った瞬間createdAtは生成される。
    open var latitude: Double = 0.0
    open var longitude: Double = 0.0

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
            Realm.getDefaultInstance().use { realm -> // realmはRealmのインスタンス realm ->とすることで、インスタンス名を指定することができる。指定しなければインスタンス名はit
                realm.where(LocationData::class.java)
                    .sort(LocationData::createdAt.name) // デフォルトが昇順なので、Sort.ASCENDINGと書く必要なし。
                    .findAll()
                    .let {
                        realm.copyFromRealm(it)
                    }
            }

        fun findLast(): LocationData? =
        Realm.getDefaultInstance().use {  realm -> // realmはRealmのインスタンス realm ->とすることで、インスタンス名を指定することができる。指定しなければインスタンス名はit
            realm.where(LocationData::class.java)
                .sort(LocationData::createdAt.name, Sort.DESCENDING) // DESCENDING(降順のfindFirstで最後のやつをとる。
                .findFirst()
                ?.let {
                    realm.copyFromRealm(it)
                }
        }
    }
}