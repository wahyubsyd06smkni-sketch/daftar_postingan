package com.example.daftarpostingan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daftarpostingan.data.Post

class PostAdapter(
    private val daftar: MutableList<Post>,
    private val onKlik: (Post) -> Unit,
    private val onHapus: (Int) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvIsi: TextView = itemView.findViewById(R.id.tvIsi)
        val tvInfo: TextView = itemView.findViewById(R.id.tvInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = daftar[position]
        holder.tvJudul.text = post.title
        holder.tvIsi.text = post.body
        holder.tvInfo.text = "Post #${post.id} - User ${post.userId}"

        holder.itemView.setOnClickListener { onKlik(post) }
        holder.itemView.setOnLongClickListener {
            onHapus(holder.bindingAdapterPosition)
            true
        }
    }

    override fun getItemCount(): Int = daftar.size

    fun tampilkan(baru: List<Post>) {
        daftar.clear()
        daftar.addAll(baru)
        notifyDataSetChanged()
    }

    fun ambil(posisi: Int): Post = daftar[posisi]

    fun posisiDari(post: Post): Int = daftar.indexOf(post)

    fun hapusPada(posisi: Int) {
        daftar.removeAt(posisi)
        notifyItemRemoved(posisi)
    }
}