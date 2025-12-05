package com.example.trekking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trekking.R
import com.example.trekking.data.AppDatabase
import com.example.trekking.data.PlaceDao
import com.example.trekking.data.PlaceEntity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DataViewerActivity : AppCompatActivity() {
    private lateinit var dao: PlaceDao
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: PlacesAdapter
    private var currentCategory: String = "TREKKING"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_viewer)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_data_viewer)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dao = AppDatabase.getInstance(this).placeDao()

        // Pass the onDelete listener to the adapter's constructor
        adapter = PlacesAdapter { place ->
            showDeleteConfirmationDialog(place)
        }

        recycler = findViewById(R.id.recyclerPlaces)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Event Listeners
        findViewById<ChipGroup>(R.id.chipGroup_categories).setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds.first()) {
                    R.id.chip_trekking -> loadCategory("TREKKING")
                    R.id.chip_parks -> loadCategory("PARK")
                    R.id.chip_waterfalls -> loadCategory("WATERFALL")
                }
            }
        }
        findViewById<MaterialButton>(R.id.btnSeed100).setOnClickListener { seed100() }
        findViewById<MaterialButton>(R.id.btnLoad).setOnClickListener { loadCategory(currentCategory) }
        findViewById<MaterialButton>(R.id.btnPushCloud).setOnClickListener { pushToCloud() }
        findViewById<MaterialButton>(R.id.btnPullCloud).setOnClickListener { pullFromCloud() }

        // Initial Load
        lifecycleScope.launch {
            ensurePrepopulated()
            findViewById<Chip>(R.id.chip_trekking).isChecked = true
            loadCategory("TREKKING")
        }
    }

    private fun showDeleteConfirmationDialog(place: PlaceEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Place")
            .setMessage("Are you sure you want to delete '${place.name}'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deletePlace(place)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePlace(place: PlaceEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                dao.delete(place)
            }
            loadCategory(currentCategory)
            Toast.makeText(this@DataViewerActivity, "'${place.name}' deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCategory(cat: String) {
        currentCategory = cat
        lifecycleScope.launch(Dispatchers.IO) {
            val data = dao.getByCategory(cat)
            withContext(Dispatchers.Main) { adapter.submit(data) }
        }
    }

    private fun pushToCloud() {
        if (!firestoreAvailable()) {
            Toast.makeText(this, "Cloud not configured.", Toast.LENGTH_LONG).show()
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val allPlaces = dao.getAll()
            val db = Firebase.firestore
            try {
                val batch = db.batch()
                val col = db.collection("places")
                allPlaces.forEach { place ->
                    val docId = "${place.category}_${place.name.replace(" ", "_")}"
                    val docRef = col.document(docId)
                    batch.set(docRef, mapOf(
                        "name" to place.name,
                        "location" to place.location,
                        "category" to place.category
                    ))
                }
                batch.commit().await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DataViewerActivity, "Pushed ${allPlaces.size} items to cloud", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DataViewerActivity, "Push failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun pullFromCloud() {
        if (!firestoreAvailable()) {
            Toast.makeText(this, "Cloud not configured.", Toast.LENGTH_LONG).show()
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val db = Firebase.firestore
            try {
                val snap = db.collection("places").get().await()
                val cloudList = snap.documents.mapNotNull { d ->
                    val name = d.getString("name")
                    val location = d.getString("location")
                    val category = d.getString("category")
                    if (name != null && location != null && category != null) {
                        PlaceEntity(name = name, location = location, category = category)
                    } else null
                }
                dao.clearAndInsert(cloudList)
                val data = dao.getByCategory(currentCategory)
                withContext(Dispatchers.Main) {
                    adapter.submit(data)
                    Toast.makeText(this@DataViewerActivity, "Pulled ${cloudList.size} items from cloud", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DataViewerActivity, "Pull failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun firestoreAvailable(): Boolean = try {
        FirebaseApp.getApps(this).isNotEmpty()
    } catch (e: Exception) { false }

    private suspend fun ensurePrepopulated() {
        if (dao.count() == 0) {
            withContext(Dispatchers.IO) {
                seedDatabase()
            }
        }
    }

    private fun seed100() {
        lifecycleScope.launch(Dispatchers.IO) {
            seedDatabase()
            val data = dao.getByCategory(currentCategory)
            withContext(Dispatchers.Main) {
                adapter.submit(data)
                Toast.makeText(this@DataViewerActivity, "Seeded 300 items", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun seedDatabase() {
        val items = mutableListOf<PlaceEntity>()
        for (i in 1..100) items.add(PlaceEntity(name = "Trek Spot $i", location = "Hills Region $i", category = "TREKKING"))
        for (i in 1..100) items.add(PlaceEntity(name = "Park $i", location = "City Zone $i", category = "PARK"))
        for (i in 1..100) items.add(PlaceEntity(name = "Waterfall Mountain $i", location = "Valley Area $i", category = "WATERFALL"))
        dao.insertAll(items)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

class PlacesAdapter(
    private val onLongClickListener: (PlaceEntity) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlaceVH>() {

    private val items = mutableListOf<PlaceEntity>()

    fun submit(data: List<PlaceEntity>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceVH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlaceVH, position: Int) {
        val currentPlace = items[position]
        holder.bind(currentPlace)
        holder.itemView.setOnLongClickListener {
            onLongClickListener.invoke(currentPlace)
            true
        }
    }

    class PlaceVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTv: TextView = itemView.findViewById(R.id.tvPlaceName)
        private val locTv: TextView = itemView.findViewById(R.id.tvPlaceLocation)
        fun bind(p: PlaceEntity) {
            nameTv.text = p.name
            locTv.text = p.location
        }
    }
}