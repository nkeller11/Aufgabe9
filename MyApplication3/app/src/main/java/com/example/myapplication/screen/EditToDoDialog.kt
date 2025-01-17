package com.example.myapplication.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.database.dataclass.ToDoDataClass
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * function to add/update/delete a task
 */
@Composable
fun EditToDoDialog(
    toDo: ToDoDataClass?,
    onDismiss: () -> Unit,
    onSave: (ToDoDataClass) -> Unit,
    onDelete: (ToDoDataClass) -> Unit
) {
    //attributes of a todoItem
    var name by remember { mutableStateOf(toDo?.name ?: "") }
    var priority by remember { mutableStateOf(toDo?.priority ?: "") }
    var endDate by remember { mutableStateOf(toDo?.endDate ?: "") }
    var description by remember { mutableStateOf(toDo?.description ?: "") }
    //variables for the datepicker
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")//
    val priorityOptions = listOf("Low", "Normal", "High")

    //dialog of the function
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (toDo == null) "Neuen Eintrag anlegen" else "Eintrag bearbeiten")
        },
        text = {
            //name of the task
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("name") },
                    modifier = Modifier.fillMaxWidth()
                )
                //Textfield to select a date
                OutlinedTextField(
                    value = endDate.format(dateFormatter),
                    onValueChange = {}, // TextField is read-only in this example
                    label = { Text("Select EndDate") },
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDatePickerVisible = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
                // RadioButton Gruppe für Prioritäten
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Priority", style = MaterialTheme.typography.bodyLarge)
                    priorityOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (priority == option),
                                    onClick = { priority = option },
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            //Radiobutton to select a priority
                            RadioButton(
                                selected = (priority == option),
                                onClick = null // Do nothing, `selectable` is responsible for click
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(text = option)
                        }
                    }
                }
                //textfield to change the description
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("description") },
                    modifier = Modifier.fillMaxWidth()
                )
                //datepicker to select a date
                if (isDatePickerVisible) {
                    DatePickerModal(
                        onDateSelected = { selectedDate ->
                            endDate = selectedDate?.let {
                                // Format in "dd.MM.yyyy"
                                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
                            } ?: ""
                            isDatePickerVisible = false
                        },
                        onDismiss = { isDatePickerVisible = false }
                    )
                }
            }
        },
        //button to close the dialog and update the task
        confirmButton = {
            Log.d("Dialog",priority)
            Button(onClick = {
                val updatedToDo =
                    ToDoDataClass(
                        id = toDo?.id ?: 0,
                        name = name,
                        priority = priority,
                        endDate = endDate,
                        description = description,
                        status = toDo?.status ?: 0
                    )
                    onSave(updatedToDo)
            }) {
                Text("Speichern")
            }
        },
        //button to close the dialog
        dismissButton = {
            if (toDo != null) {
                Button(onClick = { onDelete(toDo) }) {
                    Text("Löschen")
                }
            }
        }
    )
}


/**
 * function to create a datepicker
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}




