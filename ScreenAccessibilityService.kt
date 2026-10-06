package com.example.geminiscreentrigger

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ScreenAccessibilityService: AccessibilityService() {
 private var lastDown=0L
 private var busy=false
 private val handler=Handler(Looper.getMainLooper())

 override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
 override fun onInterrupt() {}

 override fun onKeyEvent(event: KeyEvent): Boolean {
  if(event.action==KeyEvent.ACTION_DOWN && event.keyCode==KeyEvent.KEYCODE_VOLUME_DOWN && !event.isCanceled) {
   val now=System.currentTimeMillis()
   if(now-lastDown in 80..650) {
    lastDown=0
    capture()
    return true
   }
   lastDown=now
  }
  return false
 }

 private fun capture() {
  if(busy) return
  busy=true
  takeScreenshot(android.view.Display.DEFAULT_DISPLAY, mainExecutor, object: TakeScreenshotCallback {
   override fun onSuccess(r: ScreenshotResult) {
    try {
     val bmp=Bitmap.wrapHardwareBuffer(r.hardwareBuffer,r.colorSpace)
     r.hardwareBuffer.close()
     if(bmp!=null) launchGeminiAndUseScreenContext()
    } finally { busy=false }
   }
   override fun onFailure(code:Int) { busy=false }
  })
 }

 private fun launchGeminiAndUseScreenContext() {
  // Uses the installed Gemini app, not an API key.
  // Google controls the actual Gemini UI, so exact node labels can vary.
  val launch=packageManager.getLaunchIntentForPackage("com.google.android.apps.bard")
  if(launch==null) {
   busy=false
   return
  }
  launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
  startActivity(launch)

  // Give Gemini time to appear, then attempt to focus the prompt.
  handler.postDelayed({ tryFocusGeminiPrompt() }, 900)
 }

 private fun tryFocusGeminiPrompt() {
  val root=rootInActiveWindow ?: return
  val nodes=findEditable(root)
  if(nodes.isNotEmpty()) {
   val node=nodes.first()
   node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
  }
  // We deliberately do not inject a fake click/keystroke into arbitrary
  // third-party UI beyond focusing the visible input. Gemini's own screen
  // context remains responsible for the screenshot.
 }

 private fun findEditable(root:AccessibilityNodeInfo):List<AccessibilityNodeInfo> {
  val out=mutableListOf<AccessibilityNodeInfo>()
  fun walk(n:AccessibilityNodeInfo) {
   if(n.isEditable) out.add(n)
   for(i in 0 until n.childCount) n.getChild(i)?.let(::walk)
  }
  walk(root)
  return out
 }
}
