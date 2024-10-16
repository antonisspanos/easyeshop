package advhci.ihu.gr.vl

import adhvci.ihu.gr.vl.db_stuff.Product
import adhvci.ihu.gr.vl.db_stuff.Supply
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class SuppliesFragment : Fragment() {
    var L : ListView? = null
    var A : ArrayAdapter<String>? = null
    var i : Int = 0

    var addbtn: Button? = null
    var rmbtn : Button? = null
    var quant : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_supplies, container, false)
        L = view.findViewById(R.id.lst)
        addbtn = view.findViewById(R.id.addsup)
        rmbtn = view.findViewById(R.id.rmsupp)
        quant = view.findViewById(R.id.quant)

        A = ArrayAdapter( this.requireContext() ,
            android.R.layout.simple_list_item_single_choice, fillList() )

        L!!.adapter  = A
        L!!.choiceMode = ListView.CHOICE_MODE_SINGLE
        L!!.setOnItemClickListener { _,_,position,_
            ->
            i = position
        }
        L!!.setItemChecked(0 , true)

        addbtn!!.setOnClickListener{
            try {

                val quant: Int = quant!!.text.toString().toInt()
                val substr: List<String> = A!!
                    .getItem(i)
                    .toString()
                    .split("\\s".toRegex())
                val pname: String = substr[0]
                val pids = MainActivity.sqlite
                    .product().load_all_by_names(pname)
                val rn : Int = Random.nextInt(0, pids.size)
                val pid: Long = pids[rn].pid
                val cur = MainActivity.sqlite.supply().gtsupPid(pid).quant
                MainActivity.sqlite.supply().update_p(cur + quant, pid)
                A = ArrayAdapter(
                    this.requireContext(),
                    android.R.layout.simple_list_item_single_choice, fillList()
                )
                L!!.adapter = A
                L!!.setItemChecked(0, true)
                i = 0
            }
            catch(e:Exception)
            {
                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG)
                    .show()
            }

        }
        rmbtn!!.setOnClickListener{
            try {

                val quant: Int = quant!!.text.toString().toInt()
                val substr: List<String> = A!!
                    .getItem(i)
                    .toString()
                    .split("\\s".toRegex())

                val cur: Int = substr[1].toInt()
                if (cur - quant >= 0) {
                    val pname: String = substr[0]
                    val prods = MainActivity.sqlite
                        .product().load_all_by_names(pname)
                    println(prods.toString())
                    val ref = MainActivity.sqlite.supply()

                    ref.try_rm(quant, prods)

                    A = ArrayAdapter(
                        this.requireContext(),
                        android.R.layout.simple_list_item_single_choice, fillList()
                    )
                    L!!.adapter = A
                    L!!.setItemChecked(0, true)
                    i = 0
                } else {
                    MainActivity.pushNotification(requireContext(), "Not enough Supply!!")
                }
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
        var ret  : MutableList<String> = ArrayList()
        val Prods = MainActivity.sqlite.supply().get_prod_supplies()
        for( (p, s)  in Prods)
            ret.add(  p + " " + s.toString() )

        return ret.toList()
    }
}