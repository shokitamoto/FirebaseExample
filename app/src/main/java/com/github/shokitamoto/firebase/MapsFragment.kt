package com.github.shokitamoto.firebase

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import com.github.shokitamoto.firebase.databinding.FragmentMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.jar.Manifest

class MapsFragment : Fragment() {

    private var map: GoogleMap? = null
    private val markers = mutableListOf<Marker>()
    private var polyline: Polyline? = null

    private var timer: Timer? = null
    private val db = FirebaseFirestore.getInstance()
    private var binding: FragmentMapsBinding? = null


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
        map = googleMap
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.apply {
            isMyLocationButtonEnabled = true //現在地取得
            isScrollGesturesEnabled = true //スワイプで地図を平行移動
            isZoomGesturesEnabled = true // ピンチイン・アウトで縮尺の変更・画面右下にズーム変更ボタンの配置
            isRotateGesturesEnabled = true // ピンチからの回転で、地図を回転
            isMapToolbarEnabled = true // 画面の右下に表示されるGoogleMapのユーザーイターフェイス
            isTiltGesturesEnabled = true // 2本指でスワイプで視点を傾けることができる
            isCompassEnabled = true // コンパスの表示
            isZoomControlsEnabled = true

            // todo: infowindowまずは画像を使わない -> 余裕が出れば画像がある場合の実装(githubでやり方検索)
            // infowindow画像がアプリの中にある場合は問題ないが、FireStoreの画像を引っ張ってくる場合は画像の再描画が必要。グライドで画像を表示する。画像の読み込みが終わったを知らせるコールバックを使用したあとに再描画をする。
        }
        LocationData.findLast()?.also {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 10F)) // 1F~16F
        }
        updateMap()
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

        val bindingData: FragmentMapsBinding? = DataBindingUtil.bind(view)
        binding = bindingData ?: null
        bindingData?.user1?.setOnClickListener {
            // todo: user1の表示処理
        }
        bindingData?.user2?.setOnClickListener {
            // todo: user2の表示処理
        }
        bindingData?.user3?.setOnClickListener {
            // todo: user3の表示処理
        }
    }

    override fun onResume() {
        super.onResume()
        resumeTimer()
    }
    private fun resumeTimer() {
        pauseTimer()
        timer = Timer().apply {
            schedule(object: TimerTask() {
                override fun run() {
                    updateMap()
                }
            }, TIMER_DURATION, TIMER_DURATION)
        }
    }

    override fun onPause() {
        super.onPause()
        pauseTimer()
    }

    private fun pauseTimer() {
        timer?.cancel()
        timer = null
    }


    private fun updateMap() {
        val list = LocationData.findAll()
        if (list.size == markers.size)
            return
        CoroutineScope(Dispatchers.Main).launch { // マーカーの描画をするからメインスレッドで処理を行う。
            updateMarker(list)
            updatePolyline(list)
        }
    }
    fun updateMarker(list: List<LocationData>) {

            markers.forEach {
                it.remove() // markerを地図から消す
            }
            markers.clear() // リストを空にする。
            LocationData.findAll().forEach {
                val markerOptions =
                    MarkerOptions().position(LatLng(it.latitude, it.longitude)) // 場所
                val descriptor =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                markerOptions.icon(descriptor)
                map?.addMarker(markerOptions)?.also { marker ->
                    markers.add(marker) // markerがnull以外の時しか入れないよ
                }
            }
    }

    private fun updatePolyline(list: List<LocationData>) {
        polyline?.remove()
        if (list.size < 2)
            return
        val polylineOptions = PolylineOptions()
            .color(Color.BLACK)
            .width(10F)
            .addAll(list.map { LatLng(it.latitude, it.longitude) })

        polyline = map?.addPolyline(polylineOptions) // markersと違って、リストが必要ないので、clearする代わりにここで代入。

//        polyline?.isVisible = true // 他人のmapの切り替え作業。
//        markers.forEach {
//            it.isVisible = false
//        }

    }



    private fun usersMaps(list: List<LocationFirebase>) {
        val uids = list.map { LocationFirebase ->
            LocationFirebase.uid
        }.toSet() // Setに変換。uidの重複なしのリストを取得できる。toSetは重複なしで順序あり。　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
        val map: Map<String, List<LocationFirebase>> = uids.map { uid -> // ある人が旅で行った全工程が入っているmapを取得 // StringはuidがStringだから。
            Pair(uid, list.filter { it.uid == uid})
        }.toMap() // Mapに変換している

//        val aList: List<LocationFirebase>? = map["aさんのuid"] // 取得し、マーカーとポリラインを変更すると、地図上にaさんの地図が表示される。
//        val bList: List<LocationFirebase>? = map["bさんのuid"]

    }

//    private fun addData(db:LocationFirebase, uid: String, latitude: Double, longitude: Double) {
//        db.collection(users)
//            .add(user)
//
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "addData")
//            }
//            .addOnFailureListener { e ->
//                Log.d(TAG, "Error adding document" + e)
//            }
//    }


    companion object {
        private const val TIMER_DURATION = 10 * 1000L // 10秒。更新されていたらマーカーをつける
    }
}

