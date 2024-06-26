package com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.search.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.yusuf.yusuf_mucahit_solmaz_final.R
import com.yusuf.yusuf_mucahit_solmaz_final.core.utils.ViewUtils.setupGlide
import com.yusuf.yusuf_mucahit_solmaz_final.data.datastore.repo.UserSessionRepository
import com.yusuf.yusuf_mucahit_solmaz_final.data.mapper.toAddCartRequest
import com.yusuf.yusuf_mucahit_solmaz_final.data.remote.responses.cart.AddCartRequest
import com.yusuf.yusuf_mucahit_solmaz_final.data.remote.responses.product.Product
import com.yusuf.yusuf_mucahit_solmaz_final.databinding.ItemSearchProductBinding
import com.yusuf.yusuf_mucahit_solmaz_final.presentation.drawer.ui.search.SearchFragmentDirections
import java.text.DecimalFormat

class SearchProductAdapter (private  val products: ArrayList<Product>, private val context: Context, private val session: UserSessionRepository, private val addToCart: (AddCartRequest) -> Unit)
    : RecyclerView.Adapter<SearchProductAdapter.SearchProductViewHolder>() {

        class SearchProductViewHolder(val binding: ItemSearchProductBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {
        val itemBinding = ItemSearchProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchProductViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchProductViewHolder, position: Int) {

        val product = products[position]

        holder.binding.apply {
            title.text = product.title
            starRate.text = product.rating.toString()
            stock.text = "${context.getString(R.string.in_stock)}: ${product.stock}"
            price.text = "${product.price}$"
            shippingInformationTextView.text = product.shippingInformation

            oldPriceTextView.paintFlags = oldPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val oldPrice = product.price / (1 - product.discountPercentage / 100)
            val decimalFormat = DecimalFormat("#.#")
            val formattedPrice = decimalFormat.format(oldPrice)
            oldPriceTextView.text = ("$formattedPrice$")

            ratingBar.rating = product.rating.toFloat()

            setupGlide(context,product.images[0],productImage,loadingAnimationView)

            addToCartBtn.setOnClickListener {
                showAddToCartDialog(product)
            }

            root.setOnClickListener {
                val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(products[position].id.toString())
                it.findNavController().navigate(action)
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateSearchProduct(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    private fun showAddToCartDialog(product: Product) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val etQuantity = dialogView.findViewById<EditText>(R.id.et_quantity)
        val btnAddToCart = dialogView.findViewById<Button>(R.id.btn_add_to_cart)

        btnAddToCart.setOnClickListener {
            val quantity = etQuantity.text.toString().toIntOrNull()

            if (quantity != null ) {
                if (quantity.toInt() > product.stock){
                    Toast.makeText(context, context.getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show()

                }
                else if (quantity > 0) {
                    val cartProduct = product.toAddCartRequest(session.getUserId(), quantity.toString())

                    addToCart(cartProduct)
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, context.getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }
}