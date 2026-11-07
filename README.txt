CRAWLPEDIA FLOATING PETS — ANDROID APK BUILD

This project packages the uploaded Crawlpedia.html inside an Android app and adds a native floating creature overlay.

IMPORTANT LIMITATION
The current floating overlay is a lightweight animated crawler prototype. It roams in a small draggable floating window, reacts to taps by changing its species number/appearance, and can be dragged with your finger. It does not yet render all original 1,111 detailed HTML species as native 3D/animated creatures; the full Crawlpedia browser remains inside the app.

HOW TO GET THE INSTALLABLE APK (FREE)
1. Create/sign in to a GitHub account.
2. Create a NEW repository named CrawlpediaFloating.
3. Upload the contents of this folder (not the outer folder itself) to the repository.
4. Open the repository's Actions tab and enable GitHub Actions if prompted.
5. Select “Build Crawlpedia APK” and press “Run workflow” (or wait for the automatic build after uploading to main).
6. When the run is green, open it and download the artifact named “Crawlpedia-Floating-Pets-APK”.
7. Extract the ZIP; install app-debug.apk on your Android phone. Android may ask you to allow installation from that source.
8. Open Crawlpedia Floating Pets, tap START FLOATING PET, and allow “Display over other apps”. Return to the app and tap START again.

STOPPING
Tap STOP FLOATING PET in the app, or stop the app from Android Settings.

BUILD SETTINGS
Package: com.crawlpedia.floating
Minimum Android: 6.0 (API 23)
Target SDK: 35
The debug APK is unsigned for Play Store release but is installable directly on a compatible Android phone.
