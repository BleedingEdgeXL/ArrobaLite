/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arrobaLite.util;

import android.content.ContentValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.arrobaLite.annotations.Column;
import org.arrobaLite.annotations.ColumnType;
import org.arrobaLite.Exception.AnnotationException;
import org.arrobaLite.annotations.Table;

/**
 *
 * @author seba
 */
public class ObjectConverter {
    private Table table;
    private Column primaryKey;
    private Map<Field,Method> getters;
    private Map<Field,Method> setters;

    public Table getTable() { return table; }
    public Map<Field, Method> getGetters() { return getters; }
    public Map<Field, Method> getSetters() { return setters; }
    public Column getPrimaryKey() { return primaryKey; }

    public ObjectConverter(Class myClass) throws AnnotationException {
        this.getters = new HashMap(); this.setters = new HashMap();
        for (Field field : myClass.getDeclaredFields()) {
            if(field.isAnnotationPresent(Column.class)){
                this.getters.put(field,AnnotationUtil.findGetter(myClass,field));
                this.setters.put(field,AnnotationUtil.findSetter(myClass,field));
                if(field.getAnnotation(Column.class).primaryKey()){this.primaryKey = field.getAnnotation(Column.class); }
            }
        }
        this.table = AnnotationUtil.getTable(myClass);
    }
    
    public ContentValues createValues(Object obj) throws AnnotationException{
        try {
           ContentValues tableMap = new ContentValues();
            for (Field field : obj.getClass().getDeclaredFields()) {
               if(field.isAnnotationPresent(Column.class)){
                   Column anCol = field.getAnnotation(Column.class);
                   if(!anCol.readonly()){
                       Object fieldValue = this.getters.get(field).invoke(obj);
                       if(anCol.primaryKey() && !anCol.autoincrement() && fieldValue==null ){
                           throw new AnnotationException(String.format("Primary key %s is null.", field.getName()));
                       }
                       if(fieldValue!=null){ tableMap.put(anCol.name(), fieldValue.toString()); }
                   }
              }
          }
          return tableMap;
        } 
        catch (Exception e) { e.printStackTrace(); throw new AnnotationException(e.getMessage()); }
    }
    
     public Object createObject(Class myClass, ResultObject resultObject) throws AnnotationException{
        try {
           Object object = myClass.newInstance();
            for (Field field : myClass.getDeclaredFields()) {
               if(field.isAnnotationPresent(Column.class)){
                   Column anCol = field.getAnnotation(Column.class);
                   ColumnType type = anCol.columnType();
                   if(type==ColumnType.BOOLEAN){ this.setters.get(field).invoke(object,resultObject.getBoolean(anCol.name())); }
                   if(type==ColumnType.TEXT){ this.setters.get(field).invoke(object,resultObject.getString(anCol.name())); }
                   if(type==ColumnType.INTEGER){ this.setters.get(field).invoke(object,resultObject.getInteger(anCol.name())); }
                   if(type==ColumnType.LONG){ this.setters.get(field).invoke(object,resultObject.getLong(anCol.name())); }
                   if(type==ColumnType.REAL){ this.setters.get(field).invoke(object,resultObject.getFloat(anCol.name())); }
              }
          }
          return object;
        } 
        catch (Exception e) { e.printStackTrace(); throw new AnnotationException(e.getMessage()); }
    }
     
}
