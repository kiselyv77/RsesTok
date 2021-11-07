
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rsestok.CHILD_FULLNAME
import com.example.rsestok.NODE_USERS
import com.example.rsestok.REF_DATABASE_ROOT
import com.example.rsestok.utilits.app_listeners.AppValueEventListener

class SingleChatViewModel(application: Application, uid:String) : ViewModel() {

    private val _name = MutableLiveData<String>().apply {
        REF_DATABASE_ROOT.child(NODE_USERS).child(uid).child(CHILD_FULLNAME).addListenerForSingleValueEvent(
            AppValueEventListener{
            value = it.getValue() as String?

        })
    }
    val name: LiveData<String> = _name
}