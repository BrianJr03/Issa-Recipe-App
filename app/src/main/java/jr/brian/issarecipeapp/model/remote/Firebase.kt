package jr.brian.issarecipeapp.model.remote

import com.google.firebase.database.FirebaseDatabase
import jr.brian.issarecipeapp.model.local.Recipe
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

fun retrieveRecipes(onSuccess: (List<Recipe>) -> Unit, onError: (error: DatabaseError) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val recipesRef = database.getReference("recipes")

    recipesRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val recipeList = mutableListOf<Recipe>()

            for (childSnapshot in dataSnapshot.children) {
                val recipeName = childSnapshot.child("name").getValue(String::class.java)
                val recipeContent = childSnapshot.child("recipe").getValue(String::class.java)

                if (recipeName != null && recipeContent != null) {
                    val recipe = Recipe(name = recipeName, recipe = recipeContent)
                    recipeList.add(recipe)
                }
            }

            onSuccess(recipeList)
        }

        override fun onCancelled(error: DatabaseError) {
            onError(error)
        }
    })
}


fun uploadRecipes(list: List<Recipe>) {
    val database = FirebaseDatabase.getInstance()
    val recipesRef = database.getReference("recipes")
    list.forEachIndexed { index, recipe ->
        val recipeRef = recipesRef.child("recipe $index")
        recipeRef.setValue(recipe)
    }
}

