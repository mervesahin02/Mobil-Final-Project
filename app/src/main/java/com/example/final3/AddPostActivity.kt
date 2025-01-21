package com.example.final3

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPostActivity : AppCompatActivity() {

    // Görsel için ImageView ve URI
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    // Firestore ve Firebase Storage referansları
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()

    // Mevcut kullanıcı e-postası
    private var currentUserEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        // Kullanıcı e-postası intent ile alınır
        currentUserEmail = intent.getStringExtra("userEmail")

        // XML'deki görsel ve buton bileşenlerini bağlama
        imageView = findViewById(R.id.imageView)
        val btnUploadImage = findViewById<Button>(R.id.btnUploadImage)
        val btnSavePost = findViewById<Button>(R.id.btnSavePost)

        // EditText bileşenlerini bağlama
        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etBirthPlace = findViewById<EditText>(R.id.etBirthPlace)
        val etBirthDate = findViewById<EditText>(R.id.etBirthDate)
        val etDeathDate = findViewById<EditText>(R.id.etDeathDate)
        val etContributions = findViewById<EditText>(R.id.etContributions)

        // Görsel yükleme butonuna tıklanınca galeri açılır
        btnUploadImage.setOnClickListener { openGallery() }

        // Gönderiyi kaydet butonuna tıklanınca işlem başlar
        btnSavePost.setOnClickListener {
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            val birthPlace = etBirthPlace.text.toString()
            val birthDate = etBirthDate.text.toString()
            val deathDate = etDeathDate.text.toString()
            val contributions = etContributions.text.toString()

            // Verilerin doluluğu kontrol edilir ve görsel yüklenir
            if (name.isNotEmpty() && surname.isNotEmpty() && imageUri != null) {
                uploadImageToFirebase(
                    name, surname, birthPlace, birthDate, deathDate, contributions
                )
            } else {
                Toast.makeText(this, "Lütfen tüm bilgileri doldurun ve bir görsel yükleyin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Galeriyi açmak için intent kullanılır
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1001)
    }

    // Galeriden seçilen görselin URI'si alınır ve gösterilir
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            try {
                // Görsel bitmap olarak alınır ve ImageView'de gösterilir
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Görsel yükleme başarısız.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firebase Storage'a görsel yükleme işlemi
    private fun uploadImageToFirebase(
        name: String,
        surname: String,
        birthPlace: String,
        birthDate: String,
        deathDate: String,
        contributions: String
    ) {
        val fileName = "${UUID.randomUUID()}.jpg" // Benzersiz dosya adı
        val storageRef = storage.reference.child("images/$fileName") // Firebase Storage'da depolama yolu

        imageUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    // Yükleme başarılı, URL alınır
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveDataToFirestore(
                            name, surname, birthPlace, birthDate, deathDate, contributions, downloadUri.toString()
                        )
                    }
                }
                .addOnFailureListener {
                    // Yükleme başarısız
                    Toast.makeText(this, "Görsel yükleme başarısız.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Firestore'a gönderi verilerini kaydetme
    private fun saveDataToFirestore(
        name: String,
        surname: String,
        birthPlace: String,
        birthDate: String,
        deathDate: String,
        contributions: String,
        imageUrl: String
    ) {
        // Gönderi verilerini içeren bir harita oluşturulur
        val postData = hashMapOf(
            "Ad" to name,
            "Soyad" to surname,
            "Doğum Yeri" to birthPlace,
            "Doğum Tarihi" to birthDate,
            "Ölüm Tarihi" to deathDate,
            "Katkıları" to contributions,
            "Görsel URL" to imageUrl,
            "Kullanıcı" to currentUserEmail,
            "Tarih" to System.currentTimeMillis() // Gönderi tarihi
        )

        // Veriler Firestore'a eklenir
        db.collection("biliminsanlari")
            .add(postData)
            .addOnSuccessListener {
                Toast.makeText(this, "Gönderi başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
                finish() // Aktiviteyi kapat
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gönderi kaydetme başarısız.", Toast.LENGTH_SHORT).show()
            }
    }
}
