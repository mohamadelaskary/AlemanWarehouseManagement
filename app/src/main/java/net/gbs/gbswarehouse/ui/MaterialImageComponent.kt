package net.gbs.gbswarehouse.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.data.remote.api.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

data class MaterialImageUiModel(
    val materialImageId: Int,
    val bitmap: Bitmap?,
    val createdAt: String?,
    val imagePath: String?
)

@Composable
fun MaterialImageButton(materialCode: String, accent: Color = ORANGE) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var imageItems by remember { mutableStateOf<List<MaterialImageUiModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var materialDescription by remember { mutableStateOf<String?>("") }

    val apiService = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            MaterialApiEntryPoint::class.java
        ).apiService()
    }

    val fetchImages: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                isLoading = true
                val response = apiService.getMaterialImages(materialCode)
                if (response.isSuccessful && response.body() != null) {
                    val bodyData = response.body()!!.data
                    if (bodyData != null) {
                        materialDescription = bodyData.materialDescription
                        val fetchedItems = mutableListOf<MaterialImageUiModel>()
                        for (imgDto in bodyData.images) {
                            var bmp: Bitmap? = null
                            try {
                                val fileResp = apiService.getMaterialImageFile(imgDto.materialImageId)
                                if (fileResp.isSuccessful && fileResp.body() != null) {
                                    val bytes = fileResp.body()!!.bytes()
                                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                }
                            } catch (_: Exception) {}
                            fetchedItems.add(
                                MaterialImageUiModel(
                                    materialImageId = imgDto.materialImageId,
                                    bitmap = bmp,
                                    createdAt = imgDto.createdAt,
                                    imagePath = imgDto.imagePath
                                )
                            )
                        }
                        imageItems = fetchedItems
                    } else {
                        imageItems = emptyList()
                    }
                } else {
                    imageItems = emptyList()
                }
            } catch (_: Exception) {
                imageItems = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap ->
        if (capturedBitmap != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    isLoading = true
                    val stream = ByteArrayOutputStream()
                    capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                    val byteArray = stream.toByteArray()
                    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData("Image", "material_$materialCode.jpeg", requestBody)
                    val codeBody = materialCode.toRequestBody("text/plain".toMediaTypeOrNull())

                    val response = apiService.uploadMaterialImage(codeBody, imagePart)
                    if (response.isSuccessful) {
                        fetchImages()
                    }
                } catch (_: Exception) {
                } finally {
                    isLoading = false
                }
            }
        }
    }

    IconButton(onClick = {
        showDialog = true
        fetchImages()
    }) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Material Image",
            tint = accent
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("×", color = accent)
                }
            },
            title = {
                Column {
                    Text(stringResource(R.string.material_image, materialCode), fontSize = 14.sp)
                    if (!materialDescription.isNullOrBlank()) {
                        Text(materialDescription!!, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = accent)
                    } else if (imageItems.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(imageItems, key = { it.materialImageId }) { item ->
                                    Card(
                                        modifier = Modifier
                                            .width(200.dp)
                                            .fillMaxHeight(),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            if (item.bitmap != null) {
                                                Image(
                                                    bitmap = item.bitmap.asImageBitmap(),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(stringResource(R.string.image_unavailable), fontSize = 12.sp, color = Color.Gray)
                                                }
                                            }
                                            // Delete icon button overlay
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch(Dispatchers.IO) {
                                                        try {
                                                            isLoading = true
                                                            val delResp = apiService.deleteMaterialImage(item.materialImageId)
                                                            if (delResp.isSuccessful) {
                                                                fetchImages()
                                                            }
                                                        } catch (_: Exception) {
                                                        } finally {
                                                            isLoading = false
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.5f))
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Image",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = accent)
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.add_another_image), color = Color.White)
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.image_unavailable), color = Color.Gray, fontSize = 12.sp)
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = accent)
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.add_image), color = Color.White)
                            }
                        }
                    }
                }
            }
        )
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MaterialApiEntryPoint {
    fun apiService(): ApiService
}
