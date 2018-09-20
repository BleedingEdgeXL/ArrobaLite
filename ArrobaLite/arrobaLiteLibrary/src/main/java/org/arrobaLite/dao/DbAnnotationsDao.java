package org.arrobaLite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import org.arrobaLite.Exception.AnnotationException;
import org.arrobaLite.Exception.DatabaseException;
import org.arrobaLite.Exception.InsertException;
import org.arrobaLite.Exception.QueryException;
import org.arrobaLite.Exception.UpdateException;
import org.arrobaLite.annotations.Column;
import org.arrobaLite.annotations.ColumnType;
import org.arrobaLite.util.AnnotationUtil;
import org.arrobaLite.util.ObjectConverter;
import org.arrobaLite.util.ResultObject;

/**
 * Created by d070169 on 06/06/2018.
 */

public abstract class DbAnnotationsDao<T> {

    private ObjectConverter converter;
    private SqlLiteConnector connector;

    public DbAnnotationsDao(Context context, String databaseName,int databaseVersion,Class myClass) {
        this.connector = new SqlLiteConnector(context,databaseName,databaseVersion,myClass);
        this.connector.createTable(this.connector.getWritableDatabase());
    }

    public ObjectConverter getConverter() throws AnnotationException {
        if( converter == null){ converter = new ObjectConverter(connector.getDbClass()); }
        return converter;
    }

    public SqlLiteConnector getConnector() { return connector; }

    protected final List<ResultObject> executeQuery(String query) throws QueryException {
       Cursor cursor = null; SQLiteDatabase db = null;
        try{
            final List<ResultObject> objects = new ArrayList();
            Log.i("[EXECUTING QUERY]", query);
            db = this.connector.getConnectionReadOnly(); cursor = db.rawQuery(query,null);
            if (cursor.getCount() > 0) { while (cursor.moveToNext()) {objects.add(new ResultObject(cursor)); } }
            return objects;
        }catch (Exception e){ e.printStackTrace(); throw new QueryException(e); }
        finally{ this.connector.closeConnnection(cursor,db); }
    }

