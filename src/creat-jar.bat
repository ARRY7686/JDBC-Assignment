@echo off
echo Creating Recruitment Management System JAR...

:: Compile everything
javac -cp "lib\mysql-connector-j-8.4.0.jar" -d "bin" "src\main\java\com\recruitment\**\*.java"

:: Copy resources to bin
copy "src\main\resources\*" "bin\"

:: Create JAR with dependencies
jar -cfm "RecruitmentSystem.jar" "MANIFEST.MF" -C "bin" . -C "lib" mysql-connector-j-8.4.0.jar

echo JAR created: RecruitmentSystem.jar
echo Run with: java -jar RecruitmentSystem.jar
pause