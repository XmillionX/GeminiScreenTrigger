package com.example.geminiscreentrigger

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity() {
 override fun onCreate(state: Bundle?) {
  super.onCreate(state)
  val box=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(40,40,40,40) }
  box.addView(TextView(this).apply {
   text="""Gemini Screen Trigger — No Menu

1. В Gemini включи «Использовать текст с экрана» и «Использовать скриншот».
2. Нажми кнопку ниже и включи службу.
3. Дважды быстро нажми Volume Down.

Приложение пытается открыть Gemini напрямую и передать ему снимок через Accessibility, без системного меню Share."""
   textSize=16f
  })
  box.addView(Button(this).apply {
   text="Открыть специальные возможности"
   setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
  })
  setContentView(box)
 }
}
