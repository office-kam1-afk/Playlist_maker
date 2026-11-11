// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
   id("com.android.application") version "8.3.2" apply false // Исправлена опечатка (было 8.13.2)
   id("org.jetbrains.kotlin.android") version "1.9.24" apply false // <-- Понижено до стабильной 1.9.24
}