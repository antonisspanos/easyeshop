package advhci.ihu.gr.vl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment


class InfoTransactionFragment : Fragment()
{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_info_trans, container, false)

        view.findViewById<TextView>(R.id.pname).text = "Product Name : " + requireArguments().getString("pname")
        view.findViewById<TextView>(R.id.date).text = "  "+requireArguments().getString("date")
        view.findViewById<TextView>(R.id.pprice).text = "Product Price : " + requireArguments().getString("price")
        view.findViewById<TextView>(R.id.psupp).text = "Customer Name : " + requireArguments().getString("customer")
        view.findViewById<TextView>(R.id.pquant).text = "Quantity Sold : " + requireArguments().getString("quant")
        view.findViewById<TextView>(R.id.total).text = "Total moners : " + requireArguments().getString("total")

        return view
    }
}