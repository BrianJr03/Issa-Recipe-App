package jr.brian.issarecipeapp.util

const val GPT_3_5_TURBO = "gpt-3.5-turbo"

const val GENERATE_API_KEY_URL = "https://platform.openai.com/account/api-keys"

val copyToastMessages = listOf(
    "Your copy is ready for pasta!",
    "What are you waiting for? Paste!",
    "Your clipboard has been blessed.",
    "Copied!",
    "Copied, the text has been."
)

// Routes
const val HOME_ROUTE = "home"
const val ASK_ROUTE = "ask"
const val ASK_CONTEXT_ROUTE = "ask-context"
const val MEAL_DETAILS_ROUTE = "meal-details"
const val FAV_RECIPES_ROUTE = "fav-recipes"
const val SWIPE_RECIPES_ROUTE = "swipe-recipes"
const val SETTINGS_ROUTE = "settings"
// End Routes

// Max Values
const val MAX_CARDS_IN_STACK = 7
const val RECIPE_NAME_MAX_CHAR_COUNT = 40
const val PARTY_SIZE_MAX_CHAR_COUNT = 4
// End Max Values

// Meal Hours
const val breakfastStartHour = 5 // 5 AM
const val breakfastEndHour = 10 // 10 AM
const val lunchStartHour = 11 // 11 AM
const val lunchEndHour = 16 // 4 PM
// End Meal Hours

// Emojis
const val UP_SIDE_DOWN_FACE_EMOJI = "\uD83D\uDE43"
const val SAD_FACE_EMOJI = "\uD83D\uDE14"
const val COOL_FACE_EMOJI = "\uD83D\uDE0E"
// End Emojis

// Labels
const val API_KEY_LABEL = "OpenAI API Key"
const val PARTY_SIZE_LABEL = "Party Size *"
const val DIETARY_RESTRICTIONS_LABEL = "Dietary Restrictions"
const val FOOD_ALLERGY_LABEL = "Food Allergies"
const val INGREDIENTS_LABEL = "Ingredients *"
const val REJECTED_RECIPES_DIALOG_LABEL = "Rejected Recipes $SAD_FACE_EMOJI"
const val NO_REJECTED_RECIPES_DIALOG_LABEL = "No Rejected Recipes $COOL_FACE_EMOJI"
const val CHEF_GPT_LABEL = "ChefGPT"
const val USER_LABEL = "Me"
const val SWIPE_SCREEN_LABEL = "Love at First Swipe"
// End Labels

// Error Messages
const val API_KEY_REQUIRED = "API Key is required"
const val NO_RESPONSE_MSG = "No response. Please try again."
const val NO_RECIPES_TO_SWIPE_MSG = "No recipes to swipe at this time. Please check again later."
const val CONNECTION_TIMEOUT_MSG = "Connection timed out. Please try again."
const val ERROR = "error"
// End Error Messages