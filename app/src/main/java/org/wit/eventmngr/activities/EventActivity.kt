package org.wit.eventmngr.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import org.wit.eventmngr.R
import org.wit.eventmngr.databinding.ActivityEventBinding
import org.wit.eventmngr.helpers.showImagePicker
import org.wit.eventmngr.main.MainApp
import org.wit.eventmngr.models.Location
import org.wit.eventmngr.models.EventModel
import timber.log.Timber
import timber.log.Timber.i

class EventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventBinding
    var event = EventModel()
    lateinit var app: MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    //var location = Location(52.245696, -7.139102, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var edit = false

        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        i("Event Manager started...")

        if (intent.hasExtra("event_edit")) {
            edit = true
            event = intent.extras?.getParcelable("event_edit")!!
            binding.eventTitle.setText(event.title)
            binding.description.setText(event.description)
            binding.btnAdd.setText(R.string.save_event)
            Picasso.get()
                .load(event.image)
                .into(binding.eventImage)
            if (event.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_event_image)
            }
        }

        binding.btnAdd.setOnClickListener() {
            event.title = binding.eventTitle.text.toString()
            event.description = binding.description.text.toString()
            if (event.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_event_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.events.update(event.copy())
                } else {
                    app.events.create(event.copy())
                }
            }
            i("add Button Pressed: $event")
            setResult(RESULT_OK)
            finish()
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        binding.eventLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (event.zoom != 0f) {
                location.lat =  event.lat
                location.lng = event.lng
                location.zoom = event.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        registerImagePickerCallback()
        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_event, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            event.image = result.data!!.data!!
                            Picasso.get()
                                .load(event.image)
                                .into(binding.eventImage)
                            binding.chooseImage.setText(R.string.change_event_image)
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            event.lat = location.lat
                            event.lng = location.lng
                            event.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}