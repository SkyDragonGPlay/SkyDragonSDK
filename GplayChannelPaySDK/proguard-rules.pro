# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zhangjunfei/work/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class arch to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontshrink
-dontoptimize
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-ignorewarnings
-repackageclasses com.skydragon.gplay.paysdk
-keepattributes InnerClasses,Signature,*Annotation*
-dontpreverify
-verbose
-dontwarn

##########################################################################
## Android System libraries that don't need to be obfuscated

-dontskipnonpubliclibraryclasses
-verbose

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

#=================================================================
#module paysdk
-keepattributes Exceptions,InnerClasses,Signature,Deprecated

-keep public class com.skydragon.gplay.paysdk.GplayPaySDK {
    public <fields>;
    public <methods>;
}
-keep interface com.skydragon.gplay.paysdk.GplayPaySDK$** {*;}
-keep public class com.skydragon.gplay.paysdk.GplayUser {
    public <fields>;
    public <methods>;
}
-keep public class com.skydragon.gplay.paysdk.OAuthData {
    public <fields>;
    public <methods>;
}
-keep public class com.skydragon.gplay.paysdk.PayData {
    public <fields>;
    public <methods>;
}

-keep public class com.skydragon.gplay.paysdk.ui.CircleProgressBar {
    public <fields>;
    public <methods>;
}

-keep public class com.skydragon.gplay.paysdk.ui.GplayActivity {
    public void onCreate(android.os.Bundle);
    public void onBackPressed();
    protected void onResume();
    protected void onPause();
}
-keepclassmembers public class * extends android.support.v4.app.Fragment {
    public void onAttach(android.content.Context);
    public void onAttach(android.app.Activity);
    public android.view.View onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle);
    public void onActivityCreated(android.os.Bundle);
    public void onStart();
    public void onResume();
    public void onSaveInstanceState(android.os.Bundle);
    public void onPause();
    public void onStop();
    public android.app.Dialog onCreateDialog(android.os.Bundle);
    public void onActivityResult(int,int,android.content.Intent);
    public void onDestroyView();
    public void onDestroy();
    public void onDetach();
}

#log
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** d(...);
    public static *** e(...);
}
