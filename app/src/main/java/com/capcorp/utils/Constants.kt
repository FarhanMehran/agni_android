package com.capcorp.utils


// dev base url
const val BASE_URL = "https://api-dev.h2d.app"

//live base url
//const val BASE_URL = "https://api-live.h2d.app"


const val HELP_URL = "$BASE_URL/help"
const val MAP_SCREENSHOT_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?center="
const val MAP_SCREENSHOT_END_URL = "&zoom=16&size=760x700&maptype=roadmap&key="
const val STATUS_CODE_SUCCESS = 200
const val USERTYPE = "type"
const val OTP = "otp"
const val ANDROID = "ANDROID"
const val COUNTRY_CODE = "countryCode"
const val PHONE_NUMBER = "phoneNo"
const val OLD_PHONE_NUMBER = "OLD_PHONE_NUMBER"
const val FULL_NUMBER = "fullNumber"
const val FIRST_NAME = "firstName"
const val COUNTRY_ISO = "countryISO"


// Test
const val STRIPE_KEY =
    "pk_test_51Gq5OfLnSiUjysZ2Q9T17O9rtrnKq12WQI6s3azT4XgVIinEppHVmErcNobScdLc69sU1liZeuasJpW0A8y16kkM00UWViIQVk"

// Live
//const val STRIPE_KEY = "pk_live_kuEiU69P5Hlfw9Vqqt9N7ziA00jcIHpXJK"


const val USER = "User"
const val LAST_NAME = "lastName"
const val DATE_OF_BIRTH = "dateOfBirth"
const val COUNTRY = "country"
const val EMAIL = "emailId"
const val BIO = "bio"
const val PASSWORD = "password"
const val PROFILE_PIC = "profile_pic"
const val ISFIRST_TIME = "isFirstTimeFilter"
const val LAT = "lat"
const val LONG = "long"
const val DEFAULT_LAT = "defaultLat"
const val DEFAULT_LONG = "defaultLong"
const val DEVICE_TYPE = "deviceType"
const val PROFILE_PIC_URL = "profilePicURL"
const val LANGUAGE_ID = "languageId"

const val IS_FILE_IMAGE = "isFileImage"
const val USER_DATA = "user_data"
const val FILTER_DATA = "filter_data"
const val SOCIAL_LOGIN_KEY = "key"
const val SOCIAL_LOGIN_TYPE = "type"
const val SOCIAL_TYPE = "socialType"
const val AUTHORISATION = "authorization"
const val ORDER_STATUS = "orderStatus"
const val LIMIT = "limit"
const val TRAVELER_ACTION = "travelerAction"
const val SKIP = "skip"

const val COUPON_CODE = "coupon_code"
const val ODER_ID = "orderId"

const val MESSAGE_ORDER = "messageOrder"
const val ACCESS_TOKEN = "accessToken"
const val DEVICE_TOKEN = "deviceToken"
const val WALKTHROUGH_STATUS = "walkthrough_status"
const val TRANSPORT_MIN_PRICE = 3
const val S3_BUCKET = "https://h2d-dev.s3-us-west-2.amazonaws.com/"
const val REQUEST_DETAIL = "request_detail"
const val COMPLETED_DETAIL = "completed_detail"
const val OFFER_TYPE = "offerType"
const val ACCEPT_DETAIL = "accept_detail"
const val IS_COMPLETED = "isCompleted"
const val REVIEW_DETAIL = "review_detail"
const val ORDER_CANCEL = "orderCancel"
const val PAGE_LIMIT = 20
const val ORDER_ID = "orderId"
const val OPPOSITION_ID = "opposition_id"
const val RECEIVER_INFO = "receiverInfo"
const val POSITION = "position"
const val ISDRIVERCANCEL = "isDriverCancel"
const val IS_USERRATED = "is_UserRated"
const val IS_DRIVERRATED = "isDriverRated"
const val CHANGED_URL = "changed_url"
const val PRICE = "price"
const val MAKE_OFFER = "make_offer"
const val ORDER_DETAIL = "order_detial"
const val LOCATION = "location"
const val TYPE_OFFER_MADE = "offer_made"
const val MESSAGE_ID = "messageId"
const val RECEIVER_ID = "receiverId"
const val USER_ID = "user_id"
const val RECOMMENDED_FEE = "RECOMMENDED_FEE"
const val USER_NAME = "userName"
const val USER_TYPE = "userType"
const val PREF_LANG = "prefLang"
const val IS_MUTE = "IS_MUTE"
const val LAST_MESSAGE = "last_message"
const val STORE = "store"
const val REQUEST_CREATED = "request_created"
const val REQUESTED_RESULT_CODE = 201
const val REQUEST_ADD_DETAIL = 202
const val ACCEPT_LIST_DETAIL = 204
const val NOTIFICATION = "notification"
const val MAP_LISTING = "map_listing"
const val IS_FIRST_TIME = "is_first_time"
const val IS_PROFILE_UPDATE = "isProfileUpdate"


