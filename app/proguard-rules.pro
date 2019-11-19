# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
#把混淆类中的方法名也混淆了
-useuniqueclassmembernames
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
# 避免混淆内部类、泛型、匿名类
-keepattributes InnerClasses,Signature,EnclosingMethod
#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#重命名抛出异常时的文件名称为"SourceFile"
-renamesourcefileattribute SourceFile
#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
 static final long serialVersionUID;
 private static final java.io.ObjectStreamField[] serialPersistentFields;
 private void writeObject(java.io.ObjectOutputStream);
 private void readObject(java.io.ObjectInputStream);
 java.lang.Object writeReplace();
 java.lang.Object readResolve();
}
#保留support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留继承的support类
-keep public class * extends androidx.annotation.**
#保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
     public <init>(android.content.Context);
     public <init>(android.content.Context, android.util.AttributeSet);
     public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留友盟所在包
-keep class com.umeng.** {*;}
#保留ijkplayer所在包
-keep class tv.danmaku.ijk.** {*;}
