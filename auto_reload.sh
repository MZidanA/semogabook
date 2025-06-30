#!/bin/bash

APP_ID="com.insfinal.bookdforall"
ACTIVITY=".ui.SplashActivity"

echo "Menunggu perubahan file Kotlin..."
cat filelist.txt | entr -r bash -c '
    echo "🛠️  Building project..."
    ./gradlew build

    echo "📦 Installing APK..."
    ./gradlew installDebug

    echo "🔁 Restarting App on device..."
    adb shell am force-stop '"$APP_ID"'
    adb shell am start -n '"$APP_ID"/"$APP_ID""$ACTIVITY"'

    echo "⏳ Menunggu aplikasi berjalan..."
    sleep 2

    PID=$(adb shell pidof '"$APP_ID"' | tr -d "\r")

    if [ -z "$PID" ]; then
        echo "❌ Gagal mendapatkan PID untuk $APP_ID. Pastikan aplikasi berjalan."
    else
        echo "📋 Menampilkan logcat untuk PID: $PID"
        echo "📌 Tekan CTRL+C untuk berhenti"
        adb logcat --pid=$PID
    fi
'
