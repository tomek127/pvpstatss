# PvP Stats (Fabric 1.21.4, client-side)

Budowanie JAR-a:
1. Zainstaluj JDK 21 oraz Gradle 8.x (lub skopiuj folder gradle/ + gradlew z https://github.com/FabricMC/fabric-example-mod)
2. W tym folderze: `gradle build`  (albo `./gradlew build`)
3. Gotowy plik: build/libs/pvpstats-1.0.0.jar -> wrzuć do .minecraft/mods (potrzebny Fabric Loader + Fabric API)

Użycie: R (zmienialne w Sterowaniu) -> Start. Liczy trafienia, celność, combo, najlepsze combo, otrzymane trafienia i czas.
