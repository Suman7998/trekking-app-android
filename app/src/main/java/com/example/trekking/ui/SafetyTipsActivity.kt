package com.example.trekking.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trekking.R
import com.google.android.material.appbar.MaterialToolbar

class SafetyTipsActivity : AppCompatActivity() {

    // A list to hold all the views for each expandable section
    private val sections = mutableListOf<ExpandableSection>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_tips)

        setupToolbar()
        setupExpandableSections()
    }

    private fun setupToolbar() {
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_safety_tips)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Finds all the views for each section and sets up the click listeners.
     */
    private fun setupExpandableSections() {
        sections.add(
            ExpandableSection(
                clickableArea = findViewById(R.id.btnPersonal),
                indicator = findViewById(R.id.indicatorPersonal),
                content = findViewById(R.id.tvPersonalContent)
            )
        )
        sections.add(
            ExpandableSection(
                clickableArea = findViewById(R.id.btnOnline),
                indicator = findViewById(R.id.indicatorOnline),
                content = findViewById(R.id.tvOnlineContent)
            )
        )
        sections.add(
            ExpandableSection(
                clickableArea = findViewById(R.id.btnTravel),
                indicator = findViewById(R.id.indicatorTravel),
                content = findViewById(R.id.tvTravelContent)
            )
        )
        sections.add(
            ExpandableSection(
                clickableArea = findViewById(R.id.btnTrekking),
                indicator = findViewById(R.id.indicatorTrekking),
                content = findViewById(R.id.tvTrekkingContent)
            )
        )

        // Set an OnClickListener for each section's clickable area
        sections.forEach { section ->
            section.clickableArea.setOnClickListener {
                toggleSection(section)
            }
        }
    }

    /**
     * Handles the logic for expanding or collapsing a section.
     * It also ensures only one section is open at a time.
     */
    private fun toggleSection(clickedSection: ExpandableSection) {
        val isCurrentlyOpen = clickedSection.content.visibility == View.VISIBLE

        // First, close all sections
        sections.forEach { section ->
            close(section)
        }

        // If the clicked section was not already open, open it
        if (!isCurrentlyOpen) {
            open(clickedSection)
        }
    }

    /**
     * Makes the content visible and changes the indicator text to 'v' (down).
     */
    private fun open(section: ExpandableSection) {
        section.content.visibility = View.VISIBLE
        section.indicator.text = "v"
    }

    /**
     * Hides the content and resets the indicator text to '>' (right).
     */
    private fun close(section: ExpandableSection) {
        section.content.visibility = View.GONE
        section.indicator.text = ">"
    }

    /**
     * Handles the click of the back arrow in the toolbar to finish the activity.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

/**
 * A helper data class to group the views for an expandable section.
 * The indicator is now a TextView instead of an ImageView.
 */
data class ExpandableSection(
    val clickableArea: LinearLayout,
    val indicator: TextView,
    val content: TextView
)