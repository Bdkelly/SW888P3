package com.example.sw888p3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectedProductAdapter(
    private val context: Context,
    private var productList: MutableList<Product> // This list will hold the products to display
) : RecyclerView.Adapter<SelectedProductAdapter.ViewHolder>() {

    // ViewHolder class to hold the views for each item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productDescriptionTextView: TextView = itemView.findViewById(R.id.productDescriptionTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView) // If you have an image

        fun bind(product: Product) {
            productNameTextView.text = product.name
            productDescriptionTextView.text = product.description
            productPriceTextView.text = String.format("$%.2f", product.price)


            if (product.image != null) {
                productImageView.setImageResource(product.image)
            } else {
                productImageView.setImageResource(R.drawable.ic_launcher_background) // Provide a default image
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_item_selected_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    fun clearItems() {
        val oldSize = productList.size
        productList.clear()
        notifyItemRangeRemoved(0, oldSize)
    }
    fun getCurrentItems(): List<Product> {
        return productList.toList()
    }
    fun clearProducts() {
        val oldSize = productList.size
        productList.clear()
        notifyItemRangeRemoved(0, oldSize) // Notify adapter about the removal
    }
}
