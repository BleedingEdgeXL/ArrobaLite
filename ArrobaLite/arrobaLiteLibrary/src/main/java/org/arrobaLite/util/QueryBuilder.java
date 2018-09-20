package org.arrobaLite.util;

/**
 * Created by d070169 on 11/01/2018.
 */

public class QueryBuilder {

    public enum Order{ASC,DESC};
    
    private final StringBuilder builder = new StringBuilder();

    public void addSql(String sql){ builder.append(sql); }

    public void addSelectAll(String table){ builder.append(String.format(" SELECT * FROM %s ",table)); }
     
    public void addWhere(String column,String value){ builder.append(String.format(" WHERE %s = %s ",column,value)); }

    public void addWhereString(String column,String value){ builder.append(String.format(" WHERE %s = '%s' ",column,value)); }

    public void addDeleteFrom(String table){ builder.append(String.format(" DELETE FROM %s ",table)); }

    public void addSelectCountAs(String table,String as){ builder.append(String.format(" SELECT COUNT(*) AS %s FROM %s ",as,table)); }

    public void addSelectCountColumnAs(String table,String column,String as){ builder.append(String.format(" SELECT COUNT(%s) AS %s FROM %s ",as,column,table)); }

    public void addAnd(String column,String value){ builder.append(String.format(" AND %s = %s ",column,value));  }

    public void addOr(String column,String value){ builder.append(String.format(" OR %s = %s ",column,value));  }
    
    public void addLike(String column,String value){ builder.append(String.format(" LIKE %s ",column,value));  }

    public void addOrderBy(String column,Order order){ builder.append(String.format(" ORDER BY %s %s ",column, order.toString())); }

    @Override
    public String toString() { return builder.toString()+";"; }
}
