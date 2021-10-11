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
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {

    private var hasCompletedInitMap = false
    private val sharedPreference by lazy { getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE) } // MODE_PRIVATE 他のアプリから見られなくなる。

    // 現在地の取得
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(bottom_navigation, navController)

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
            add(R.id.fragment_maps, mapsFragment) // fragmentを乗せる操作
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

}



