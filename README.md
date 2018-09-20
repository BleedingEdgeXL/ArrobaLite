# ArrobaLite
Basic annotation library for Android Sqllite

# Installation
import arrobaLiteLibrary-xxx.aar as a new Android Module.

# Annotations

* @Table (= "TABLE_NAME")

* @Index(name = "TABLE_INDEX")

* @Column(name = "COLUMN_NAME", columnType = ColumnType.INTEGER, primaryKey = true, notNull = true, autoincrement = true)

* @ForeignKey ( columnName "MY_COLUMN", foreignTableName = "OTHER_TABLE", String foreignColumnName = "OTHER_TABLE_ID" )

# ColumnTypes
* INTEGER: Integer
* TEXT: String, 
* BOOLEAN: Boolean 
* LONG: Long 
* REAL: Float

# Examples

@Table(name = "STUDENT")
@Index(name = "STUDENT_INDEX")
public class Student {

    @Column(name = "S_ID", columnType = ColumnType.LONG, primaryKey = true, notNull = true, autoincrement = true)
    private Long id;

    @Column(name = "S_NAME", columnType = ColumnType.TEXT, notNull = true)
    private String name;

    @Column(name = "S_LASTNAME", columnType = ColumnType.TEXT, notNull = true)
    private String lastname;

    @Column(name = "S_AVEGRAGE_GRADES", columnType = ColumnType.REAL, notNull = true)
    private float averageGrades;

    @Column(name = "S_EXPELLED", columnType = ColumnType.BOOLEAN)
    private boolean expelled;

    //Getters and Setters....
}

// Extend DbAnnotationsDao

public class StudentDao extends DbAnnotationsDao{

    private static StudentDao dao;

    private StudentDao(Context context, String databaseName, int version, Class myClass) {
        super(context, databaseName, version, myClass);
    }

    public static StudentDao getDao(Context context) {
        if(dao==null){dao=new StudentDao(context,"Student.db",1,Student.class); };
        return dao;
    }
    
}

# Dao Public Methods.

Finds the object in the table by id
* public final T FindById(Object id) throws QueryException

Lists all the rows in the table as objects
* public final List<T> list() throws QueryException

Inserts the Object into the table. 
* public final long save(T obj)throws InsertException

Inserts the Object into the table or updates it. 
* public final long saveOrReplace(T obj)throws InsertException

Inserts many objects of the same type into the same transaction.
* public final void saveMany(List<T> objs)throws InsertException
    
Updates the object into the table.
* public final void updateById(T obj)throws UpdateException

Deletes in the table the Object that recieves.
* public final void deleteById(T obj)throws DatabaseException

Deletes all the rows in the table.
* public final void deleteAll()throws DatabaseException

Returns the Object to Table Converter
* public ObjectConverter getConverter() throws AnnotationException 

Return an extended SqlLiteOpenHelper
* public SqlLiteConnector getConnector()

# Dao Protected Methods.

* protected final void delete(String whereColumn, String whereValue)throws DatabaseException
* protected final void update(T obj, String whereColumn, String whereValue)
* protected final void executeSql(String sql,Object[] parameters) throws DatabaseException
* protected final List<ResultObject> query(String query) throws QueryException
* protected final T querySingleObject(String query) throws QueryException

