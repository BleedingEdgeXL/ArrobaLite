package org.arrobaLite.util;

import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by d070169 on 29/09/2017.
 */

public class ResultObject {
    private final Map<String,String> values = new HashMap();

     public ResultObject(Cursor cursor){
         if(cursor!=null && cursor.getColumnCount()>0){
             String[] columns = cursor.getColumnNames();
             for(String column : columns){ 
                int colIndex = cursor.getColumnIndexOrThrow(column);
                values.put(column,cursor.getString(colIndex));
             }
         }
     }

     public String getString(String column){ return this.values.get(column); }
     public int getInteger(String column){ String val = this.values.get(column); if(val!=null){return Integer.parseInt(val);} return 0; }
     public Long getLong(String column){ String val=this.values.get(column); if(val!=null){return new Long(val);} return null; }
     public boolean getBoolean(String column){ String val=this.values.get(column); return val != null && new Boolean(val); }
     public Float getFloat(String column){ String val=this.values.get(column); if(val!=null){return new Float(val);} return null; }
     public Double getDouble(String column){ String val=this.values.get(column); if(val!=null){return new Double(val);} return null; }
     public Map<String, String> getValues() { return values; }
     
}
