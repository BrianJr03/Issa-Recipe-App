package jr.brian.issarecipeapp.util

const val GPT_3_5_TURBO = "gpt-3.5-turbo"
const val GPT_4 = "gpt-4"

const val DALL_E_3 = "dall-e-3"
const val DEFAULT_IMAGE_SIZE = "1024x1024"
const val STANDARD_IMAGE_QUALITY = "standard"

const val GENERATE_API_KEY_URL = "https://platform.openai.com/account/api-keys"
const val API_USAGE_URL = "https://platform.openai.com/usage"

const val DEFAULT_RECIPE_TITLE = "Food"

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
const val GPT_LABEL = "GPT Model"
const val INGREDIENTS_LABEL = "Ingredients *"
const val REJECTED_RECIPES_DIALOG_LABEL = "Rejected Recipes $SAD_FACE_EMOJI"
const val NO_REJECTED_RECIPES_DIALOG_LABEL = "No Rejected Recipes $COOL_FACE_EMOJI"
const val CHEF_GPT_LABEL = "ChefGPT"
const val USER_LABEL = "Me"
const val SWIPE_SCREEN_LABEL = "Love at First Swipe"
// End Labels

// Error Messages
const val ERROR = "error"
const val API_KEY_REQUIRED = "API Key is required"
const val TITLE_IS_REQUIRED = "Title is required."
const val NO_RESPONSE_MSG = "No response. Please try again."
const val CONNECTION_TIMEOUT_MSG = "Connection timed out. Please try again."
const val NO_RECIPES_TO_SWIPE_MSG = "No recipes to swipe at this time. Please check again later."
const val ERROR_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/" +
        "No-Image-Placeholder.svg/1665px-No-Image-Placeholder.svg.png"
// End Error Messages