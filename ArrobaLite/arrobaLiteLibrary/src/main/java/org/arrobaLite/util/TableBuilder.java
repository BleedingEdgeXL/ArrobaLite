/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arrobaLite.util;

import org.arrobaLite.annotations.ForeignKey;
import org.arrobaLite.annotations.Table;
import org.arrobaLite.annotations.Column;
import org.arrobaLite.annotations.ColumnType;
import java.util.List;
import org.arrobaLite.Exception.AnnotationException;

/**
 *
 * @author seba
 */
public class TableBuilder {

    private static final String CREATE_STATEMENT = " CREATE TABLE IF NOT EXISTS %s (";
    //Text
    private static final String ADD_VARCHAR_STATEMENT = "%s TEXT DEFAULT %s,";
    private static final String ADD_VARCHAR_NOT_NULL_STATEMENT = "%s TEXT NOT NULL DEFAULT %s,";
    private static final String ADD_VARCHAR_PK_STATEMENT = "%s TEXT NOT NULL PRIMARY KEY DEFAULT %s,"; 
    //Integer
    private static final String ADD_INTEGER_STATEMENT = "%s INTEGER DEFAULT %s,";
    private static final String ADD_INTEGER_NOT_NULL_STATEMENT = "%s INTEGER NOT NULL DEFAULT %s,";
    private static final String ADD_INTEGER_PK_STATEMENT = "%s INTEGER NOT NULL PRIMARY KEY DEFAULT %s,";
    //Real
    private static final String ADD_REAL_STATEMENT = "%s REAL DEFAULT %s,";
    private static final String ADD_REAL_NOT_NULL_STATEMENT = "%s REAL NOT NULL DEFAULT %s,";
    private static final String ADD_REAL_PK_STATEMENT = "%s REAL NOT NULL PRIMARY KEY DEFAULT %s,";
    //Boolean
    private static final String ADD_BOOLEAN_STATEMENT = "%s INTEGER DEFAULT 0,";
    private static final String ADD_FOREIGN_KEY_STATEMENT = "FOREIGN KEY(%s) REFERENCES %s (%s),";
    private static final String ADD_PRIMARY_KEY_STATEMENT = "PRIMARY KEY (%s),";
    private static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS %s;";
    private static final String CREATE_INDEX_STATEMENT = "CREATE UNIQUE INDEX IF NOT EXISTS %s ON %s(%s);";
    private static final String DROP_INDEX_STATEMENT = "DROP INDEX %s;";
    private static final String STATEMENT_TRUNCATE_FORMAT = "DELETE FROM %s WHERE %s IS NOT NULL;";
     private static final String COLON_CHAR = ",";
    private static final String END_TABLE_CHAR = ");";
    
    public String createTable(Class myClass) throws AnnotationException{
        StringBuilder table = new StringBuilder();
        table.append(String.format(CREATE_STATEMENT, AnnotationUtil.getTable(myClass).name()));
        //Processs Columns.
        List<Column> columns = AnnotationUtil.getColumns(myClass);
        for (Column column : columns) {
            if(column.columnType() == ColumnType.BOOLEAN){
              table.append(String.format(ADD_BOOLEAN_STATEMENT, column.name()));
            }
            if(column.columnType() == ColumnType.TEXT){
                if(column.primaryKey()){ table.append(String.format(ADD_VARCHAR_PK_STATEMENT, column.name(),column.defaultVarcharValue())); }
                if(!column.primaryKey() && column.notNull()){ table.append(String.format(ADD_VARCHAR_NOT_NULL_STATEMENT, column.name(),column.defaultVarcharValue())); }
                if(!column.primaryKey() && !column.notNull()){ table.append(String.format(ADD_VARCHAR_STATEMENT, column.name(),column.defaultVarcharValue())); }
            }
            if(column.columnType() == ColumnType.INTEGER || column.columnType()== ColumnType.LONG){
               if(column.primaryKey()){ table.append(String.format(ADD_INTEGER_PK_STATEMENT, column.name(),column.defaultNumericValue())); }
               if(!column.primaryKey() && column.notNull()){ table.append(String.format(ADD_INTEGER_NOT_NULL_STATEMENT, column.name(),column.defaultNumericValue())); }
               if(!column.primaryKey() && !column.notNull()){ table.append(String.format(ADD_INTEGER_STATEMENT, column.name(),column.defaultNumericValue())); }
            }
            
            if(column.columnType() == ColumnType.REAL){
               if(column.primaryKey()){ table.append(String.format(ADD_REAL_PK_STATEMENT, column.name(),column.defaultNumericValue())); }
               if(!column.primaryKey() && column.notNull()){ table.append(String.format(ADD_REAL_NOT_NULL_STATEMENT, column.name(),column.defaultNumericValue())); }
               if(!column.primaryKey() && !column.notNull()){ table.append(String.format(ADD_REAL_STATEMENT, column.name(),column.defaultNumericValue())); }
            }
        }
        //Process fks;
         List<ForeignKey> foreignKeys = AnnotationUtil.getForeignKeys(myClass);
        for (ForeignKey foreignKey : foreignKeys) {
            table.append(String.format(ADD_FOREIGN_KEY_STATEMENT, foreignKey.columnName(),foreignKey.foreignTableName(),foreignKey.foreignColumnName()));
        }
        String dml = table.toString();
        return dml.substring(0, dml.lastIndexOf(COLON_CHAR)) +END_TABLE_CHAR;
   }
    
    
    public String dropTable(Class myClass) throws AnnotationException{
        return String.format(DROP_TABLE_STATEMENT, AnnotationUtil.getTable(myClass).name());
    }

    public String truncateTable(Class myClass) throws AnnotationException{
        Table anTable = AnnotationUtil.getTable(myClass);
        Column pkCol = AnnotationUtil.getPrimaryKeyColumn(myClass);
        if(pkCol!=null){ return String.format(STATEMENT_TRUNCATE_FORMAT, anTable.name(),pkCol.name()); }
        throw new AnnotationException("Primary Key not found.");
    }
    
     public String createTableIndex(Class myClass) throws AnnotationException{
         String tableName = AnnotationUtil.getTable(myClass).name();
         String pkName = AnnotationUtil.getPrimaryKeyColumn(myClass).name();
         if(AnnotationUtil.getIndex(myClass)!=null){
             String indexName = AnnotationUtil.getIndex(myClass).name();
             return String.format(CREATE_INDEX_STATEMENT, indexName,tableName,pkName);
         } else{
             return String.format(CREATE_INDEX_STATEMENT, "NULL_INDEX",tableName,pkName);
         }
    }
     
    public String dropTableIndex(Class myClass) throws AnnotationException{
        return String.format(DROP_INDEX_STATEMENT, AnnotationUtil.getIndex(myClass).name());
    }
}
