package advhci.ihu.gr.vl


import adhvci.ihu.gr.vl.db_stuff.Product
import adhvci.ihu.gr.vl.db_stuff.Supplier
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

/**
 * A simple [Fragment] subclass.
 */
class AddTransactionFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_add_trans, container, false)
        editText1 = view.findViewById(R.id.upeditText1)
        editText2 = view.findViewById(R.id.upeditText2)
        editText3 = view.findViewById(R.id.upeditText3)
        editText4 = view.findViewById(R.id.upeditText4)
        upbutton = view.findViewById(R.id.updateuser)

        upbutton!!.setOnClickListener {
            AddTrans()
        }
        return view
    }

    fun AddTrans()
    {

            val tinfo = MainActivity.sqlite.product()
                .get_product_if_product_is_available(editText1!!.text.toString()
                ,editText4!!.text.toString().toInt())

            if(tinfo.isNotEmpty()) {
                MainActivity
                    .fireslow
                    .collection("transactions")
                    .add(
                        hashMapOf(
                            "product" to editText1!!.text.toString(),
                            "customer" to editText2!!.text.toString(),
                            "price" to editText3!!.text.toString().toFloat(),
                            "quantity" to editText4!!.text.toString().toInt(),
                            "date" to Timestamp.now()
                        )
                    )
                    .addOnSuccessListener {
                        MainActivity.pushNotification(
                            requireContext(),
                            "Trasanction Record Added!!"
                        )
                        MainActivity.sqlite.supply()
                            .try_rm(editText4!!.text.toString().toInt() ,
                            MainActivity.sqlite.product().load_all_by_names(tinfo[0].name))

                        Toast.makeText(
                            this.requireContext(),
                            "Trasanction Record Added!!",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this.requireContext(),
                            "Something went Wrong",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
            }
            else{

                MainActivity.pushNotification(
                    requireContext(),
                    "Not enough supply or invalid product name!!"
                )

            }

    }

}
