package com.example.stepcounterexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepcounterexample.service.startNotifyForegroundService
import com.example.stepcounterexample.service.stopNotifyForegroundService
import com.example.stepcounterexample.ui.theme.StepCounterExampleTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            var allPermissionGranted = true
            val permissionList: List<String> = listOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            val requestPermissionList: MutableList<String> = mutableListOf()
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
            ) { isGrantedMap: Map<String, Boolean> ->
                isGrantedMap.forEach{(_, value)->
                    if(!value){
                        Toast.makeText(
                            this,
                            "許可されていない権限が存在し、正常に動作しません。",
                            Toast.LENGTH_LONG
                        ).show()
                        allPermissionGranted = false
                    }
                }
                if(allPermissionGranted){
                    Toast.makeText(
                        this,
                        "すべての権限が許可されました。",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            permissionList.forEach { p ->
                if (ActivityCompat.checkSelfPermission(this, p)
                    != PackageManager.PERMISSION_GRANTED){
                    requestPermissionList.add(p)
                }
            }
            if(requestPermissionList.isNotEmpty()) {
                requestPermissionLauncher.launch(requestPermissionList.toTypedArray())
            }
        }

        setContent {
            StepCounterExampleTheme {
                val viewModel: StepCounterViewModel = viewModel()

                val currentLogUiState = viewModel.currentLogUiState.collectAsState()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("計測器の歩数")
                        if(currentLogUiState.value.isNotEmpty()){
                            Text(currentLogUiState.value[0].currentStepCounts.toString())
                        }else{
                            Text("0")
                        }
                        Row{
                            Column {
                                Text("今日の歩数")
                                if(currentLogUiState.value.isNotEmpty()){
                                    Text(currentLogUiState.value[0].dayTotalStepCounts.toString())
                                }else{
                                    Text("0")
                                }
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Column {
                                val currentHour = LocalDateTime.now().hour
                                Text("${currentHour}時からの歩数")
                                if(currentLogUiState.value.isNotEmpty()){
                                    Text(currentLogUiState.value[0].hourTotalStepCounts.toString())
                                }else{
                                    Text("0")
                                }
                            }
                        }
                        Row{
                            Button(
                                modifier = Modifier.width(155.dp),
                                onClick = { startNotifyForegroundService(context) }
                            ) {
                                Text("StartForegroundService")
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Button(
                                modifier = Modifier.width(155.dp),
                                onClick = { stopNotifyForegroundService(context) }
                            ) {
                                Text("StopForegroundService")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StepCounterExampleTheme {
        Greeting("Android")
    }
}