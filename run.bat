@echo off
echo Compiling ZeldaRPG...
if not exist out mkdir out

(for /r src %%i in (*.java) do @echo "%%i") > sources.txt
javac -d out -sourcepath src -encoding UTF-8 @sources.txt
del sources.txt

if %errorlevel% neq 0 (
    echo COMPILATION FAILED
    pause
    exit /b 1
)

echo Build successful! Launching game...
java -cp "out;src" main.GameLauncher
