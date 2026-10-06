# Gemini Screen Trigger — No Menu

Android 14 / Gemini account version.

Flow:
Volume Down x2 -> screenshot -> launch installed Gemini -> attempt to focus Gemini input.

This build does NOT use the Gemini API and does not require an API key.
It intentionally avoids Android's Share chooser.

Important limitations:
- There is no official public API for a third-party app to silently control the consumer Gemini Android app.
- Gemini package/UI can change. The Accessibility automation may therefore need adjustment for a particular Gemini version.
- The screenshot itself is obtained using Android AccessibilityService.
- Protected FLAG_SECURE windows cannot be captured.
- Android may show system indicators/permissions related to Accessibility/screen capture.
- This project does not attempt to bypass Android security prompts.

Build with GitHub Actions using the included workflow.
