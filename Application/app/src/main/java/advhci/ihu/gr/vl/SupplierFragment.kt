package advhci.ihu.gr.vl

import adhvci.ihu.gr.vl.db_stuff.Supplier
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
class SupplierFragment : Fragment() {
    var L : ListView? = null
    var A : ArrayAdapter<String>? = null
    var i : Int = 0

    var addbtn: Button? = null
    var rmbtn : Button? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_supplier, container, false)
        L = view.findViewById(R.id.lst)
        addbtn = view.findViewById(R.id.addsup)
        rmbtn = view.findViewById(R.id.rmsupp)
        A = ArrayAdapter( this.requireContext() ,
            android.R.layout.simple_list_item_single_choice, fillList() )

        L!!.adapter  = A
        L!!.choiceMode = ListView.CHOICE_MODE_SINGLE
        L!!.setOnItemClickListener { _,_,position,_
        ->
            i = position
        }
        L!!.setItemChecked(0 , true)
        addbtn!!.setOnClickListener {
            MainActivity
                .fragmentManager
                .beginTransaction()
                .replace(
                    R.id.drawer_layout,
                    AddSupplierFragment()
                )
                .addToBackStack(null)
                .commit()
        }
        rmbtn!!.setOnClickListener{

            try {
                MainActivity
                    .sqlite
                    .supplier()
                    .delete(
                        MainActivity
                            .sqlite
                            .supplier() // we can ensure this will never be null cuz selection list duh
                            .find_by_name(A!!.getItem(i).toString())!!
                    )
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
                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG)
                    .show()
            }
        }
        return view
    }

    fun fillList() : List<String>
    {
        var s :  List<Supplier>  = MainActivity.sqlite.supplier().suppliers
        var ret  : MutableList<String> = ArrayList()
        for( S in s ) {
            ret.add(  S.name )
        }
        return ret.toList()
    }

}