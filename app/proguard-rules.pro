# Keep all classes in your app's main package and its subpackages
-keep class com.akash.memories.** { *; }

# Keep specific class and all its members
-keep class com.akash.memories.memories.CreateMemory { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
