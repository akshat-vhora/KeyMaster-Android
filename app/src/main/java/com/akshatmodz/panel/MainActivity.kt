package com.akshatmodz.panel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.akshatmodz.panel.data.SessionManager
import com.akshatmodz.panel.ui.app.PanelRoot
import com.akshatmodz.panel.ui.app.PanelRootMode
import com.akshatmodz.panel.ui.theme.PanelTheme

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(applicationContext)

        setContent {
            PanelTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PanelRoot(
                        sessionManager = sessionManager,
                        mode = PanelRootMode.Full
                    )
                }

            }
        }
    }
}
