# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Jorell\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
public *;
}
-keepclassmembers class * {
    public void openFileChooser(android.webkit.ValueCallback, java.lang.String);
    public void openFileChooser(android.webkit.ValueCallback);
    public void openFileChooser(android.webkit.ValueCallback, java.lang.String, java.lang.String);
    public boolean onShowFileChooser(android.webkit.WebView, android.webkit.ValueCallback, android.webkit.WebChromeClient.FileChooserParams);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
   public void openFileChooser(...);
}

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

-keep class * extends android.webkit.WebChromeClient { *; }

-dontwarn okio.**
