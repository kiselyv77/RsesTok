package com.example.rsestok


import android.graphics.Bitmap
import android.net.Uri
import com.example.rsestok.models.ChatModel
import com.example.rsestok.models.MessageModel
import com.example.rsestok.models.UserModel
import com.example.rsestok.models.VideoModel
import com.example.rsestok.utilits.*
import com.example.rsestok.utilits.app_listeners.AppChildEventListener
import com.example.rsestok.utilits.app_listeners.AppValueEventListener
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File


//NODES
const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_MESSAGES = "messages"
const val NODE_CHAT_LIST = "chat_list"
const val NODE_VIDEOS = "videos"
const val  CHILD_THUMBNAIL = "thumbnailUrl"

//USER CHILD
const val CHILD_ID = "id"
const val CHILD_FULLNAME = "fullname"
const val CHILD_USERNAME = "username"
const val CHILD_STATUS = "status"
const val CHILD_PROFILE_PHOTO_URI = "profilePhotoUri"
const val CHILD_DESCRIPTION = "description"
const val CHILD_USER_ID = "userId"
const val CHILD_TITLE = "title"
const val CHILD_VIDEO_URL = "videoURI"
const val CHILD_USER_VIDEO_ID = "userVideoId"


//MESSAGE CHILD
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"
const val CHILD_FILE_URL = "fileUrl"
const val CHILD_SUBSCRIBERS = "subscribers" //ПОДПИСКИ
const val CHILD_SUBSCRIPTIONS = "subscriptions" //ПОДПИСКИ

const val NIDE_LIKES = "likes"

//FOLDERS
const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_MESSEGE_IMSGE = "message_image"
const val MESSAGE_FILES = "messages_files"
const val VIDEOS_FILES = "videos_files"
const val THUMBNAIL_FILES = "thumbnail_files"






lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel
lateinit var usernamesListener: AppValueEventListener

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
    USER = UserModel()
}
inline fun  initUser( crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

//AUTH
fun createUser(email:String, password:String, fullname:String, username:String) {
    AUTH.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(APP_ACTIVITY) { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_FULLNAME] = fullname
                dateMap[CHILD_USERNAME] = username
                dateMap[CHILD_STATUS] = UserStatus.ONLINE

                REF_DATABASE_ROOT.child(NODE_USERNAMES).child(uid).setValue(username)
                REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                restartActivity()
            } else {
                showToast(task.exception?.message.toString())
            }
        }
//    usernamesListener = AppValueEventListener {
//        if(it.children.map{ it.getStringList() }.contains(username)){
//           showToast("Такой username уже есть")
//       }
//        else{
//            showToast("Работает")
//        }
//    }
//
//    REF_DATABASE_ROOT.child(NODE_USERNAMES).addValueEventListener(usernamesListener)
//    REF_DATABASE_ROOT.child(NODE_USERNAMES).removeEventListener(usernamesListener)

}
fun loginUser(email:String, password:String){
    AUTH.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
        if(task.isSuccessful){
            restartActivity()
        }
        else {
            showToast(task.exception?.message.toString())
        }
    }
}
fun signOut(){
    UserStatus.updateState(UserStatus.OFFLINE)
    AUTH.signOut()
    restartActivity()
}

//DATABASE
fun updateCurrentUsername(newUserName: String) {
    usernamesListener = AppValueEventListener {
        if(it.children.map{ it.getStringList() }.contains(newUserName)){
            showToast("Пользователь с таким именем уже существует")
        }
        else{
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
                .setValue(newUserName).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("Данные обновленны")
                        deleteOldUsername(newUserName)
                    } else showToast(it.exception?.message.toString())
                }

        }
    }
    REF_DATABASE_ROOT.child(NODE_USERNAMES).addListenerForSingleValueEvent(usernamesListener)
}


fun deleteOldUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("Данные обновленны")
                USER.username = newUserName
            } else showToast(it.exception?.message.toString())
        }
}


