package com.example.sw888p3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAda(
    private val context: Context,
    val productList: MutableList<Product>,
    private val onProductInteraction: (product: Product, isChecked: Boolean) -> Unit
    ) : RecyclerView.Adapter<ProductAda.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.productImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        private val sellerTextView: TextView = itemView.findViewById(R.id.productSellerTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.productCheckBox)

        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text =
                String.format("$%.2f", product.price) // Format price to 2 decimal places
            sellerTextView.text = "Sold by: ${product.seller}"

            // Handle image loading
            if (product.image != null) {
                imageView.setImageResource(product.image)
            } else {
                imageView.setImageResource(R.drawable.ic_placeholder) // Replace with your actual placeholder drawable
            }

            // Set CheckBox state and listener
            // Crucial: Set listener to null before changing checked state to avoid infinite loops or unwanted calls
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = product.isSelected

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                product.isSelected = isChecked
                onProductInteraction(
                    product,
                    isChecked
                ) // Notify MainActivity about the interaction
            }

            itemView.setOnClickListener {
                checkBox.toggle() // This will trigger the OnCheckedChangeListener`
            }
        }


    }
}


