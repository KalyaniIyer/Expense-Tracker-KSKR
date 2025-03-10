// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.6.0" apply false // Add the Safe Args plugin here
}