class Validations {
    companion object {
        const val FIELD_EMPTY = "field_empty"
        const val FIELD_INVALID = "field_invalid"
        const val FIRST_NAME_EMPTY = "first_name_empty"
        const val FIRST_NAME_INVALID = "first_name_invalid"
        const val LAST_NAME_EMPTY = "last_name_empty"
        const val LAST_NAME_INVALID = "last_name_invalid"
        const val EMAIL_EMPTY = "email_empty"
        const val EMAIL_INVALID = "email_invalid"
        const val NUMBER_EMPTY = "number_empty"
        const val NUMBER_INVALID = "number_invalid"


    }
}

class BadgeName {
    companion object {
        const val NEW_SHOPPER = "NEW SHOPPER"
        const val BRONZE_SHOPPER = "BRONZE SHOPPER"
        const val SILVER_SHOPPER = "SILVER SHOPPER"
        const val GOLD_SHOPPER = "GOLD SHOPPER"
        const val PLATINUM_SHOPPER = "PLATINUM SHOPPER"
    }
}

class UtilityConstants {
    companion object {
        const val GALLERY = "GALLERY"
        const val VIDEO = "VIDEO"
        const val CAMERA = "CAMERA"
        const val ACTION_TAKE_VIDEO = 1
        const val PATH = "/h2d"
        const val IMAGENAME = "imageName"
        const val PHOTO_REQUEST_CAMERA = 2
        const val PHOTO_REQUEST_GALLERY = 3
        const val PHOTO_REQUEST_CROP = 4
        const val IMAGES_PREFIX = "IMG_"
        const val IMAGES_SUFFIX = ".jpg"

    }
}

class RequestType {
    companion object {
        const val TRANSPORT = "TRANSPORT"
        const val PARCEL = "PARCEL"
        const val GROCERIES = "GROCERIES"
        const val SHIPMENT = "SHIPMENT"
    }
}

class UserType {
    companion object {
        const val USER = "USER"
        const val DRIVER = "DRIVER"
    }
}

class DriverAction {
    companion object {
        const val OFFER = "OFFER"
        const val ACCEPT = "ACCEPT"
        const val REJECT = "REJECT"
    }
}

class DriverStatus {
    companion object {
        const val NO_ACTION = "NO_ACTION"
        const val PICKUP = "PICKUP"
        const val ARRIVED = "ARRIVED"
        const val DELIVERED = "DELIVERED"
        const val ACCEPTED = "ACCEPTED"
        const val PURCHASE_MADE = "PURCHASE MADE"
        const val CONFIRMATION = "CONFIRMATION"
        const val COMPLETED = "COMPLETED"
        const val COMMITED = "COMMITED"
        const val ADDRESS_GIVEN = "ADDRESS GIVEN"
        const val ADD_RECIEPT = "ADD RECEIPT"
    }
}

class OrderStatus {
    companion object {
        const val REQUESTED = "REQUESTED"
        const val ACCEPTED = "ACCEPTED"
        const val COMPLETED = "COMPLETED"
        const val EXPIRED = "EXPIRED"
    }
}

class OrderStatusDriver {
    companion object {
        const val OFFER_MADE = "OFFER MADE"
        const val ACCEPTED = "ACCEPTED"
        const val COMPLETED = "COMPLETED"
    }
}

class ChatType {
    companion object {
        const val TEXT = "text"
        const val IMAGE = "image"
    }
}

class ItemSize {
    companion object {
        const val POCKET = "POCKET"
        const val SMALL = "SMALL"
        const val MEDIUM = "MEDIUM"
        const val LARGE = "LARGE"
    }
}

class MediaUploadStatus {
    companion object {
        const val NOT_UPLOADED = "not_uploaded"
        const val UPLOADING = "uploading"
        const val CANCELED = "canceled"
        const val UPLOADED = "unloaded"
    }
}

class StaticRecommendFee {
    companion object {
        const val POCKET_SIZE_FEE = 10
        const val SMALL_SIZE_FEE = 15
        const val MEDIUM_SIZE_FEE = 18
        const val LARGE_SIZE_FEE = 20
    }
}

class MessageOrder {
    companion object {
        const val BEFORE = "BEFORE"
        const val AFTER = "AFTER"
    }
}

class OrderType {
    companion object {
        const val DELIVERY = "Delivery"
        const val PICKUP = "Pickup"
        const val SHOP = "Shop"
        const val NONE = "None"
    }
}

class NotificationType {
    companion object {
        const val OFFER = "OFFER"
        const val CHANGE_OFFER = "CHANGE_OFFER"
        const val REJECTED = "REJECTED"
        const val ACCEPTED = "ACCEPTED"
        const val PICKUP = "PICKUP"
        const val DELIVERED = "DELIVERED"
        const val ACCEPT = "ACCEPT"
        const val PURCHASE_MADE = "PURCHASE MADE"
        const val COMPLETED = "COMPLETED"
        const val CONFIRMATION = "CONFIRMATION"
        const val COMMITED = "COMMITED"
        const val ADDRESS_GIVEN = "ADDRESS GIVEN"
        const val CANCEL = "CANCEL"
        const val ADD_RECEIPT = "ADD RECEIPT"
    }
}
