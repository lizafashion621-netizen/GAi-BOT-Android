# GAi BOT Android screen-scanner (starter build)

This repository contains an Android Studio project and GitHub Actions workflow to produce a debug APK. The original Chrome extension files are preserved under `app/src/main/assets/original_extension/` for reference.

## Build APK on GitHub
1. Create a GitHub repository and upload the contents of this folder.
2. Push to `main`/`master`, or open **Actions → Build GAi BOT Android APK → Run workflow**.
3. Open the completed workflow run and download the `GAi-BOT-Android-debug-APK` artifact.

## Important limitations
- This is a native Android starter port, not a byte-for-byte or behavior-identical conversion of the Chrome extension. Chrome extension APIs (`chrome.tabs`, `chrome.runtime`, storage and Binance page content scripts) do not run inside a normal Android APK; the original files are preserved for porting.
- The floating popup can appear above other apps after the user grants Android's overlay permission. Screen capture starts only after the user taps scan and approves Android's system capture dialog.
- The current visual classifier is a very basic screen-wide red/green pixel heuristic. It does **not** identify candle geometry reliably and does **not** reproduce the original eight-strategy market signal engine. Treat UP/DOWN as experimental visual estimates, not trading advice. A real faithful port needs chart-region detection and a native port of the strategy engine, then device testing.
- Screen capture shows Android's ongoing capture indicator and can be stopped by the user. Some apps can block capture.
- This is a debug APK build; it is not a Play Store release-signed package.
