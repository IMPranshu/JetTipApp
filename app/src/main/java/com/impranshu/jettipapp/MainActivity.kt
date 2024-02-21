package com.impranshu.jettipapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impranshu.jettipapp.components.InputField
import com.impranshu.jettipapp.ui.theme.JetTipAppTheme
import com.impranshu.jettipapp.utils.calculateTotalPerPerson
import com.impranshu.jettipapp.utils.calculateTotalTip
import com.impranshu.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

                MyApp {
                     TopHeader()
                }
            }

    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    JetTipAppTheme {
        Surface(

            color = MaterialTheme.colorScheme.background
        ) {
            content()

        }
    }

}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface {
        Column(modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
            .background(Color(0xFFE9D7F7))
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                val total = "%.2f".format(totalPerPerson)
                Text(text = "Total Per Person",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(text = "$$total",
                    style = TextStyle(fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }

}

@Preview
@Composable
fun MainContent(){

    val splitByState = remember {
        mutableIntStateOf(1)
    }
    val splitRange = IntRange(1,20)
    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }
        BillForm(
            splitByState = splitByState,
            splitRange = splitRange,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
            ){billAmt ->
            Log.d("BillAmt", "MainContent: $billAmt")



    }






}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    splitRange: IntRange = 1..20,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {}
    ){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()

    }
    val keyboardController = LocalSoftwareKeyboardController.current

    var sliderPositionState by remember {
        mutableFloatStateOf(0.0f)
    }

    val tipPercentage = (sliderPositionState * 100).toInt()
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current



    Column {
        TopHeader(totalPerPersonState.value)


    Surface(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.Black)
    ) {
        Column(
            modifier =
            Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start

        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Your Bill Amount",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                }
            )
            if (validState){
            Row(
                modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Split",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(120.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    RoundIconButton(imageVector = Icons.Default.Remove,
                        onClick = {
                            if (splitByState.value > 1) {
                                splitByState.value -= 1
                            } else {
                                splitByState.value = 1
                                Toast.makeText(
                                    context,
                                    "Atleast 1 person is required.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage
                            )
                        })
                    Text(
                        text = "${splitByState.value}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 13.dp, end = 13.dp)
                    )
                    RoundIconButton(imageVector = Icons.Default.Add,
                        onClick = {
                            if (splitByState.value < splitRange.last) {
                                splitByState.value += 1
                            } else {
                                splitByState.value = 20
                                Toast.makeText(
                                    context,
                                    "Max. 20 people supported",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage
                            )

                        }
                    )
                }
            }
            // Top Row

            Row(
                modifier = Modifier
                    .padding(horizontal = 3.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Tip",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(
                    text = "$${tipAmountState.value}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = "$tipPercentage %")
                Spacer(modifier = Modifier.height(14.dp))

                // Slider
                Slider(
                    value = sliderPositionState,
                    onValueChange = { newVal ->

                        sliderPositionState = newVal
                        tipAmountState.value = calculateTotalTip(
                            totalBill = totalBillState.value.toDouble(),
                            tipPercentage = tipPercentage
                        )

                        totalPerPersonState.value = calculateTotalPerPerson(
                            totalBill = totalBillState.value.toDouble(),
                            splitBy = splitByState.value,
                            tipPercentage = tipPercentage
                        )

                    },

                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    onValueChangeFinished = {},
                    interactionSource = interactionSource

                )
            }

            }else{
            Box() {

            }
            }

        }
    }
    }

}




//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipAppTheme {
        MyApp {
            TopHeader()
        }

    }
}