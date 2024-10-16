package advhci.ihu.gr.vl

import adhvci.ihu.gr.vl.db_stuff.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
class ProductsFragment : Fragment() {
    var L : ListView? = null
    var A : ArrayAdapter<String>? = null
    var i : Int = 0

    var addbtn: Button? = null
    var rmbtn : Button? = null
    var morebt : Button? = null
    var ids : MutableList<Long>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_products, container, false)

        L = view.findViewById(R.id.lst)
        addbtn = view.findViewById(R.id.addprod)
        rmbtn = view.findViewById(R.id.rmprod)
        morebt = view.findViewById(R.id.more)
        ids = ArrayList<Long>()

        A = ArrayAdapter( this.requireContext() ,
            android.R.layout.simple_list_item_single_choice, fillList() )

        L!!.adapter  = A
        L!!.choiceMode = ListView.CHOICE_MODE_SINGLE
        L!!.setItemChecked(0 , true)
        L!!.setOnItemClickListener { _,_,position,_
            ->
            i = position
        }

        addbtn!!.setOnClickListener {
            MainActivity
                .fragmentManager
                .beginTransaction()
                .replace(
                    R.id.drawer_layout,
                    AddProductsFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        rmbtn!!.setOnClickListener{
            try {
                val adios: Product = MainActivity
                    .sqlite
                    .product()
                    .load_all_by_ids(arrayOf(ids!![i]).toLongArray())[0]

                MainActivity
                    .sqlite
                    .product()
                    .delete(adios)

                // reset the list after removing
                A = ArrayAdapter(
                    this.requireContext(),
                    android.R.layout.simple_list_item_single_choice, fillList()
                )

                L!!.adapter = A
                L!!.choiceMode = ListView.CHOICE_MODE_SINGLE
                L!!.setItemChecked(0, true)
                i = 0
            }
            catch(e:Exception)
            {
                Toast.makeText(requireContext(),e.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        }

        morebt!!.setOnClickListener{

            try {

                val product: Product = MainActivity
                    .sqlite
                    .product()
                    .load_all_by_ids(arrayOf(ids!![i]).toLongArray())[0]
                val supplier: Supplier = MainActivity
                    .sqlite
                    .supplier()
                    .load_all_by_ids(arrayOf(product.sid).toIntArray())[0]
                val supply: Supply = MainActivity
                    .sqlite
                    .supply()
                    .gtsupPid(product.pid)
                val category: Category = MainActivity
                    .sqlite
                    .category()
                    .getCategory(product.cid)!!

                var args = Bundle()
                args.putString("pname", product.PName)
                args.putString("pcat", category.category_name)
                args.putString("price", product.Price.toString())
                args.putString("supplier", supplier.name)
                args.putString("quant", supply.quant.toString())
                args.putString("desc", product.Desc)
                args.putString(
                    "total",
                    MainActivity.sqlite.supply().get_sum(product.pid).toString()
                )

                val info_frag = ProductInfo()
                info_frag.arguments = args
                MainActivity
                    .fragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.drawer_layout,
                        info_frag
                    )
                    .addToBackStack(null)
                    .commit()
            }
            catch(e:Exception)
            {
                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG)
                    .show()
            }
        }

        return view
    }
    fun fillList() : List<String>
    {
        var s :  List<ProductDao.toShow>  = MainActivity
            .sqlite
            .product()
            .get_prods_with_supps_and_availability
        var ret  : MutableList<String> = ArrayList()
        for( S in s ) {
            println(S.toString())
            ret.add(  S.availability+" "+S.pname+" "+S.sname )
            ids!!.add( S.pid )
        }
        println("size : "+MainActivity.sqlite.product().products.size)
        return ret.toList()
    }
}