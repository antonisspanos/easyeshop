package advhci.ihu.gr.vl

import adhvci.ihu.gr.vl.db_stuff.Category
import adhvci.ihu.gr.vl.db_stuff.Product
import adhvci.ihu.gr.vl.db_stuff.Supplier
import adhvci.ihu.gr.vl.db_stuff.Supply
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.lang.reflect.Executable

/**
 * A simple [Fragment] subclass.
 */
class AddProductsFragment : Fragment() {
    var editText1: EditText? = null
    var editText2: EditText? = null
    var editText3: EditText? = null
    var editText4: EditText? = null
    var editText5: EditText? = null
    var upbutton: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_prod, container, false)
        
        editText1 = view.findViewById(R.id.upeditText1)
        editText2 = view.findViewById(R.id.upeditText2)
        editText3 = view.findViewById(R.id.upeditText3)
        editText4 = view.findViewById(R.id.upeditText4)
        editText5 = view.findViewById(R.id.upeditText5)
        upbutton = view.findViewById(R.id.updateuser)

        upbutton!!.setOnClickListener {
            try {
                AddProd()
            }
            catch(e:java.lang.Exception)
            {
                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG)
                    .show()
            }
        }
        return view
    }

    fun AddProd()
    {
        val name : String = editText1!!.text.toString()
        val cat : String = editText4!!.text.toString()
        val price : Float = editText3!!.text.toString().toFloat()
        val desc : String = editText5!!.text.toString()
        val sup : String = editText2!!.text.toString()

        if( name.trim().isEmpty() or cat.trim().isEmpty() )
            throw Exception("empty fields")

        val cats = MainActivity.sqlite.category()
        val sups = MainActivity.sqlite.supplier()

        var id : Int? = cats.get_cid(cat)
        val s : Supplier? = sups.find_by_name(sup)

        if( id == null){
            cats.insert_all(Category(category_name = cat))
            id = cats.get_cid(cat)
        }
        var k : Int?
        if (s!=null)
            k =  s!!.sid
        else {
            val Z = Supplier(name = sup , email = null)
            MainActivity.sqlite.supplier().insert_all(Z)
            k = MainActivity
                .sqlite
                .supplier()
                .suppliers
                .last()
                .sid
        }

        val obj = Product(cid = id!! , sid = k!! , PName = name,
            Price = price , Desc = desc)
        MainActivity.sqlite.product().insert_all(obj)
        MainActivity.sqlite.supply().insert_all(Supply(pid =
            MainActivity
                .sqlite
                .product()
                .load_all_by_names(name)
                .last()
                .pid, quant = 0))

        Toast
            .makeText(this.requireContext(),"Product Added !! ", Toast.LENGTH_LONG)
            .show()
        MainActivity.pushNotification(requireContext(),"Product Added !! ")
    }
}