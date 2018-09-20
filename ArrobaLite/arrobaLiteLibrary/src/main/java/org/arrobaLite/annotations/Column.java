/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arrobaLite.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
   String name();
   ColumnType columnType();
   boolean notNull() default false;
   boolean primaryKey() default false;
   boolean autoincrement() default false;
   int defaultNumericValue() default 0;
   String defaultVarcharValue() default "''";
   boolean readonly() default false;
}