fun setNameToDataBase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME).setValue(fullname)
        .addOnCompleteListener() {
            if (it.isSuccessful) {
                showToast("Данные обновленны")
                USER.fullname = fullname
            }
        }
}


fun setDescriptionToDataBase(description: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_DESCRIPTION).setValue(description)
        .addOnCompleteListener() {
            if (it.isSuccessful) {
                showToast("Данные обновленны")
                USER.description = description
            }
        }
}


fun subscribe(uid:String, function: () -> Unit){
    val refUser = "$NODE_USERS/$CURRENT_UID/$CHILD_SUBSCRIPTIONS"
    val refReceivingUser = "$NODE_USERS/$uid/$CHILD_SUBSCRIBERS"
    val mapSubscribers = hashMapOf<String, Any>()
    mapSubscribers["$refUser/$uid"] = uid

    val mapSubscripions = hashMapOf<String, Any>()
    mapSubscribers["$refReceivingUser/$uid"] = AUTH.currentUser!!.uid

    REF_DATABASE_ROOT.updateChildren(mapSubscribers)
    REF_DATABASE_ROOT.updateChildren(mapSubscripions)
    function()
}


fun unsubscribe(uid: String, function: () -> Unit){
    val refUser = "$NODE_USERS/$CURRENT_UID/$CHILD_SUBSCRIPTIONS"
    val refReceivingUser = "$NODE_USERS/$uid/$CHILD_SUBSCRIBERS"

    REF_DATABASE_ROOT.child("$refUser/$uid").removeValue()
    REF_DATABASE_ROOT.child("$refReceivingUser/$uid").removeValue()
    function()
}


fun sendMessage(message: String, receivingUserID: String, type: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = type
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}


fun saveMainList(id: String, type: String) {
    val refUser = "$NODE_CHAT_LIST/$CURRENT_UID/$id"
    val refReceived = "$NODE_CHAT_LIST/$id/$CURRENT_UID"

    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type

    mapReceived[CHILD_ID] = CURRENT_UID
    mapReceived[CHILD_TYPE] = type

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }

}


fun getMessageKey(contactUID: String) = REF_DATABASE_ROOT.child(FOLDER_MESSEGE_IMSGE).child(CURRENT_UID).child(contactUID)
    .push().key.toString()

fun getVideoKey(uid: String) = REF_DATABASE_ROOT.child(VIDEOS_FILES).child(CURRENT_UID).child(uid)
    .push().key.toString()
fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(id).removeValue()
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(CURRENT_UID).removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}


fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_CHAT_LIST).child(CURRENT_UID).child(id).removeValue()
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener { showToast(it.message.toString()) }

}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    type: String,
    filename: String,
    userVideoId: String = ""
) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = type
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename
    mapMessage[CHILD_USER_VIDEO_ID] = userVideoId

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }

}


//STORAGE
inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}
inline fun putFiletoStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
inline fun putUrlToDataBase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_PROFILE_PHOTO_URI).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}
fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    type: String,
    filename: String = ""
) {
    val path = REF_STORAGE_ROOT.child(MESSAGE_FILES).child(messageKey)
    putFiletoStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(receivedID, it, messageKey, type, filename)
        }
    }
}
fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile).addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}



fun uploadVideoToStarage(
    userId: String,
    title: String,
    description: String,
    uri: Uri,
    videoKey: String,
    thumbnail: Bitmap,
    function: () -> Unit
){
    val path = REF_STORAGE_ROOT.child(VIDEOS_FILES).child(videoKey)
    val pathThumbnail = REF_STORAGE_ROOT.child(THUMBNAIL_FILES).child(videoKey)
    putFiletoStorage(uri, path) {

        getUrlFromStorage(path) {uri->
            val baos = ByteArrayOutputStream()
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val uploadTask:UploadTask = pathThumbnail.putBytes(baos.toByteArray())
            val task: Task<Uri> = uploadTask.continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    return pathThumbnail.downloadUrl
                }
            }).addOnCompleteListener(object : OnCompleteListener<Uri> {
                override fun onComplete(p0: Task<Uri>)
                {
                    uploadVideoToDataBase(videoKey, userId, uri, title, description, p0.result.toString()){
                        File(uri.toString()).delete()
                        function()
                    }
                }
            })

        }
    }

}


