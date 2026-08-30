# PowerShell helper to generate a debug.keystore in repo root if keytool (JDK) is available
$keystorePath = Join-Path $PSScriptRoot "debug.keystore"
if (Test-Path $keystorePath) {
  Write-Output "debug.keystore already exists at $keystorePath"
  exit 0
}
$keytool = Get-Command keytool -ErrorAction SilentlyContinue
if (-not $keytool) {
  Write-Error "keytool not found on PATH. Install a JDK and ensure keytool is available."
  exit 2
}
& keytool -genkeypair -alias androiddebugkey -keypass android -storepass android -keystore $keystorePath -dname 'CN=Android Debug,O=Android,C=US' -keyalg RSA -validity 10000 -keysize 2048
if ($LASTEXITCODE -eq 0) { Write-Output "Created debug.keystore at $keystorePath" } else { Write-Error "Failed to create debug.keystore"; exit $LASTEXITCODE }
