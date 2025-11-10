package com.urbanoexpress.iridio3.pre.presenter

import com.urbanoexpress.iridio3.pre.view.BaseV5View


/**
 * Created by Brandon Quintanilla on Febrero/03/2025.
 */
interface DriverContract: BaseV5View {

    interface DriverVerificationView {
        fun showLoginProgressDialog()
        fun dismissLoginProgressDialog()
        fun fromLoginToMainMenu()
        fun showError(error: String)
    }

    interface DriverVerificationPresenter {
        fun loginDriverUser(idRutaQR: String, verCodeQR: String, driverPhone: String)
        fun detachView()
    }

}