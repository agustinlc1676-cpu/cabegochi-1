package com.example.cabegochi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cabegochi.viewmodel.CabegochiViewModel

@Composable
fun AccountRegistrationScreen(
    viewModel: CabegochiViewModel,
    onDone: (accountId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val emailState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val otpState = remember { mutableStateOf("") }
    val statusState = remember { mutableStateOf("") }
    val awaitingOtp = remember { mutableStateOf(false) }
    val createdAccountId = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Registro / Cuenta")
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Correo (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phoneState.value,
            onValueChange = { phoneState.value = it },
            label = { Text("Teléfono (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (!awaitingOtp.value) {
            Button(onClick = {
                statusState.value = "Creando cuenta..."
                viewModel.createAccount(
                    emailState.value.ifBlank { null },
                    phoneState.value.ifBlank { null }
                ) { id ->
                    if (id != null) {
                        createdAccountId.value = id
                        awaitingOtp.value = true
                        statusState.value = "Cuenta creada: $id. Solicitando OTP..."
                        viewModel.requestOtp(id) { otp ->
                            if (otp != null) {
                                statusState.value = "OTP generado (simulado): $otp"
                            } else {
                                statusState.value = "Error generando OTP"
                            }
                        }
                    } else {
                        statusState.value = "Error creando cuenta"
                    }
                }
            }) {
                Text("Crear cuenta y solicitar OTP")
            }
        } else {
            OutlinedTextField(
                value = otpState.value,
                onValueChange = { otpState.value = it },
                label = { Text("OTP") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                statusState.value = "Verificando..."
                val acct = createdAccountId.value
                if (acct == null) {
                    statusState.value = "ID de cuenta no disponible"
                    return@Button
                }
                viewModel.verifyOtp(acct, otpState.value) { ok ->
                    statusState.value = if (ok) "Cuenta verificada" else "OTP inválido"
                    if (ok) onDone(acct)
                }
            }) {
                Text("Verificar OTP")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = statusState.value)
        TextButton(onClick = { onDone(null) }) {
            Text("Omitir registro por ahora")
        }
    }
}
