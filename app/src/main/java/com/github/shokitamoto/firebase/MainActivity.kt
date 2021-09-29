package com.github.shokitamoto.firebase

import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapsFragment = MapsFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            add(R.id.fragment_maps, mapsFragment)
            commit()
        }
    }

//    https://qiita.com/potato-refrain/items/03acd46a5d804cf45302
//    private fun createLocationRequest() {
//
//        this.locationRequest = LocationRequest.create()
//
//        // 位置情報に接続するために、位置情報リクエストを追加
//        val builder = LocationSettingRequest.Builder().addLocationRequest(locationRequest!!)
//
//        // 現在の設定が満たされているかチェックする
//        val client: SettingsClient = LocationServices.getSettingsClient(this)
//        val task: Task<LocationSettingResponse> = client.checkLocationSettings(builder.build())
//        task.addOnSuccessListener { locationSettingResponse ->
//            requestingLocationUpdates = true
//        }
//
//        // 以下のチェックは、Android端末の設定→位置情報がONになっていない場合にONにする設定。（アプリレベルの許可は別）
//        // エラーが発生した場合でResolvableApiExceptionが発生した場合は位置情報サービスを有効にするか確認する
//
//        task.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException) {
//                try {
//                    exception.startResolutionForResult(
//                        this,
//                        REQUEST_CHECK_SETTINGS
//                    )
//                    requestingLocationUpdates = true
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    // Ignore the error.
//                }
//            }
//        }
//
//        // アプリに位置情報の使用を許可する
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            // ok→BackGroundが許可されているかチェック
//            val backgroundLocationPermissionApproved =
//                ActivityCompat.checkSelfPermission(
//                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//            // 許可されている
//            if (backgroundLocationPermissionApproved) {
//            }
//            // 許可されていないのでバックグラウンド許可を求める
//            else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
//                    REQUEST_BACKGROUND_SETTINGS
//                )
//            }
//        }
//        // 許可されていない場合は許可を求める
//        else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                ),
//                REQUEST_ALL
//            )
//        }
//    }
}



