package br.com.sec4you.authfy.app

import android.content.SharedPreferences
import android.util.Log
import net.openid.appauth.AuthState
import org.json.JSONException
import java.util.concurrent.locks.ReentrantLock

class AuthStateManager(
  private val mPrefs: SharedPreferences?,
  private val mPrefsLock: ReentrantLock?
) {

  val authState: AuthState = AuthState()

  private val KEY_STATE = "state"

  fun readState() : AuthState {
    mPrefsLock?.lock();
    try {
      val currentState =
        mPrefs?.getString(KEY_STATE, null)
          ?: return AuthState();

      try {
        return AuthState.jsonDeserialize(currentState)
      } catch (ex: JSONException) {
        Log.e("HELPERFUNC", "Failed to deserialize stored auth state - discarding", ex)
        return AuthState()
      }
    } finally {
      mPrefsLock?.unlock();
    }
  }

  fun saveState() {
    mPrefsLock?.lock()
    try {
      // Assuming you have a jsonSerializeString() method in AuthState
      val jsonString = authState.jsonSerializeString()
      with(mPrefs?.edit()) {
        this?.putString(KEY_STATE, jsonString)
        this?.apply()
      }
    } finally{
      mPrefsLock?.unlock()
    }
  }

}
