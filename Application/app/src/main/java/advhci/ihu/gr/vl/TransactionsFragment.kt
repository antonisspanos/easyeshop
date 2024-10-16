package advhci.ihu.gr.vl

import adhvci.ihu.gr.vl.db_stuff.Product
import adhvci.ihu.gr.vl.db_stuff.Supplier
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldPath.documentId
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp



class TransactionsFragment : Fragment() {
    var L : ListView? = null
    var A : ArrayAdapter<String>? = null
    var i : Int = 0

    var Hashes : MutableList<String>? = null

    var addbtn: Button? = null
    var rmbtn : Button? = null
    var morebt : Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trans, container, false)

        L = view.findViewById(R.id.lst)
        addbtn = view.findViewById(R.id.addtr)
        rmbtn = view.findViewById(R.id.rmtr)
        morebt = view.findViewById(R.id.more)
        Hashes = ArrayList<String>()

        fillList()
        L!!.setOnItemClickListener { _, _, position, _
            ->
            i = position
        }

        addbtn!!.setOnClickListener {
            MainActivity
                .fragmentManager
                .beginTransaction()
                .replace(
                    R.id.drawer_layout,
                    AddTransactionFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        rmbtn!!.setOnClickListener{

            try {
                val hash: String = Hashes!![i]

                MainActivity
                    .fireslow
                    .collection("transactions")
                    .whereEqualTo(documentId(), hash)
                    .orderBy("date")
                    .get()
                    .addOnSuccessListener {

                        it.documents[0].reference.delete()
                        fillList()
                    }
                    .addOnFailureListener {
                        Toast
                            .makeText(
                                this.requireContext(),
                                "something went wrong",
                                Toast.LENGTH_LONG
                            )
                            .show()
                    }
                i = 0
            }
            catch(e:Exception)
            {
                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG)
                    .show()
            }

        }

        morebt!!.setOnClickListener {

            try {

                MainActivity
                    .fireslow
                    .collection("transactions")
                    .whereEqualTo(documentId(), Hashes!![i])
                    .get()
                    .addOnSuccessListener {

                        val doc = it.documents[0]
                        var args = Bundle()
                        args.putString("pname", doc.get("product").toString())
                        args.putString("date", (doc.get("date") as Timestamp).toDate().toString())
                        args.putString("price", doc.get("price").toString())
                        args.putString("customer", doc.get("customer").toString())
                        args.putString("quant", doc.get("quantity").toString())
                        args.putString(
                            "total", (doc.get("quantity").toString().toInt() *
                                    doc.get("price").toString().toFloat()).toString()
                        )

                        val info_frag = InfoTransactionFragment()
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
                    .addOnFailureListener {
                        Toast
                            .makeText(
                                this.requireContext(),
                                "something went wrong",
                                Toast.LENGTH_LONG
                            )
                            .show()
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


    fun fillList()  {

        var ret: MutableList<String> = ArrayList()


            MainActivity
                .fireslow
                .collection("transactions")
                .get()
                .addOnSuccessListener {
                    val docs = it.documents
                    for (doc in docs) {
                        println(doc.toString())
                        ret.add(
                            "Date : " + (doc.get("date") as Timestamp).toDate().toString() + " Total Moners : "
                                    + doc.get("price").toString().toFloat()
                                    * doc.get("quantity").toString().toInt()
                        )
                        Hashes!!.add(doc.id)
                    }
                    A = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_single_choice, ret
                    )

                    L!!.adapter = A
                    L!!.choiceMode = ListView.CHOICE_MODE_SINGLE
                    L!!.setItemChecked(0, true)

                }
                .addOnFailureListener{
                    Toast
                        .makeText(this.requireContext(),"something went wrong",Toast.LENGTH_LONG)
                        .show()
        }
    }

}

