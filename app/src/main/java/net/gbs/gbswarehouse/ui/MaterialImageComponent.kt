package net.gbs.gbswarehouse.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun MaterialImageButton(materialCode: String, accent: Color = ORANGE) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var hasImage by remember { mutableStateOf<Boolean?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val apiService = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            MaterialApiEntryPoint::class.java
        ).apiService()
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
                        bitmap = capturedBitmap
                        hasImage = true
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
        coroutineScope.launch(Dispatchers.IO) {
            try {
                isLoading = true
                val response = apiService.getMaterialImage(materialCode)
                if (response.isSuccessful && response.body() != null) {
                    val bytes = response.body()!!.bytes()
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bmp != null) {
                        bitmap = bmp
                        hasImage = true
                    } else {
                        hasImage = false
                    }
                } else {
                    hasImage = false
                }
            } catch (_: Exception) {
                hasImage = false
            } finally {
                isLoading = false
            }
        }
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


            title = { Text(stringResource(R.string.material_image, materialCode), fontSize = 14.sp) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = accent)
                    } else if (hasImage == true && bitmap != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                bitmap = bitmap!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = accent)
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.change_image), color = Color.White)
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
