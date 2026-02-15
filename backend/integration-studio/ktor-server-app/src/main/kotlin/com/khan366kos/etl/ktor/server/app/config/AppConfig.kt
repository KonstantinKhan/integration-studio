package com.khan366kos.etl.ktor.server.app.config

import com.khan366kos.etl.ktor.server.app.session.SessionStore
import com.khan366kos.etl.polynom.bff.PolynomClient

class AppConfig(
    val sessionStore: SessionStore,
    val polynomClient: PolynomClient
)