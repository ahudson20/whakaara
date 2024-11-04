package net.vbuild.verwoodpages.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application = application) {
    // TODO: begin to migrate code over from MainViewModel
}
