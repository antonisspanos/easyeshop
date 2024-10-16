package advhci.ihu.gr.vl


import adhvci.ihu.gr.vl.db_stuff.Supplier
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
class AddSupplierFragment : Fragment() {
    var editText1: EditText? = null
    var editText2: EditText? = null
    var upbutton: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_sup, container, false)
        editText1 = view.findViewById(R.id.upeditText1)
        editText2 = view.findViewById(R.id.upeditText2)
        upbutton = view.findViewById(R.id.updateuser)

        upbutton!!.setOnClickListener {
            try {
                AddSup()
            }
            catch(e:Exception)
            {
                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG)
                    .show()
            }
        }
        return view
    }

    fun AddSup()
    {
        val name : String = editText1!!.text.toString()
        val email : String? = editText2!!.text.toString()

        if(name.isEmpty())
            throw Exception("name field required")

        MainActivity.sqlite.supplier().insert_all(
            Supplier(name = name , email = email)
        )
        MainActivity.pushNotification(requireContext(),"Supplier Added !! ")
        Toast
            .makeText(this.requireContext(),"Supplier Added !! ", Toast.LENGTH_LONG)
            .show()

    }
}
