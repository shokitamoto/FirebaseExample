package com.github.shokitamoto.firebase

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private var hasCompletedInitMap = false
    private val sharedPreference by lazy { getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE) } // MODE_PRIVATE 他のアプリから見られなくなる。
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = FusedLocationProviderClient(this)

        // どのような取得方法を要求するか
        val locationRequest = LocationRequest.create()?.apply {
            // 精度重視(電力大)と省電力重視(精度低)を両立するため2種類の更新間隔を指定
            // 公式のサンプル通り
            interval = 10000 // 最遅の更新間隔(正確ではない)
            fastestInterval = 5000 // 最短の更新間隔
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 精度重視
        }

        // コールバック
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                // 更新直後の位置が格納されているはず
                val location = locationResult?.lastLocation ?: return
                Toast.makeText(this@MainActivity, "緯度:${location.latitude}, 軽度:${location.longitude}", Toast.LENGTH_LONG).show()
            }
        }
        // 位置情報を更新
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                &&
                sharedPreference.getBoolean(IS_CHECKED_NOT_ASK_AGAIN_LOCATION, false)) {
                    // TODO: showDialog()
                showSettingApp()
                return
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            return
        }
        initFragment()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 位置測定を始めるコードへ跳ぶ
                initFragment()

            } else {
                sharedPreference.edit().putBoolean(IS_CHECKED_NOT_ASK_AGAIN_LOCATION, true).apply()
            }
        }
    }

    private fun initFragment() {
        if (hasCompletedInitMap) { // hasCompletedInitMapがtrueだと、initFragmentはreturnを返す（何も返さない）
            return
        } else {
            hasCompletedInitMap = true
        }
        val mapsFragment = MapsFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            add(R.id.fragmentContainerView, mapsFragment) // fragmentoを乗せる操作
            addToBackStack(mapsFragment.hashCode().toString()) // 戻るボタンを押すと、そのFragmentだけ剥がすことができる。
            commit()
        }
    }

    private fun showSettingApp() { // TODO:下記のプログラム前にDialogを出して、ユーザーに設定画面から位置情報権限をONにしてもらう必要がある。
        val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri= Uri.fromParts("package", packageName, null)
        intent.data= uri
        startActivity(intent)
    }


    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1000
        private const val IS_CHECKED_NOT_ASK_AGAIN_LOCATION = "IS_CHECKED_NOT_ASK_AGAIN_LOCATION"
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



