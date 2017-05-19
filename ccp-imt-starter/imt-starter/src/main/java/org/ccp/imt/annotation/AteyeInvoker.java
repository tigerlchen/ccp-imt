package org.ccp.imt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AteyeInvoker
{

    /**
     * 方法的功能描述
     * @return
     */
    public String description() default "";
    /**
     * 参数的描述信息[如果是没有参数的，就是默认值空串；如果有参数的写成一个字符串，中间用分隔符&分开]
     * 比如有三个参数的方法travel(String startPoint,String endPoint,Date time)
     * 连接成的字符串例如：起点&终点&时间
     * @return
     */
    public String paraDesc() default "";
    /**
     * 方法类型，（只读、读写、Daily只读、Daily读写）
     * @return
     */
    public InvokerType type() default InvokerType.READ_ONLY;
}