fun uploadVideoToDataBase(videoKey:String, userId:String,videoURI:String, title:String,description:String, thumbnailUrl:String, function: () -> Unit) {
    val refVideos = REF_DATABASE_ROOT.child(NODE_VIDEOS).child(CURRENT_UID).child(videoKey)
    val dateMap = mutableMapOf<String, Any>()
    dateMap[CHILD_ID] = videoKey
    dateMap[CHILD_USER_ID]  = userId
    dateMap[CHILD_TITLE]  = title
    dateMap[CHILD_VIDEO_URL]  = videoURI
    dateMap[CHILD_DESCRIPTION] = description
    dateMap[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    dateMap[CHILD_THUMBNAIL] = thumbnailUrl
    refVideos.updateChildren(dateMap).addOnCompleteListener{
        function()
    }

}

fun deleteVideo(videoModel: VideoModel, function: () -> Unit) {
    val refVideo = REF_DATABASE_ROOT.child(NODE_VIDEOS).child(videoModel.userId).child(videoModel.id)
    val pathVideo = REF_STORAGE_ROOT.child(VIDEOS_FILES).child(videoModel.id)
    val pathThumbnail = REF_STORAGE_ROOT.child(THUMBNAIL_FILES).child(videoModel.id)

    refVideo.removeValue().addOnSuccessListener {
        pathVideo.delete().addOnSuccessListener {
            pathThumbnail.delete().addOnSuccessListener {
                function()
            }
        }
    }

}

fun likeVideo(videoModel: VideoModel, uid: String){
    val refVideo = "$NODE_VIDEOS/${videoModel.userId}/${videoModel.id}/$NIDE_LIKES"
    val mapLikes = hashMapOf<String, Any>()
    mapLikes["$refVideo/${uid}"] = uid
    REF_DATABASE_ROOT.updateChildren(mapLikes)

    val refLikes = "$NODE_USERS/${videoModel.userId}/${NIDE_LIKES}"


    val globalLikesMap = hashMapOf<String, Any>()
    globalLikesMap["$refLikes/${videoModel.id}$CURRENT_UID"] = CURRENT_UID+videoModel.id
    REF_DATABASE_ROOT.updateChildren(globalLikesMap)
}

fun removeLikeVideo(videoModel: VideoModel, uid: String){
    val refVideo = "$NODE_VIDEOS/${videoModel.userId}/${videoModel.id}/$NIDE_LIKES"
    REF_DATABASE_ROOT.child("$refVideo/${uid}").removeValue()

    val refLikes = "$NODE_USERS/${videoModel.userId}/${NIDE_LIKES}"

    REF_DATABASE_ROOT.child("$refLikes/${videoModel.id}$CURRENT_UID").removeValue()

}

fun sendVideo(thumbnailUrl: String, userId: String, videoId: String, receivingUserID: String) {
    saveMainList(receivingUserID, TYPE_CHAT)
    sendMessageAsFile(receivingUserID, thumbnailUrl, getMessageKey(receivingUserID), TYPE_MESSAGE_VIDEO, "", userId)
}


//GET VALUE
fun DataSnapshot.getUserModel() = this.getValue(UserModel::class.java) ?: UserModel()

fun DataSnapshot.getChatModel() = this.getValue(ChatModel::class.java) ?: ChatModel()

fun DataSnapshot.getMessageModel() = this.getValue(MessageModel::class.java)?: MessageModel()

fun DataSnapshot.getVideoModel() = this.getValue(VideoModel::class.java)?: VideoModel()

fun DataSnapshot.getStringList() = this.getValue(String::class.java)?: String