    protected final List<T> executeQuery(String tableName,String[] columns, String where,String[] params,String groupBy,String having,String orderBy) throws QueryException {
        Cursor cursor = null; SQLiteDatabase db = null;
        try{
            final List<T> objects = new ArrayList();
            db = this.connector.getConnectionReadOnly(); cursor = db.query(tableName,columns,where,params,groupBy,having,orderBy);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    T object = (T)getConverter().createObject(connector.getDbClass(),new ResultObject(cursor));
                    objects.add(object);
                }
            }
            return objects;
        }catch (Exception e){ e.printStackTrace(); throw new QueryException(e); }
        finally{ this.connector.closeConnnection(cursor,db); }
    }
    
     protected final T querySingleObject(String tableName,String[] columns, String where,String[] params,String groupBy,String having,String orderBy) throws QueryException {
        Cursor cursor = null; SQLiteDatabase db = null;
        try{
            db = this.connector.getConnectionReadOnly(); cursor =  db.query(tableName,columns,where,params,groupBy,having,orderBy);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Object obj = getConverter().createObject(connector.getDbClass(), new ResultObject(cursor));
                return (T)obj;
            }
            return null;
        }catch (Exception e){ e.printStackTrace(); throw new QueryException(e); }
        finally{ this.connector.closeConnnection(cursor,db); }
    }

    protected final ResultObject querySingleResult(String query) throws QueryException {
        Cursor cursor = null; SQLiteDatabase db = null;
        try{
            Log.i("[EXECUTING QUERY]", query);
            db = this.connector.getConnectionReadOnly(); cursor = db.rawQuery(query,null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst(); return new ResultObject(cursor);
            }
            return null;
        }catch (Exception e){ e.printStackTrace(); throw new QueryException(e); }
        finally{ this.connector.closeConnnection(cursor,db); }
    }

    public final T FindById(Object id) throws QueryException{
        Cursor cursor = null; SQLiteDatabase db = null;
        try{
            String tableName = AnnotationUtil.getTable(connector.getDbClass()).name();
            String pkName = AnnotationUtil.getPrimaryKeyColumn(connector.getDbClass()).name();
            String where = String.format("%s = ?",pkName);
            String[] params = new String[]{id.toString()};
            return this.querySingleObject(tableName,null,where,params,null,null,null);
        }catch (Exception e){ throw new QueryException(e); }
        finally{ this.connector.closeConnnection(cursor,db); }
    }
    
    public final List<T> list() throws QueryException{
        try {
            String tableName = getConverter().getTable().name();
            return this.executeQuery(tableName,null,null,null,null,null,null);
        } catch (Exception e) {throw new QueryException(e); }
    }
    
     protected final void executeSql(String sql,Object[] parameters) throws DatabaseException {
        SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection(); this.connector.getTransaction(db);
            Log.i("[EXECUTING QUERY]", sql);
            if(parameters!=null){db.execSQL(sql,parameters);} else{db.execSQL(sql);}db.setTransactionSuccessful();
        }
        catch (Exception e){throw new DatabaseException(e); }
        finally{ this.connector.closeTransaction(db); this.connector.closeConnnection(db); }
    }
     
    public final long save(T obj)throws InsertException{
        SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection(); this.connector.getTransaction(db);
            ContentValues insertValues = getConverter().createValues(obj);
            String table = AnnotationUtil.getTable(obj.getClass()).name();
            long rowId = db.insertOrThrow(table,null,insertValues);
            Column primaryKey = AnnotationUtil.getPrimaryKeyColumn(obj.getClass());
            if(primaryKey.autoincrement()){
                if(primaryKey.columnType()==ColumnType.LONG){ AnnotationUtil.setPrimaryKeyValue(obj,rowId); }
                if(primaryKey.columnType()==ColumnType.REAL){ AnnotationUtil.setPrimaryKeyValue(obj,new Float(rowId)); }
                if(primaryKey.columnType()==ColumnType.INTEGER){ AnnotationUtil.setPrimaryKeyValue(obj,new Long(rowId).intValue()); }
            }
            this.connector.commit(db); return rowId;
        }
        catch (Exception e){ throw new InsertException(e); }
        finally{ this.connector.closeTransaction(db); }
    }

    public final long saveOrReplace(T obj)throws InsertException{
         SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection();  this.connector.getTransaction(db);
            ContentValues insertValues = getConverter().createValues(obj);
            String table = AnnotationUtil.getTable(obj.getClass()).name();
            long rowId = db.insertWithOnConflict(table,null,insertValues,SQLiteDatabase.CONFLICT_REPLACE);
            Column primaryKey = AnnotationUtil.getPrimaryKeyColumn(obj.getClass());
            if(primaryKey.autoincrement()){
                if(primaryKey.columnType()==ColumnType.LONG){ AnnotationUtil.setPrimaryKeyValue(obj,rowId); }
                if(primaryKey.columnType()==ColumnType.REAL){ AnnotationUtil.setPrimaryKeyValue(obj,new Float(rowId)); }
                if(primaryKey.columnType()==ColumnType.INTEGER){ AnnotationUtil.setPrimaryKeyValue(obj,new Long(rowId).intValue()); }
            }
            this.connector.commit(db); return rowId;
        } catch (Exception e){ throw new InsertException(e); }
        finally{ this.connector.closeTransaction(db); }
    }
    
    public final void saveMany(List<T> objs)throws InsertException{
        SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection(); this.connector.getTransaction(db);
            for (T obj : objs) {
                String table = AnnotationUtil.getTable(obj.getClass()).name();
                ContentValues insertValues = getConverter().createValues(obj);
                long rowId = db.insertOrThrow(table,null,insertValues);
                Column primaryKey = AnnotationUtil.getPrimaryKeyColumn(obj.getClass());
                if(primaryKey.autoincrement()){
                    if(primaryKey.columnType()==ColumnType.LONG){ AnnotationUtil.setPrimaryKeyValue(obj,rowId); }
                    if(primaryKey.columnType()==ColumnType.REAL){ AnnotationUtil.setPrimaryKeyValue(obj,new Float(rowId)); }
                    if(primaryKey.columnType()==ColumnType.INTEGER){ AnnotationUtil.setPrimaryKeyValue(obj,new Long(rowId).intValue()); }
                }
            }
            this.connector.commit(db);
        }
        catch (Exception e){ throw new InsertException(e); }
        finally{ this.connector.closeTransaction(db); }
    }

    public final void saveDifferentObjects(List<Object> objs)throws InsertException{
        SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection(); this.connector.getTransaction(db);
            for (Object obj : objs) {
                String table = AnnotationUtil.getTable(obj.getClass()).name();
                ContentValues insertValues = getConverter().createValues(obj);
                long rowId = db.insertOrThrow(table,null,insertValues);
                Column primaryKey = AnnotationUtil.getPrimaryKeyColumn(obj.getClass());
                if(primaryKey.autoincrement()){
                    if(primaryKey.columnType()==ColumnType.LONG){ AnnotationUtil.setPrimaryKeyValue(obj,rowId); }
                    if(primaryKey.columnType()==ColumnType.REAL){ AnnotationUtil.setPrimaryKeyValue(obj,new Float(rowId)); }
                    if(primaryKey.columnType()==ColumnType.INTEGER){ AnnotationUtil.setPrimaryKeyValue(obj,new Long(rowId).intValue()); }
                }
            }
            this.connector.commit(db);
        }
        catch (Exception e){ throw new InsertException(e); }
        finally{ this.connector.closeTransaction(db); }
    }
    
      protected final void update(T obj, String whereColumn, String whereValue)throws UpdateException{
         SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection(); this.connector.getTransaction(db);
            ContentValues updateValues = getConverter().createValues(obj);
            String table = AnnotationUtil.getTable(obj.getClass()).name();
            String[] where = new String[]{whereValue};
            db.update(table, updateValues, String.format("%s = ?", whereColumn), where);
            this.connector.commit(db);
        } catch (Exception e){ throw new UpdateException(e); }
        finally{ this.connector.closeTransaction(db); }
    }
    
     public final void updateById(T obj)throws UpdateException{
        try{
          String pkName = AnnotationUtil.getPrimaryKeyColumn(obj.getClass()).name();
          Object pkValue = AnnotationUtil.getPrimaryKeyValue(obj);
          if(pkValue==null){throw new UpdateException("Primary key is null");}
          this.update(obj,pkName,pkValue.toString());
        } catch (Exception e){ throw new UpdateException(e); }
    }
     
      protected final void delete(String whereColumn, String whereValue)throws DatabaseException{
        SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection(); this.connector.getTransaction(db);
            String table = AnnotationUtil.getTable(connector.getDbClass()).name();
            String[] where = new String[]{whereValue};
            db.delete(table, String.format("%s = ?", whereColumn), where);
            this.connector.commit(db);
        } catch (Exception e){ throw new DatabaseException(e); }
        finally{ this.connector.closeTransaction(db); }
    }
     
      public final void deleteById(T obj)throws DatabaseException{
        try{
          String pkName = AnnotationUtil.getPrimaryKeyColumn(obj.getClass()).name();
          Object pkValue = AnnotationUtil.getPrimaryKeyValue(obj);
          if(pkValue==null){throw new DatabaseException("Primary key is null");}
          this.delete(pkName,pkValue.toString());
        } catch (Exception e){ throw new DatabaseException(e); }
    }

    public final void deleteAll()throws DatabaseException{
       SQLiteDatabase db = null;
        try{
            db = this.connector.getConnection(); this.connector.getTransaction(db);
            String table = AnnotationUtil.getTable(connector.getDbClass()).name();
            db.delete(table,null,null);
            this.connector.commit(db);
        } catch (Exception e){ throw new DatabaseException(e); }
        finally{ this.connector.closeTransaction(db); }
    }

}
