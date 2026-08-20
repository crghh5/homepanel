package com.example.homepanel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) { PanelScreen() }
        }
    }
}

data class PanelTile(val emoji: String, val title: String, val subtitle: String, val pkg: String)

@Composable
fun PanelScreen() {
    val context = LocalContext.current

    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) { now = Date(); delay(10_000) }
    }
    val timeText = remember(now) { SimpleDateFormat("HH:mm", Locale.getDefault()).format(now) }
    val dateText = remember(now) { SimpleDateFormat("EEEE, d MMMM", Locale("ru")).format(now) }

    val tiles = listOf(
        PanelTile("🏠", "Умный дом", "Smart Life", "com.tuya.smartlife"),
        PanelTile("📹", "Камеры", "SuperLive Plus", "com.tvt.superliveplus")
    )

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(timeText, fontSize = 72.sp, fontWeight = FontWeight.Light)
        Text(dateText.replaceFirstChar { it.uppercase() }, fontSize = 22.sp,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))

        LazyVerticalGrid(
            GridCells.Adaptive(240.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tiles) { tile ->
                ElevatedCard(
                    onClick = { openApp(context, tile.pkg) },
                    modifier = Modifier.height(160.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(tile.emoji, fontSize = 44.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(tile.title, fontSize = 22.sp, fontWeight = FontWeight.SemiBold,
                             textAlign = TextAlign.Center)
                        Text(tile.subtitle, fontSize = 14.sp,
                             color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

fun openApp(context: Context, pkg: String) {
    val pm = context.packageManager
    val candidates = if (pkg == "com.tuya.smartlife")
        listOf("com.tuya.smartlife", "com.tuya.smart")
    else listOf(pkg)

    val intent = candidates.firstNotNullOfOrNull { pm.getLaunchIntentForPackage(it) }
    if (intent != null) {
        context.startActivity(intent)
    } else {
        context.startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$pkg")))
    }
}
