package com.example.trekking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trekking.R
import com.google.android.material.appbar.MaterialToolbar

class MLInsightsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ml_insights)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_ml_insights)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recycler = findViewById<RecyclerView>(R.id.recyclerInsights)
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = InsightsAdapter(staticInsights())
        recycler.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }

    private fun staticInsights(): List<Insight> = listOf(
        Insight("Kedarkantha", "Uttarakhand", "Trek", 5f, "Snow trek with panoramic peaks"),
        Insight("Hampta Pass", "Himachal Pradesh", "Trek", 4.5f, "Crossover trek with dramatic valley views"),
        Insight("Valley of Flowers", "Uttarakhand", "Trek", 5f, "UNESCO site with alpine meadows"),
        Insight("Sandakphu", "West Bengal", "Trek", 4.0f, "Kanchenjunga & Everest views"),
        Insight("Jog Falls", "Karnataka", "Waterfall", 4.5f, "One of the highest waterfalls in India"),
        Insight("Dudhsagar Falls", "Goa", "Waterfall", 5f, "Iconic milky cascade"),
        Insight("Athirapally Falls", "Kerala", "Waterfall", 4.0f, "Niagara of India"),
        Insight("Triund", "Himachal Pradesh", "Trek", 4.5f, "Short trek with Dharamsala vistas"),
        Insight("Har Ki Dun", "Uttarakhand", "Trek", 4.0f, "Valley of gods with ancient villages"),
        Insight("Nohkalikai Falls", "Meghalaya", "Waterfall", 5f, "Spectacular plunge pool")
    )
}

data class Insight(
    val name: String,
    val location: String,
    val type: String,
    val rating: Float,
    val details: String
)

class InsightsAdapter(private val data: List<Insight>) : RecyclerView.Adapter<InsightsAdapter.InsightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsightViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_insight, parent, false)
        return InsightViewHolder(v)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: InsightViewHolder, position: Int) = holder.bind(data[position])

    class InsightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tvName)
        private val loc: TextView = itemView.findViewById(R.id.tvLocation)
        private val type: TextView = itemView.findViewById(R.id.tvType)
        private val details: TextView = itemView.findViewById(R.id.tvDetails)
        private val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val icon: ImageView = itemView.findViewById(R.id.ivTypeIcon)

        fun bind(i: Insight) {
            name.text = i.name
            loc.text = i.location
            type.text = i.type
            details.text = i.details
            rating.rating = i.rating

            // Set the correct icon based on the type
            when (i.type.lowercase()) {
                "trek" -> icon.setImageResource(R.drawable.ic_terrain)
                "waterfall" -> icon.setImageResource(R.drawable.ic_waterfall)
                else -> icon.setImageResource(R.drawable.ic_default_pin) // A fallback icon
            }
        }
    }
}