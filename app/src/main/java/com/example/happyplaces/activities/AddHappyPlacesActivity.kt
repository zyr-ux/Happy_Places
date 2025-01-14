package com.example.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityAddHappyPlacesBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.example.happyplaces.database.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.launch
import java.lang.Exception


class AddHappyPlacesActivity : AppCompatActivity(), View.OnClickListener
{
    private var binding:ActivityAddHappyPlacesBinding?=null
    private lateinit var imageUri: Uri
    private var fullSiedImageUri:Uri?=null
    private var longitude:Double=0.0
    private var latittude:Double=0.0
    private lateinit var happyPlacesdao:HappyPlacesDao
    private var id:Int?=null
    private var editmode:Boolean?=false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddHappyPlacesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.root?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
        }
        actionBar()
        defaultView()
        happyPlacesdao = (application as HappyPlacesApp).db.happyPlacesDao()
        binding?.dateEt?.setOnClickListener(this)
        binding?.addImg?.setOnClickListener(this)
        binding?.locationEt?.setOnClickListener(this)
        if (intent.hasExtra("ID")) {
            editmode=true
            id = intent.getIntExtra("ID", 0)
            Log.e("edit ID","$id")
            lifecycleScope.launch {
                happyPlacesdao.fetchHappyPlacebyID(id!!).collect {
                    Log.e("E","$it")
                    onEdit(id!!,it,happyPlacesdao)
                }
            }
        }
        if(editmode==false){
            binding?.saveBtn?.setOnClickListener {
                addHappyPlace(happyPlacesdao)
                finish()
            }
        }
        if(!Places.isInitialized()){
            Places.initialize(this@AddHappyPlacesActivity,resources.getString(R.string.google_maps_api_key))
        }
    }

    private fun onEdit(id:Int,happyPlaceEntity: HappyPlaceEntity,happyPlacesDao: HappyPlacesDao)
    {
        binding?.actionBarAddPlace?.setTitle("Edit Happy Place")
        binding?.titleTil?.isHintAnimationEnabled=false
        binding?.descTil?.isHintAnimationEnabled=false
        binding?.dateTil?.isHintAnimationEnabled=false
        binding?.locationTil?.isHintAnimationEnabled=false
        binding?.titleEt?.setText(happyPlaceEntity.title)
        binding?.descEt?.setText(happyPlaceEntity.description)
        binding?.dateEt?.setText(happyPlaceEntity.date)
        binding?.locationEt?.setText(happyPlaceEntity.location)
        binding?.addedimgbox?.setImageURI(Uri.parse(happyPlaceEntity.img))
        binding?.titleTil?.isHintAnimationEnabled=true
        binding?.descTil?.isHintAnimationEnabled=true
        binding?.dateTil?.isHintAnimationEnabled=true
        binding?.locationTil?.isHintAnimationEnabled=true
        binding?.saveBtn?.setText("Update")

        binding?.saveBtn?.setOnClickListener{
            val newtitle = binding?.titleEt?.text.toString()
            val newdesc=binding?.descEt?.text.toString()
            val newdate=binding?.dateEt?.text.toString()
            val newlocation=binding?.locationEt?.text.toString()
            val newmLatitude=latittude
            val newmLongitude=longitude
            var newimg:String?=null
            Log.e("New","title = $newtitle")
            if(fullSiedImageUri==null){
                newimg=happyPlaceEntity.img
            }
            else{
                newimg=fullSiedImageUri.toString()
            }
            if (newtitle.isNotEmpty() && newdesc.isNotEmpty() && newdate.isNotEmpty() && newlocation.isNotEmpty() && newimg.isNotEmpty()){
                lifecycleScope.launch {
                    happyPlacesDao.update(HappyPlaceEntity(id,newtitle,newimg,newdesc,newdate,newlocation,newmLatitude,newmLongitude))
                    Toast.makeText(this@AddHappyPlacesActivity,"Record Updated",Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }
    }

    private fun defaultView()
    {
        binding?.actionBarAddPlace?.setTitle("Add Happy Place")
        binding?.titleEt?.setText("")
        binding?.descEt?.setText("")
        binding?.dateEt?.setText("")
        binding?.locationEt?.setText("")
        binding?.addedimgbox?.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.baseline_image_24))
        binding?.saveBtn?.setText("SAVE")
    }

    private fun actionBar()
    {
        val statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        binding?.actionBarAddPlace?.setPadding(0, statusBarHeight, 0, 0)
        setSupportActionBar(binding?.actionBarAddPlace)
        if(supportActionBar!=null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.actionBarAddPlace?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun datePicker()
    {
        val calendarconstraints = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select the date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(calendarconstraints.build())
            .build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = selection
            val sdf=SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            val date= Date(selectedDate)
            val formattedDate=sdf.format(date)
            binding?.dateEt?.setText(formattedDate)
        }
    }

    override fun onClick(v: View?)
    {
        when(v!!.id){
            R.id.date_et ->{
                datePicker()
            }
            R.id.add_img ->{
                val pictureAlertDialogue=AlertDialog.Builder(this)
                pictureAlertDialogue.setTitle("Select image")
                val dialogueOptionArray= arrayOf("Select photo from your gallery","Capture image from camera")
                pictureAlertDialogue.setItems(dialogueOptionArray){
                    dialogue,which ->
                    when(which){
                        1-> addImgfromCamera()
                        0-> addImagefromGallery()
                    }
                }
                pictureAlertDialogue.show()
            }
            R.id.location_et->{
                try {
                    val fields= listOf(Place.Field.ID,Place.Field.DISPLAY_NAME,Place.Field.LOCATION,Place.Field.FORMATTED_ADDRESS)
                    val intent=Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields).build(this@AddHappyPlacesActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun addImagefromGallery()
    {
        if(Build.VERSION.SDK_INT>=33)
        {
            Dexter.withContext(this).withPermissions(Manifest.permission.READ_MEDIA_IMAGES)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val galleryIntent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                            startActivityForResult(galleryIntent, GALLERY)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        showRationalDialogueForPermissions()
                    }
                }).onSameThread().check()
        }
       else
       {
            Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val galleryIntent =
                                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, GALLERY)
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                        showRationalDialogueForPermissions()
                    }
                }).onSameThread().check()
       }
    }

    private fun addImgfromCamera()
    {
        Dexter.withContext(this).withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val imageFile = createImageFile()
                        imageUri = FileProvider.getUriForFile(this@AddHappyPlacesActivity, "com.example.happyplaces.fileprovider", imageFile)

                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(cameraIntent, CAMERA)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken) {
                    showRationalDialogueForPermissions()
                }
            }).onSameThread().check()
    }

    private fun showRationalDialogueForPermissions()
    {
        AlertDialog.Builder(this).setMessage("Looks like you have not granted the required permissions. " +
                "Please grant those").setPositiveButton("Go to settings") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){dialog,_->
            dialog.dismiss()
        }.show()
    }


    private fun saveImagetoInternalStorage(bitmap: Bitmap):Uri
    {
        val wrapper=ContextWrapper(applicationContext)
        var file=wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file=File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream:OutputStream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    private fun createImageFile(): File
    {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${System.currentTimeMillis()}_", ".jpg", storageDir)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if (requestCode== GALLERY){
                if(data!=null){
                    val contentURI=data.data
                    try {
                        val selectedImgBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                        fullSiedImageUri=saveImagetoInternalStorage(selectedImgBitmap)
                        Log.e("Saved Img :","Path : $fullSiedImageUri")
                        binding?.addedimgbox?.setImageBitmap(selectedImgBitmap)
                    }catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(this,"Image selection failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if(requestCode== CAMERA)
            {
//                val thumbnail:Bitmap=data!!.extras!!.get("data") as Bitmap
//                val saveImgaetoInternalStorage=saveImagetoInternalStorage(thumbnail)
//                Log.e("Saved Img :","Path : $saveImgaetoInternalStorage")
//                val scaledBitmap = Bitmap.createScaledBitmap(thumbnail, 2000, 2000, true)
//                binding?.addedimgbox?.setImageBitmap(scaledBitmap)
                try {
                    val fullSizeBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    fullSiedImageUri=saveImagetoInternalStorage(fullSizeBitmap)
                    Log.e("Saved Img :","Path : $fullSiedImageUri")
                    binding?.addedimgbox?.setImageBitmap(fullSizeBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
                }
            }
            else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
                val place:Place=Autocomplete.getPlaceFromIntent(data!!)
                binding?.locationEt?.setText(place.formattedAddress)
                latittude=place.location.latitude
                longitude= place.location.longitude
            }
        }
    }

    fun addHappyPlace(happyPlacesDao: HappyPlacesDao)
    {
        val title = binding?.titleEt?.text.toString()
        val desc=binding?.descEt?.text.toString()
        val date=binding?.dateEt?.text.toString()
        val location=binding?.locationEt?.text.toString()
        val img=fullSiedImageUri.toString()
        val mLatitude=latittude
        val mLongitude=longitude

        if (title.isNotEmpty() && desc.isNotEmpty() && date.isNotEmpty() && location.isNotEmpty() && img.isNotEmpty())
        {
            lifecycleScope.launch{
                happyPlacesDao.insert(HappyPlaceEntity(title=title, description = desc, date = date, location = location, img = img,
                    latitude = mLatitude, longitude = mLongitude))
                Toast.makeText(applicationContext,"Record inserted",Toast.LENGTH_SHORT).show()
                binding?.titleEt?.text?.clear()
                binding?.descEt?.text?.clear()
                binding?.locationEt?.text?.clear()
                binding?.dateEt?.text?.clear()
                binding?.addedimgbox?.setImageBitmap(null)
            }
        }
        else
            Toast.makeText(applicationContext,"Fill out the details",Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding=null
    }

    companion object
    {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE=3
    }
    
}