package com.example.final3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // Firebase Authentication referansı
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase Authentication başlatılır
        auth = FirebaseAuth.getInstance()

        // XML'deki bileşenleri bağlama
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Kayıt ol butonuna tıklanınca yeni bir kullanıcı oluşturulur
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // E-posta ve şifre dolu mu kontrol edilir
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Kayıt başarılı
                            Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Kayıt başarısız
                            Toast.makeText(this, "Kayıt başarısız: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Lütfen e-posta ve şifreyi doldurun.", Toast.LENGTH_SHORT).show()
            }
        }

        // Giriş yap butonuna tıklanınca kimlik doğrulama yapılır
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // E-posta ve şifre kontrol edilir
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Giriş başarılı, DashboardActivity'e geçiş yapılır
                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.putExtra("userEmail", email)
                            startActivity(intent)
                            finish() // Bu aktiviteyi kapat
                        } else {
                            // Giriş başarısız
                            Toast.makeText(this, "Giriş başarısız: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Lütfen e-posta ve şifreyi doldurun.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
