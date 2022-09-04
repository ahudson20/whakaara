package com.app.whakaara.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.whakaara.data.Alarm
import androidx.compose.material3.Switch

//@Composable
//fun CardContainer(
//    list: ArrayList<Alarm>
//) {
//    Column(
//        modifier = Modifier.padding(10.dp)
//    ) {
//        for(item in list) {
//            Card(time = item.time, day = item.day)
//        }
//    }
//}
//
//@Composable
//fun Card(
//    time: String,
//    day: String
//) {
//    val checkedState = remember { mutableStateOf(true) }
//    ElevatedCard(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(15.dp)
//            .clickable { }
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Column {
//                Text(
//                    text = time,
//                )
//                Text(
//                    text = day,
//                )
//            }
//            Spacer(Modifier.weight(1f))
//            Switch(
//                checked = checkedState.value,
//                onCheckedChange = { checkedState.value = it }
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CardContainerPreview() {
//    CardContainer(
//        list = arrayListOf(
//            Alarm("Mon", "7:30"),
//            Alarm("Tues", "9:00")
//        )
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CardPreview() {
//    Card("asd", "asd")
//}