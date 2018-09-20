/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arrobaLite.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.arrobaLite.annotations.Column;
import org.arrobaLite.Exception.AnnotationException;
import org.arrobaLite.annotations.ForeignKey;
import org.arrobaLite.annotations.Table;
import org.arrobaLite.annotations.Index;

/**
 *
 * @author seba
 */
public final class AnnotationUtil {
    
    public static Table getTable(Class myClass) throws AnnotationException{
    if(!myClass.isAnnotationPresent(Table.class)){throw new AnnotationException("@Table not found."); }
        return (Table)myClass.getAnnotation(Table.class);
    }
    
    public static Index getIndex(Class myClass) throws AnnotationException{
    if(!myClass.isAnnotationPresent(Index.class)){throw new AnnotationException("@Index not found."); }
        return (Index)myClass.getAnnotation(Index.class);
    }
    
    public static List<Column> getColumns(Class myClass){
     List<Column> columns = new ArrayList();
     for (Field field : myClass.getDeclaredFields()) {
            if( field.getAnnotations().length> 0 ){
                Column anColumn = (Column)field.getAnnotation(Column.class);
                if(anColumn!=null){columns.add(anColumn); }
            }
      }
      return columns;
    }
    
    public static Column getPrimaryKeyColumn(Class myClass){
     for (Field field : myClass.getDeclaredFields()) {
         if( field.getAnnotations().length> 0 ){
            Column anColumn = (Column)field.getAnnotation(Column.class);
            if(anColumn!=null && anColumn.primaryKey()){return anColumn; }
         }
      }
     return null;
    }
    
     public static Object getPrimaryKeyValue(Object obj) throws AnnotationException{
         Class myClass = obj.getClass();
         for (Field field : myClass.getDeclaredFields()) {
             if( field.getAnnotations().length> 0 ){
                 Column anColumn = (Column)field.getAnnotation(Column.class);
                 if(anColumn!=null && anColumn.primaryKey()){ return getValue(obj, field, myClass); }
             }
         }
         throw new AnnotationException("Annotation not found");
     }
     
     public static void setPrimaryKeyValue(Object obj,Object value) throws AnnotationException{
         Class myClass = obj.getClass();
         for (Field field : myClass.getDeclaredFields()) {
             if( field.getAnnotations().length> 0 ){
                 Column anColumn = (Column)field.getAnnotation(Column.class);
                 if(anColumn!=null && anColumn.primaryKey()){ 
                     setValue(obj, value,field, myClass); return;
                 }
             }
         }
         throw new AnnotationException("Annotation not found");
     }
    
    public static List<ForeignKey> getForeignKeys(Class myClass){
     List<ForeignKey> foreignKeys = new ArrayList();
     for (Field field : myClass.getDeclaredFields()) {
        if( field.getAnnotations().length> 0 ){
            ForeignKey anFk = (ForeignKey)field.getAnnotation(ForeignKey.class);
            if(anFk!=null){foreignKeys.add(anFk); }
        }
     }
      return foreignKeys;
    }
    
    public static Object getValue(Object obj, Field field,Class myClass) throws AnnotationException{
        try {
            return findGetter(myClass,field).invoke(obj);
         } catch (Exception ex) {
             throw new AnnotationException(String.format("Error accesing field %s.", field.getName()),ex); 
        }
     }
    
     public static void setValue(Object obj,Object value, Field field,Class myClass) throws AnnotationException{
         try {
             findSetter(myClass,field).invoke(obj,value);
         }
        catch (IllegalArgumentException ex) {
            ex.printStackTrace();
             throw new AnnotationException(String.format("Argument type mismatch in field %s.", field.getName()),ex); 
        }
         catch (Exception ex) {
            ex.printStackTrace();
             throw new AnnotationException(String.format("Error accesing field %s.", field.getName()),ex);
        }
     }

    public static Method findSetter(Class myClass, Field field) throws AnnotationException {
        for (Method method : myClass.getMethods()) {
            String setterName = String.format("set%s%s",field.getName().substring(0, 1).toUpperCase(),field.getName().substring(1));
            if(method.getName().equals(setterName) ){ return method; }
        }
        throw new AnnotationException(String.format("Setter for field %s not found",field.getName()));
    }

    public static Method findGetter(Class myClass, Field field) throws AnnotationException {
        for (Method method : myClass.getMethods()) {
            String getterName = String.format("get%s%s",field.getName().substring(0, 1).toUpperCase(),field.getName().substring(1));
            String isName = String.format("is%s%s",field.getName().substring(0, 1).toUpperCase(),field.getName().substring(1));
            if(method.getName().equals(getterName) || method.getName().equals(isName)){ return method; }
        }
        throw new AnnotationException(String.format("Getter for field %s not found",field.getName()));
    }
}
