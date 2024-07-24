package com.example.guridownload

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guridownload.downloadutils.DownloadFile
import com.example.guridownload.downloadutils.Downloader
import com.example.guridownload.notification.NotificationUtils
import com.example.guridownload.ui.theme.GuriDownloadTheme
import com.example.guridownload.workmanager.DownloadWorker
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuriDownloadTheme {
                var buttonText by remember {
                    mutableStateOf(DOWNLOAD_TYPE.NONE)
                }
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DownloadButton("Download a Mock PDF", onClick = {
                        buttonText = DOWNLOAD_TYPE.MOCK_PDF
                        Downloader(this@MainActivity).downloadFile("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                    })
                    DownloadButton("Download a Mock PDF Using File Picker", onClick = {
                        buttonText = DOWNLOAD_TYPE.FILE_PICKER_PDF
                        launchFilePicker()
                    })
                    DownloadButton("Download a Mock PDF in 30 seconds", onClick = {
                        buttonText = DOWNLOAD_TYPE.WORK_MANAGER_PDF
                        DownloadWorker.scheduleWork(
                            this@MainActivity,
                            "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
                        )
                    })
                    Spacer(modifier = Modifier.size(20.dp))
                    if (buttonText != DOWNLOAD_TYPE.NONE) {
                        InfoText(text = getInfoText(buttonText))
                    }
                }
            }
        }
    }

    private fun launchFilePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_TITLE, Date())
        filePickerLauncher.launch(intent)
    }

    private var filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val resultCode = it.resultCode
            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri? = it.data?.data
                uri?.let { destinationUri ->
                    runBlocking {
                        DownloadFile.saveFile(
                            this@MainActivity,
                            URL("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"),
                            destinationUri
                        )
                    }
                    val notificationManager =
                        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(
                        5741,
                        NotificationUtils.getDownloadCompleteNotification(
                            this@MainActivity,
                            destinationUri
                        )
                    )
                }
            }
        }

    @Composable
    private fun getInfoText(buttonText: DOWNLOAD_TYPE): String {
        return when (buttonText) {
            DOWNLOAD_TYPE.MOCK_PDF -> "A MOCK Pdf will be downloaded using download manager. Download Manager doesnot require App Permission above Android 9. For Devices in Android 9 this willcrash the app."
            DOWNLOAD_TYPE.FILE_PICKER_PDF -> "A file picker is opened. Download Manager doesnot require App Permission above Android 9. For Devices in Android 9 this will crash the app."
            DOWNLOAD_TYPE.WORK_MANAGER_PDF -> "A MOCK Pdf will be downloaded after 30 seconds. We are using work manager here with one time request of initial delay of 30 seconds."
            else -> ""
        }
    }
}

@Composable
fun InfoText(text: String) {
    Text(
        text,
        modifier = Modifier
            .border(width = 2.dp, Color.Blue, shape = RoundedCornerShape(10))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    )
}

@Composable
fun DownloadButton(buttonText: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            onClick = onClick,
            content = {
                Text(text = buttonText)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GuriDownloadTheme {
        DownloadButton("Download PDF", { })
    }
}

enum class DOWNLOAD_TYPE {
    MOCK_PDF, WORK_MANAGER_PDF, FILE_PICKER_PDF, NONE
}