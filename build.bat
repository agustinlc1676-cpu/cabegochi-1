@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
set ROOT=%~dp0n:: Check Javanjava -version >nul 2>&1nif ERRORLEVEL 1 (
  echo Java not found on PATH. Install JDK 11+ and ensure java is available.
  exit /b 2
)
:: Prefer gradlew if presentnif exist "%ROOT%gradlew.bat" (
  echo Using gradlew.bat
  call "%ROOT%gradlew.bat" assembleDebug --no-daemon --console=plain
  exit /b %ERRORLEVEL%
)
:: Fallback: bundled gradle distributionnif exist "%ROOT%gradle-dist\gradle-8.2.1\bin\gradle.bat" (
  echo Using bundled gradle distribution
  call "%ROOT%gradle-dist\gradle-8.2.1\bin\gradle.bat" -p "%ROOT%" assembleDebug --no-daemon --console=plain
  exit /b %ERRORLEVEL%
)
echo No gradlew or bundled gradle distribution found. Run "gradle wrapper --gradle-version 8.2.1" or install Gradle.
exit /b 3
