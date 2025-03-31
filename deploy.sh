./gradlew clean bootJar

echo "Deploying"
scp build/libs/service-request-system-0.0.1-SNAPSHOT.jar dev:/var/www/request/app/main.jar


