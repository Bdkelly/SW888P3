package com.example.sw888p3

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SelectedItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var selectedProductAdapter: SelectedProductAdapter
    private lateinit var clearButton: Button
    private lateinit var emailButton: Button

    private lateinit var dbHelper: ProductDB

    private val currentSelectedProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_items) // layout for this screen

        dbHelper = ProductDB(this)

        recyclerView = findViewById(R.id.selectedItemsRecyclerView)
        clearButton = findViewById(R.id.clearSelectedButton)
        emailButton = findViewById(R.id.emailSelectedButton)

        selectedProductAdapter = SelectedProductAdapter(this, currentSelectedProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = selectedProductAdapter

        clearButton.setOnClickListener {
            selectedProductAdapter.clearItems()
            Toast.makeText(this, "Displayed items cleared", Toast.LENGTH_SHORT).show()
        }
        emailButton.setOnClickListener {
            emailSelectedItems()
        }
    }

    private fun emailSelectedItems() {
        val itemsToEmail = selectedProductAdapter.getCurrentItems()

        if (itemsToEmail.isEmpty()) {
            Toast.makeText(this, "No items available to email.", Toast.LENGTH_SHORT).show()
            return
        }

        val emailBody = StringBuilder()
        emailBody.append("Here are the available products:\n\n") // Changed message slightly

        var totalPrice = 0.0
        itemsToEmail.forEach { product ->
            emailBody.append("Product: ${product.name}\n")
            // Assuming Product has description and price
            if (::dbHelper.isInitialized) { // Check if product has description and price
                emailBody.append("Description: ${product.description}\n")
                emailBody.append("Price: $${String.format("%.2f", product.price)}\n")
            }
            emailBody.append("---------------------------\n")
            if (::dbHelper.isInitialized) { totalPrice += product.price }
        }
        emailBody.append("\nTotal Price: $${String.format("%.2f", totalPrice)}\n")

        val emailSubject = "Product List" // Changed subject

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            putExtra(Intent.EXTRA_TEXT, emailBody.toString())
        }

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_LONG).show()
        }
    }

}