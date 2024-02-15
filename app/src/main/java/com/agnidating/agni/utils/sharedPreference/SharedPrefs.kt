package com.agnidating.agni.utils.sharedPreference


import android.content.SharedPreferences
import com.agnidating.agni.model.Data
import com.agnidating.agni.utils.CommonKeys
import javax.inject.Inject


class SharedPrefs @Inject constructor(var sharePref: SharedPreferences) {
    /**
     * Function which will save the integer value to preference with given key
     */
    fun save(preferenceKey: String, integerValue: Int) {
        saveToPreference(
            preferenceKey,
            integerValue
        )
    }

    /**
     * Function which will save the double value to preference with given key
     */
    fun save(preferenceKey: String, doubleValue: Float) {
        saveToPreference(
            preferenceKey,
            doubleValue
        )
    }

    /**
     * Function which will save the long value to preference with given key
     */
    fun save(preferenceKey: String, longValue: Long) {
        saveToPreference(
            preferenceKey,
            longValue
        )
    }

    /**
     * Function which will save the boolean value to preference with given key
     */
    fun save(preferenceKey: String, booleanValue: Boolean) {
        saveToPreference(
            preferenceKey,
            booleanValue
        )
    }

    /**
     * Function which will save the string value to preference with given key
     */
    fun save(preferenceKey: String, stringValue: String?) {
        stringValue?.let {
            saveToPreference(
                preferenceKey,
                stringValue
            )
        }
    }

    /**
     * General function to save preference value
     */
    private fun saveToPreference(key: String, value: Any) {
        sharePref.let {
            with(it.edit()) {
                when (value) {
                    is Int -> putInt(key, value)
                    is Float -> putFloat(key, value)
                    is Long -> putLong(key, value)
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                }
                apply()
            }
        }
    }

    /**
     * Function which will return the integer value saved in the preference corresponds to the given preference key
     */
    fun getInt(preferenceKey: String): Int {
        return sharePref.getInt(preferenceKey, 0) ?: 0
    }

    /**
     * Function which will return the float value saved in the preference corresponds to the given preference key
     */
    fun getFloat(preferenceKey: String): Float {
        return sharePref.getFloat(preferenceKey, 0f) ?: 0f
    }

    /**
     * Function which will return the long value saved in the preference corresponds to the given preference key
     */
    fun getLong(preferenceKey: String): Long {
        return sharePref.getLong(preferenceKey, 0L) ?: 0L
    }

    /**
     * Function which will return the boolean value saved in the preference corresponds to the given preference key
     */
    fun getBoolean(preferenceKey: String): Boolean {
        return sharePref.getBoolean(preferenceKey, false)
    }

    /**
     * Function which will return the string value saved in the preference corresponds to the given preference key
     */
    fun getString(preferenceKey: String): String? {
        return sharePref.getString(preferenceKey, "") ?: ""
    }

    fun remove(preferenceKey: String) {
        sharePref.let {
            with(it.edit()) {
                remove(preferenceKey)
                apply()
            }
        }
    }

    fun clearPreference() {
        sharePref.let {
            with(it.edit()) {
                clear()
                apply()
            }
        }
    }

    fun saveUser(data: Data) {
        save(CommonKeys.LOGGED_IN,true)
        save(CommonKeys.USER_ID,data.id)
        save(CommonKeys.NAME,data.name)
        save(CommonKeys.EMAIL,data.email)
        save(CommonKeys.PHONE,data.phone)
        save(CommonKeys.COUNTRY_CODE,data.countryCode)
        save(CommonKeys.MESSAGE_ID,data.messageId)
        save(CommonKeys.TOTAL_FLOWERS,data.totalFlowers.toInt())
    }
    fun setSubscribed(subscribed:Boolean){
        save(CommonKeys.SUBSCRIBED,subscribed)
    }
    fun isSubscribed():Boolean{
        return getBoolean(CommonKeys.SUBSCRIBED)
    }
    fun saveFlowers(flowers:Int){
        save(CommonKeys.TOTAL_FLOWERS,flowers)
    }

    fun getMessageId(): String? {
        return getString(CommonKeys.MESSAGE_ID)
    }


}