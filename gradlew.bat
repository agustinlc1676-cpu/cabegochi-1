@echo off
set DIRNAME=%~dp0
set DEFAULT_JVM_OPTS=-Xmx64m
java -classpath "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*

