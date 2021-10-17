package com.github.shokitamoto.firebase

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.jar.Manifest

class MapsFragment : Fragment() {

    @SuppressLint("MissingPermission") // 「googleMap.isMyLocationEnabled = true」はパーミッションのチェックをしてからじゃないと呼べないが、「@SuppressLint("MissingPermission")」をつけることでチェックなしに呼べる。

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.apply {
            isMyLocationButtonEnabled = true //現在地取得
            isScrollGesturesEnabled = true //スワイプで地図を平行移動
            isZoomGesturesEnabled = true // ピンチイン・アウトで縮尺の変更・画面右下にズーム変更ボタンの配置
            isRotateGesturesEnabled = true // ピンチからの回転で、地図を回転
            isMapToolbarEnabled = true // 画面の右下に表示されるGoogleMapのユーザーイターフェイス
            isTiltGesturesEnabled = true // 2本指でスワイプで視点を傾けることができる
            isCompassEnabled = true // コンパスの表示


            // todo: marker(ピン)
            // todo: polyline(線) ○○km以上移動した場合に線を引くなどの設定
            // todo: fuse~~ アプリの起動中にはひたすらRealmやRoomにデータを書き込んでいく
            // todo: infowindowまずは画像を使わない -> 余裕が出れば画像がある場合の実装(githubでやり方検索)
            // infowindow画像がアプリの中にある場合は問題ないが、FireStoreの画像を引っ張ってくる場合は画像の再描画が必要。グライドで画像を表示する。画像の読み込みが終わったを知らせるコールバックを使用したあとに再描画をする。
        }

        val polyline = googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.364, 147.891),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)
                )
        )

        LocationData.findAll().forEach {
            val marker = MarkerOptions().position(LatLng(it.latitude, it.longitude)) // 場所
            val descriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            marker.icon(descriptor)
            googleMap.addMarker(marker)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_maps) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


}