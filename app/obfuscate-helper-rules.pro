#[Start-obfuscator 自定义混淆声明]

#类中所有成员不混淆(不包括内部类)
#对应声明 ObfuscateKeepAll
-keep @com.helen.obfuscator.ObfuscateKeepAll class * { *; }

#类中所有public成员不混淆(不包括内部类)
#对应声明ObfuscateKeepPublic
-keep @com.helen.obfuscator.ObfuscateKeepPublic class * {
  public <fields>;
  public <methods>;
}

#保留带注释的成员,适用于类和内部类
#对应声明ObfuscateKeepSetterGetter
-keepclassmembers class * {
@com.helen.obfuscator.ObfuscateKeepThisField * ;
}

#保留类中set/get/is函数
#对应声明ObfuscateKeepThisField
-keepclassmembers @com.helen.obfuscator.ObfuscateKeepSetterGetter class * {
    void set*(***);
    boolean is*();
    *** get*();
}
#保留所有实现IObfuscateKeepAll接口的类,(注意接口有传递性,他的子类也会被keep)(内部类如果没有继承此接口会被混淆)
#对应接口 IObfuscateKeepAll
-keep class * implements com.helen.obfuscator.IObfuscateKeepAll {
	*;
}
#[end-obfuscator 自定义混淆声明]