package com.example.final3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

// Post model verisi
data class Post(
    val name: String = "", // Ad
    val surname: String = "", // Soyad
    val birthPlace: String = "", // Doğum Yeri
    val birthDate: String = "", // Doğum Tarihi
    val deathDate: String = "", // Ölüm Tarihi
    val contributions: String = "", // Katkıları
    val imageUrl: String = "", // Görsel URL'si
    val user: String = "" // Gönderiyi ekleyen kullanıcı
)

// RecyclerView için adapter sınıfı
class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // ViewHolder sınıfı RecyclerView öğelerini bağlar
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPost)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewDetails: TextView = itemView.findViewById(R.id.textViewDetails)
        val textViewUser: TextView = itemView.findViewById(R.id.textViewUser)
    }

    // ViewHolder oluşturulur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    // ViewHolder verileri bağlar
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.textViewName.text = "${post.name} ${post.surname}" // Ad ve soyad
        holder.textViewDetails.text = "Doğum: ${post.birthDate}, Ölüm: ${post.deathDate}\nKatkıları: ${post.contributions}" // Detaylar
        holder.textViewUser.text = "Gönderen: ${post.user}" // Gönderen kullanıcı

        // Görsel Picasso ile yüklenir
        if (post.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(R.drawable.placeholder_image) // Yer tutucu resim
                .error(R.drawable.error_image) // Hata durumunda resim
                .into(holder.imageView)
        }
    }

    // Liste öğe sayısını döndürür
    override fun getItemCount(): Int = postList.size
}
