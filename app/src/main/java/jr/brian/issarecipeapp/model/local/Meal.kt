package jr.brian.issarecipeapp.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meal(
    val name: String
) : Parcelable