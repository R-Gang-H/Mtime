混淆助手使用示例：
    场景1:类中所有public成员不混淆(不包括内部类)
        @ObfuscateKeepPublic
            public class TestAnnotationKeepPublic{
        }

    场景2:类中所有成员不混淆(不包括内部类)(内部类如果不需要混淆也要加入此注释)
        @ObfuscateKeepAll
            public class TestAnnotationKeepAll {
        }

    场景3:保留所有实现IObfuscateKeepAll接口的类,(注意接口有传递性,他的子类也会被keep)(内部类如果没有继承此接口会被混淆)
        public class TestInterfaceKeep implements IObfuscateKeepAll{
        }

    场景4:保留带注释的成员,适用于类和内部类
        public class TestAnnotationKeepThisField {

            public String sName;
            public static String sSName;
            private int iValue;

            @ObfuscateKeepThisField
            private static int iSValue;

            @ObfuscateKeepThisField
            private void tFunc(){
            }
        }

    场景5:保留类中set/get/is函数
        @ObfuscateKeepSetterGetter
        public class TestAnnotationKeepSetterGetter {}

==========================================================================================================