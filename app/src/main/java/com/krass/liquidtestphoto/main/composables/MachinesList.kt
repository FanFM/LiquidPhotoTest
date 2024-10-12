package com.krass.liquidtestphoto.main.composables

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

lateinit var machines: MutableMap<String, String>



@Composable
fun MachinesList(_machines: MutableMap<String, String>){
    machines = _machines
    machines.toList().sortedBy { (key, value) -> value }.toMap()
    LazyColumn {
        for ((key, value) in machines){
            item {
                Row {
                    TextField(value = key, onValueChange = { }, singleLine = true, modifier = Modifier.weight(1f).padding(2.dp),)
                    TextField(value = value, onValueChange = { }, singleLine = true, modifier = Modifier.weight(1f).padding(2.dp),)
                }
            }
        }
    }
}

@Preview
@Composable
fun MachinesListPreview(){
    MachinesList(mutableMapOf<String, String>("Key" to "Value"))
}