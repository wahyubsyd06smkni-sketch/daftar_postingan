package com.example.daftarpostingan

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daftarpostingan.data.Post
import com.example.daftarpostingan.network.RestClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: PostAdapter
    private lateinit var tvStatus: TextView
    private lateinit var progress: ProgressBar
    private lateinit var btnUlang: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        progress = findViewById(R.id.progress)
        btnUlang = findViewById(R.id.btnUlang)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerPost)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PostAdapter(
            daftar = mutableListOf(),
            onKlik = { post -> tampilkanDetail(post) },
            onHapus = { posisi -> konfirmasiHapus(posisi) }
        )
        recyclerView.adapter = adapter

        btnUlang.setOnClickListener { muatPosts() }
        muatPosts()
    }

    private fun muatPosts() {
        progress.visibility = View.VISIBLE
        btnUlang.visibility = View.GONE
        tvStatus.text = "Memuat postingan..."

        lifecycleScope.launch {
            try {
                val hasil = RestClient.api.getPosts(limit = 20)
                adapter.tampilkan(hasil)
                tvStatus.text = "${hasil.size} postingan dimuat"
            } catch (e: HttpException) {
                tvStatus.text = "Server error: HTTP ${e.code()}"
                btnUlang.visibility = View.VISIBLE
            } catch (e: IOException) {
                tvStatus.text = "Tidak ada koneksi internet"
                btnUlang.visibility = View.VISIBLE
            } finally {
                progress.visibility = View.GONE
            }
        }
    }

    private fun tampilkanDetail(post: Post) {
        AlertDialog.Builder(this)
            .setTitle(post.title)
            .setMessage(post.body)
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun konfirmasiHapus(posisi: Int) {
        val post = adapter.ambil(posisi)
        AlertDialog.Builder(this)
            .setTitle("Hapus postingan?")
            .setMessage("Postingan #${post.id} akan dihapus.")
            .setPositiveButton("Hapus") { _, _ -> hapusPost(post) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusPost(post: Post) {
        lifecycleScope.launch {
            try {
                val respons = RestClient.api.hapusPost(post.id)
                if (respons.isSuccessful) {
                    val posisi = adapter.posisiDari(post)
                    if (posisi != -1) adapter.hapusPada(posisi)
                    Toast.makeText(this@MainActivity, "Postingan #${post.id} dihapus", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Gagal menghapus: HTTP ${respons.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
        }
    }
}