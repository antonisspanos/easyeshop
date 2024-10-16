package advhci.ihu.gr.vl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 */
class ProductInfo : Fragment()
{


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prod_info, container, false)

        view.findViewById<TextView>(R.id.pname).text = "Product Name : " + requireArguments().getString("pname")
        view.findViewById<TextView>(R.id.pcat).text = "Product Category : " + requireArguments().getString("pcat")
        view.findViewById<TextView>(R.id.pprice).text = "Product Price : " + requireArguments().getString("price")
        view.findViewById<TextView>(R.id.psupplier).text = "Product Supplier : " + requireArguments().getString("supplier")
        view.findViewById<TextView>(R.id.pquant).text = "Product Supply : " + requireArguments().getString("quant")
        view.findViewById<TextView>(R.id.desc).text = "Description : "+requireArguments().get("desc")
        view.findViewById<TextView>(R.id.tot).text = "Total Gain : "+requireArguments().get("total")

        return view
    }

}
