package adhvci.ihu.gr.vl.db_stuff

import androidx.room.*
import androidx.room.RoomDatabase


@Entity
data class Supplier(
    @PrimaryKey(autoGenerate = true) val sid : Int  = 0,
    @ColumnInfo (name = "sname") val name : String,
    @ColumnInfo (name = "email") val email : String?
)

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val cid : Int = 0,
    @ColumnInfo(name = "cname") val category_name: String
)

@Entity(foreignKeys = arrayOf(ForeignKey(entity = Category::class,
parentColumns = arrayOf("cid"),
childColumns = arrayOf("cid"),
onDelete = ForeignKey.CASCADE) , ForeignKey(entity = Supplier::class,
    parentColumns = arrayOf("sid"),
    childColumns = arrayOf("sid"),
    onDelete = ForeignKey.CASCADE)))
data class Product(
    @PrimaryKey(autoGenerate = true) val pid : Long = 0,
    @ColumnInfo(name = "cid") val cid : Int,
    @ColumnInfo(name = "sid") val sid : Int,
    @ColumnInfo(name = "product_name") val PName : String,
    @ColumnInfo(name = "price") val Price : Float,
    @ColumnInfo(name = "desc") val  Desc : String?,

)

@Entity(foreignKeys = arrayOf(ForeignKey(entity = Product::class,
    parentColumns = arrayOf("pid"),
    childColumns = arrayOf("pid"),
    onDelete = ForeignKey.CASCADE) ))
data class Supply(
    @PrimaryKey(autoGenerate = true) val cuid  : Int = 0,
    @ColumnInfo(name = "pid") val pid : Long,
    @ColumnInfo(name = "quant") var quant : Int
)

@Dao
interface SupplyDao
{
    @Query("select * from supply where cuid in (:cuids)")
    fun get_supplies(cuids:IntArray) : List<Supply>

    @Query("select  p.product_name as product_name , sum(s.quant) as quant  from supply s inner join " +
            "product p on s.pid = p.pid group by product_name")
    fun get_prod_supplies() : List<ProductWithQuantity>


    @Query("select  sum(p.price * s.quant) as sum from supply s inner join" +
            " product p on s.pid = p.pid where p.pid = :pid" +
            " group by (p.pid) order by (sum)")
    fun get_sum(pid : Long ) : Double

    data class ProductWithQuantity(
        val product_name : String?,
        val quant : Int?
    )

    @Query("select * from supply where pid = :pid")
    fun gtsupPid(pid:Long) : Supply

    @get:Query("select * from supply")
    val supplies : List<Supply>

    @Query("update supply set quant = (:quant) where cuid = :cuid")
    fun update(quant : Int , cuid : Int)

    @Query("update supply set quant = (:quant) where pid = (:pid)")
    fun update_p(quant : Int , pid : Long)

    @Insert
    fun insert_all(vararg s: Supply)

    @Delete
    fun delete(s:Supply)

    fun try_rm(from : Int, prods : List<Product>)
    {
        var from = from
        for( s in prods)
        {
            val pid = s.pid
            val sup = gtsupPid(pid)

            if( sup.quant - from >= 0) {
                update(sup.quant - from, sup.cuid)
                break
            }
            else{
                update(0 , sup.cuid)
                from -= sup.quant
            }

        }
    }
}

@Dao
interface CategoryDao
{
    @Insert
    fun insert_all(vararg categories : Category)

    @Query("select * from category where cid = :cid ")
    fun getCategory(cid : Int ) : Category?

    @Query("select cid from category where cname like :name limit 1")
    fun get_cid(name : String) : Int?

    @Delete
    fun delete(category:Category)
}
@Dao
interface ProductDao {
    @get:Query("SELECT * FROM product")
    val products : List<Product>

    @Query("SELECT * FROM product WHERE pid IN (:pids)")
    fun load_all_by_ids(pids: LongArray): List<Product>

    @Query("select * from product where product_name = :name")
    fun load_all_by_names(name : String) : List<Product>

    @Query("SELECT * FROM product WHERE product_name LIKE :name LIMIT 1")
    fun find_by_name(name: String): Product

    @Query("select p.pid , p.product_name as name , p.price , s.quant from product p inner join " +
            "supply s on p.pid = s.pid where p.product_name = :name " +
            "group by p.product_name having sum(s.quant) > :ct ")
    fun get_product_if_product_is_available(name : String , ct : Int ) : List<tinfo>


    @get:Query("select p.pid , 'In-Stock' as availability, p.product_name as pname , s.sname " +
            "from product p join supplier s on p.sid = s.sid inner join supply z on p.pid = z.pid" +
            " where z.quant >= 4 " +
            "UNION  " +
            "select p.pid ,'Out-Of-Stock' as availability, p.product_name as pname, s.sname " +
            "from product p join supplier s on p.sid = s.sid inner join supply z on p.pid = z.pid" +
            " where z.quant = 0 " +
            "UNION  " +
            "select p.pid ,'Running-Low' as availability, p.product_name as pname, s.sname " +
            "from product p join supplier s on p.sid = s.sid inner join supply z on p.pid = z.pid" +
            " where z.quant < 4 and z.quant != 0 ")
    val get_prods_with_supps_and_availability : List<toShow>

    data class toShow(
        val pid : Long,
        val availability : String,
        val pname : String,
        val sname : String
    )
    data class tinfo(
        val pid : Long,
        val name : String,
        val price : Float,
        val quant : Int
    )

    @Insert
    fun insert_all(vararg products: Product)

    @Delete
    fun delete(product: Product)
}

@Dao
interface SupplierDao
{
    @get:Query("SELECT * FROM supplier")
    val suppliers: List<Supplier>

    @Query("SELECT * FROM supplier WHERE sid IN (:pids)")
    fun load_all_by_ids(pids: IntArray): List<Supplier>


    @Query("SELECT * FROM supplier WHERE sname LIKE :name LIMIT 1")
    fun find_by_name(name: String): Supplier?

    @Insert
    fun insert_all(vararg sups: Supplier)

    @Delete
    fun delete(sup: Supplier)
}



@Database(entities = [Product::class , Supplier::class , Category::class , Supply::class] ,
    version = 1)
abstract class LocalDb : RoomDatabase()
{
    abstract fun product() : ProductDao

    abstract fun category() : CategoryDao

    abstract fun supplier() : SupplierDao

    abstract  fun supply() : SupplyDao



}