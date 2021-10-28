package com.github.shokitamoto.firebase

import com.google.firebase.auth.FirebaseAuth

class LocationFirebase { // 誰が作ったかがわかる。
    var id: String = ""
    var createdAt: Long = 0L
    var uid: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
}