#!/usr/bin/env python3
import os
import subprocess
import shutil
import zipfile
import sys

ROOT = os.path.dirname(os.path.abspath(__file__))
GRADLEW = os.path.join(ROOT, "gradlew.bat" if os.name == "nt" else "gradlew")
APK_RELEASE_SRC = os.path.join(ROOT, "app", "build", "outputs", "apk", "release", "app-release.apk")
APK_DEBUG_SRC = os.path.join(ROOT, "app", "build", "outputs", "apk", "debug", "app-debug.apk")

MODULE_DIR = os.path.join(ROOT, "magisk-module")
DIST_DIR = os.path.join(ROOT, "dist")

OUTPUT_ZIP = os.path.join(DIST_DIR, "MagiskTemplate-M3-v1.0.0.zip")
OUTPUT_APK = os.path.join(DIST_DIR, "MagiskTemplate-M3-v1.0.0.apk")

def run(cmd, cwd=ROOT):
    print(f"[*] Running: {' '.join(cmd)}")
    res = subprocess.run(cmd, cwd=cwd, shell=(os.name == "nt"))
    if res.returncode != 0:
        print(f"[!] Command failed with code {res.returncode}")
        sys.exit(res.returncode)

def main():
    print("======================================================")
    print("  Building Magisk M3 Companion App & Flashable Zip    ")
    print("======================================================")

    os.makedirs(DIST_DIR, exist_ok=True)

    # 1. Compile Release APK
    print("\n[1/4] Compiling Android Companion App via Gradle...")
    run([GRADLEW, ":app:assembleRelease"])

    apk_source = APK_RELEASE_SRC if os.path.exists(APK_RELEASE_SRC) else APK_DEBUG_SRC
    if not os.path.exists(apk_source):
        print(f"[!] Error: Could not find built APK at {apk_source}")
        sys.exit(1)

    print(f"[*] Built APK: {apk_source} ({os.path.getsize(apk_source) / 1024:.1f} KB)")

    # 2. Copy APK into Magisk module structure
    print("\n[2/4] Integrating APK into Magisk Module structure...")
    target_apk_in_module = os.path.join(MODULE_DIR, "MagiskTemplate.apk")
    shutil.copy2(apk_source, target_apk_in_module)
    shutil.copy2(apk_source, OUTPUT_APK)

    # 3. Create flashable Magisk Zip
    print(f"\n[3/4] Packaging Flashable Magisk ZIP: {OUTPUT_ZIP}...")
    if os.path.exists(OUTPUT_ZIP):
        os.remove(OUTPUT_ZIP)

    with zipfile.ZipFile(OUTPUT_ZIP, "w", zipfile.ZIP_DEFLATED) as zf:
        for root_dir, _, files in os.walk(MODULE_DIR):
            for file in files:
                abs_path = os.path.join(root_dir, file)
                rel_path = os.path.relpath(abs_path, MODULE_DIR)
                archive_name = rel_path.replace("\\", "/")
                zf.write(abs_path, archive_name)
                print(f"  + {archive_name}")

    # 4. Summary
    print("\n[4/4] Build Completed Successfully!")
    print(f"  - Standalone APK: {OUTPUT_APK} ({os.path.getsize(OUTPUT_APK) / 1024:.1f} KB)")
    print(f"  - Flashable ZIP:  {OUTPUT_ZIP} ({os.path.getsize(OUTPUT_ZIP) / 1024:.1f} KB)")
    print("\nReady to flash in Magisk Manager, KernelSU, or APatch.")

if __name__ == "__main__":
    main()
