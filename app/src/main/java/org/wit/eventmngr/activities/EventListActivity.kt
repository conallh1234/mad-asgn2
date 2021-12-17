package org.wit.eventmngr.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.eventmngr.R
import org.wit.eventmngr.adapters.EventListener
import org.wit.eventmngr.adapters.EventAdapter
import org.wit.eventmngr.databinding.ActivityEventListBinding
import org.wit.eventmngr.main.MainApp
import org.wit.eventmngr.models.EventModel

class EventListActivity : AppCompatActivity(), EventListener/*, MultiplePermissionsListener*/ {

    lateinit var app: MainApp
    private lateinit var binding: ActivityEventListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        loadEvents()
        registerRefreshCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, EventActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onEventClick(event: EventModel) {
        val launcherIntent = Intent(this, EventActivity::class.java)
        launcherIntent.putExtra("event_edit", event)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadEvents() }
    }

    private fun loadEvents() {
        showEvents(app.events.findAll())
    }

    fun showEvents (events: List<EventModel>) {
        binding.recyclerView.adapter = EventAdapter(events, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

}