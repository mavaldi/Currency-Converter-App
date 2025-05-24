package com.example.currencyconverterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencyconverterapp.ui.theme.CurrencyConverterAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CurrencyConverterAppScreen()
                }
            }
        }
    }
}

enum class Currency(val symbol: String, val rateToUSD: Double) {
    IDR("Rp", 0.000065),  // 1 IDR = 0.000065 USD
    USD("$", 1.0),        // Base currency
    GBP("£", 1.22),       // 1 GBP = 1.22 USD
    EUR("€", 1.08),       // 1 EUR = 1.08 USD
    CNY("¥", 0.14)        // 1 CNY = 0.14 USD
}

@Composable
fun CurrencyConverterAppScreen() {
    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf(Currency.IDR) }
    var toCurrency by remember { mutableStateOf(Currency.USD) }
    var convertedAmount by remember { mutableStateOf(0.0) }

    val exchangeRates = listOf(
        "1 USD = ${(1 / Currency.IDR.rateToUSD).toInt()} IDR",
        "1 USD = ${(Currency.GBP.rateToUSD).toDouble()} GBP",
        "1 USD = ${(Currency.EUR.rateToUSD).toDouble()} EUR",
        "1 USD = ${(1 / Currency.CNY.rateToUSD).toDouble()} CNY"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Currency Converter",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Current Rates:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            exchangeRates.forEach { rate ->
                Text(rate, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Text(fromCurrency.symbol) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("From:", style = MaterialTheme.typography.bodyMedium)
                CurrencyDropdown(
                    selectedCurrency = fromCurrency,
                    onCurrencySelected = { fromCurrency = it }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("To:", style = MaterialTheme.typography.bodyMedium)
                CurrencyDropdown(
                    selectedCurrency = toCurrency,
                    onCurrencySelected = { toCurrency = it }
                )
            }
        }

        Button(
            onClick = {
                val inputAmount = amount.toDoubleOrNull() ?: 0.0
                // Convert to USD first, then to target currency
                val amountInUSD = inputAmount * fromCurrency.rateToUSD
                convertedAmount = amountInUSD / toCurrency.rateToUSD
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Converted Amount:", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${toCurrency.symbol} ${"%.2f".format(convertedAmount)}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 28.sp
            )
        }

        OutlinedButton(
            onClick = {
                val temp = fromCurrency
                fromCurrency = toCurrency
                toCurrency = temp
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Swap Currencies")
        }
    }
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedCurrency.name)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Currency.values().forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency.name) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyConverterAppPreview() {
    CurrencyConverterAppTheme {
        CurrencyConverterAppScreen()
    }
}