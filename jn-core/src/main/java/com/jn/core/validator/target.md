
    /** Class, interface (including annotation type), or enum declaration */
    /** 若为type类型，可以修饰类、接口（包括注解类型）或者枚举 */
    TYPE,
 
    /** Field declaration (includes enum constants) */
    /** 可以修饰域（包括枚举常量） */
    FIELD,
 
    /** Method declaration */
    /** 修饰方法 */
    METHOD,
 
    /** Formal parameter declaration */
    /** 修饰参数 */
    PARAMETER,
 
    /** Constructor declaration */
    /** 修饰构造方法 */
    CONSTRUCTOR,
 
    /** Local variable declaration */
    /** 修饰局部变量 */
    LOCAL_VARIABLE,
 
    /** Annotation type declaration */
    /** 修饰注解类型，比如它自身就是使用此类型 */
    ANNOTATION_TYPE,
 
    /** Package declaration */
    /** 修饰包 */
    PACKAGE,
 
    /**
     * Type parameter declaration
     *
     * @since 1.8
     */
     /** 修饰类型参数 */
    TYPE_PARAMETER,
 
    /**
     * Use of a type
     *
     * @since 1.8
     */
     /** 修饰任何类型都可以 */
    TYPE_USE

————————————————
版权声明：本文为CSDN博主「把学习作为兴趣」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/AllTheWayQ2Q/article/details/114695373