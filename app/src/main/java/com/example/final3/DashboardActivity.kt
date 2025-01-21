package com.example.final3

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {

    // Firebase Authentication ve Firestore referansları
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    // Gönderiler ve adapter için değişkenler
    private val postList = mutableListOf<Post>()
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessage: TextView
    private var currentUserEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Kullanıcı kimlik doğrulama
        auth = FirebaseAuth.getInstance()
        currentUserEmail = intent.getStringExtra("userEmail")

        // Toolbar ayarları
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // RecyclerView ve boş mesaj için ayarlar
        recyclerView = findViewById(R.id.postContainer)
        emptyMessage = findViewById(R.id.emptyMessage)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(postList)
        recyclerView.adapter = adapter

        // Firestore'dan gönderiler alınır
        fetchPostsFromFirestore()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu) // Menü oluşturma
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_post -> {
                // Gönderi ekleme aktivitesine yönlendirme
                val intent = Intent(this, AddPostActivity::class.java)
                intent.putExtra("userEmail", currentUserEmail)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                // Çıkış işlemi
                auth.signOut()
                Toast.makeText(this, "Çıkış yapıldı", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Firestore'dan gönderi verilerini çekme
    private fun fetchPostsFromFirestore() {
        db.collection("biliminsanlari")
            .orderBy("Tarih") // Tarihe göre sıralama
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                for (document in result) {
                    // Her bir belge Post nesnesine dönüştürülür
                    val post = Post(
                        name = document.getString("Ad") ?: "Ad Yok",
                        surname = document.getString("Soyad") ?: "Soyad Yok",
                        birthPlace = document.getString("Doğum Yeri") ?: "Doğum Yeri Yok",
                        birthDate = document.getString("Doğum Tarihi") ?: "Tarih Yok",
                        deathDate = document.getString("Ölüm Tarihi") ?: "Tarih Yok",
                        contributions = document.getString("Katkıları") ?: "Katkı Yok",
                        imageUrl = document.getString("Görsel URL") ?: "",
                        user = document.getString("Kullanıcı") ?: "Bilinmeyen Kullanıcı"
                    )
                    postList.add(post)
                }
                updateUI() // UI güncellemesi
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Veriler yüklenirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // UI öğelerini güncelleme
    private fun updateUI() {
        if (postList.isEmpty()) {
            emptyMessage.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            emptyMessage.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }
}
