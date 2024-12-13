package com.unimib.ignitionfinance.presentation.viewmodel

import com.unimib.ignitionfinance.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel

class ResetPasswordScreenViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) {

}