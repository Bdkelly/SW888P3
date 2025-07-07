package com.example.sw888p3

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductDB(context: Context):
        SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        private const val DATABASE_NAME = "products.db"

        private const val Product_table = "products"

        private const val KEY_ID = "id"
        private const val KEY_name = "name"
        private const val KEY_desc = "description"
        private const val KEY_seller = "seller"
        private const val KEY_price = "price"
        private const val KEY_image = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createProductsTable = ("CREATE TABLE " +
                Product_table + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_name + " TEXT," +
                KEY_desc + " TEXT," +
                KEY_seller + " TEXT," +
                KEY_price + " REAL," +
                KEY_image + " TEXT" + ")")
        db?.execSQL(createProductsTable)
        firstPoint(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $Product_table")
        onCreate(db)
    }

    private fun firstPoint(db: SQLiteDatabase?) {
        val firstProducts = listOf(
            Product(
                name = "Soccer Ball",
                description = "A Good Soccer Ball",
                seller = "John Doe",
                price = 29.99,
                image = R.drawable.sball
            ),
            Product(
                name = "Basketball",
                description = "A Good Basketball",
                seller = "Jane Doe",
                price = 39.99,
                image = R.drawable.bball
            ),
            Product(
                name = "Baseball",
                description = "A Good Baseball",
                seller = "John Smith",
                price = 19.99,
                image = R.drawable.baseb
            ),
            Product(
                name = "Frisbee",
                description = "A Good Frisbee",
                seller = "Jane Smith",
                price = 9.99,
                image = R.drawable.frisbee
            )
        )
        firstProducts.forEach { product ->
            val values = ContentValues().apply {
                put(KEY_name, product.name)
                put(KEY_desc, product.description)
                put(KEY_seller, product.seller)
                put(KEY_price, product.price)
                put(KEY_image, product.image)
            }
            db?.insert(Product_table, null, values)
        }
    }

    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        val productList = mutableListOf<Product>()
        val db = this@ProductDB.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM $Product_table", null)

            if (cursor.moveToFirst()) {
                do {
                    val idIndex = cursor.getColumnIndex(KEY_ID)
                    val nameIndex = cursor.getColumnIndex(KEY_name)
                    val descIndex = cursor.getColumnIndex(KEY_desc)
                    val sellerIndex = cursor.getColumnIndex(KEY_seller)
                    val priceIndex = cursor.getColumnIndex(KEY_price)
                    val imageIndex = cursor.getColumnIndex(KEY_image)

                    // Check for -1 which means column not found
                    if (idIndex != -1 && nameIndex != -1 && descIndex != -1 && sellerIndex != -1 && priceIndex != -1 && imageIndex != -1) {
                        val product = Product(
                            name = cursor.getString(nameIndex),
                            description = cursor.getString(descIndex),
                            seller = cursor.getString(sellerIndex),
                            price = cursor.getDouble(priceIndex),
                            image = cursor.getInt(imageIndex)
                        )
                        productList.add(product)
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., log them)
            e.printStackTrace()
        } finally {
            cursor?.close()
            // db.close() // SQLiteOpenHelper manages this
        }
        productList
    }
}
