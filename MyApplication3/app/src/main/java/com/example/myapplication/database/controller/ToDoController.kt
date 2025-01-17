package com.example.myapplication.database.controller

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.myapplication.database.DbHelper
import com.example.myapplication.database.dataclass.ToDoDataClass

/**
 * Controller to interact with the database and it tables
 */
class ToDoController(context: Context) {
    //database you wanna work with
    private val dbHelper = DbHelper(context)

    /**
     * Insert a task in the database
     */
    fun insertToDo(task: ToDoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            // get the priority id from the other table
            val priorityId = getPriorityId( task.priority)
            //check if the id is valid
            if (priorityId == -1) {
                Log.e("ToDoController", "Invalid priority: ${task.priority}")
                return false
            }

            // ContentValues for the insert
            val values = ContentValues().apply {
                put("name", task.name)
                put("priority", priorityId) // Speichern der priorityId statt des priority-Strings
                put("endDate", task.endDate)
                put("description", task.description)
                put("status", task.status)
            }

            // insert it into the table
            val result = db.insert("tasks", null, values)
            result != -1L // Wenn result nicht -1L ist, war der Insert erfolgreich
        }
        //catch an exception
        catch (e: Exception) {
            Log.e("ToDoController", "Insert failed", e)
            false
        } finally {
            db.close() // Always close the db connection
        }
    }

    /**
     * function to update a task
     */
    fun updateToDo(task: ToDoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            //get the priority out of the table
            val priorityId = getPriorityId(task.priority)

            // checks if the priority is valid
            if (priorityId == -1) {
                Log.e("ToDoController", "Invalid priority: ${task.priority}")
                return false
            }

            // prepare ContentValues
            val values = ContentValues().apply {
                put("name", task.name)         // Aktualisiere den Namen
                put("priority", priorityId)    // Speichere die priorityId
                put("endDate", task.endDate)   // Aktualisiere das Enddatum
                put("description", task.description) // Aktualisiere die Beschreibung
                put("status", task.status)     // Aktualisiere den Status
            }

            //update the db but only when the id is correct
            val rowsUpdated = db.update(
                "tasks",
                values,
                "id = ?",
                arrayOf(task.id.toString())
            )

            Log.d("ToDoController", "Rows updated: $rowsUpdated for ToDo ID: ${task.id}")
            rowsUpdated > 0
        }
        //catch the excpetions
        catch (e: Exception) {
            Log.e("ToDoController", "Update failed", e)
            false
        }
        //Always close the db
        finally {
            db.close()
        }
    }



    /**
     * change the isCompleted status of an object
     */
    fun toggleToDo(task: ToDoDataClass): Boolean {
        val db = dbHelper.writableDatabase
        val toggle = if (task.status == 1) 0 else 1
        return try {
            // get the priority id out of the table
            val priorityId = getPriorityId( task.priority)

            // checks if the id is valid
            if (priorityId == -1) {
                Log.e("ToDoController", "Invalid priority: ${task.priority}")
                return false
            }
            //prepare the ContentValues
            val values = ContentValues().apply {
                put("name", task.name)
                put("priority", priorityId)
                put("endDate", task.endDate)
                put("description", task.description)
                put("status",toggle)
            }
            //update the db with the new status
            val result = db.update("tasks", values, "id = ?", arrayOf(task.id.toString()))
            Log.d("ToDoController", "Update result: $result, Student ID: ${task.id}")
            result > 0
        }
        //catch the exceptions
        catch (e: Exception) {
            Log.e("ToDoController", "Toggle failed", e)
            false
        }
        //always close the db
        finally {
            db.close()
        }
    }

    /**
     * function to delete a task
     */
    fun deleteToDo(taskId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            //delete the task
            val result = db.delete("tasks", "id = ?", arrayOf(taskId.toString()))
            result > 0
        }
        //catch the exceptions
        catch (e: Exception) {
            Log.e("ToDoController", "Delete failed", e)
            false
        }
        //always close the db
        finally {
            db.close()
        }
    }

    /**
     * get all todos from teh database that arent closed
     */
    fun getAllToDosOpen(): List<ToDoDataClass> {
        val db = dbHelper.readableDatabase
        val toDosOpen = mutableListOf<ToDoDataClass>()
        //sort the list first by the enddate and then by the priority
        val cursor = db.rawQuery("SELECT t.*, p.level AS priority FROM tasks t INNER JOIN priorities p ON t.priority = p.id WHERE t.status = ? ORDER BY t.endDate, t.priority",
            arrayOf("0"))
        try {
            //when you found a task with a status 0 make a todoItem out of it with the cursor
            if (cursor.moveToFirst()) {
                do {
                    val task = ToDoDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                    )
                    toDosOpen.add(task)
                } while (cursor.moveToNext())
            }
        }
        //catch the exceptions
        catch (e: Exception) {
            Log.e("ToDoController", "Fetching todos failed", e)
        }
        //always close the db and the cursor
        finally {
            cursor.close()
            db.close()
        }
        return toDosOpen
    }

    /**
     * get all todos from teh database that are closed
     */
    fun getAllToDoClose(): List<ToDoDataClass> {
        val db = dbHelper.readableDatabase
        val toDosClose = mutableListOf<ToDoDataClass>()
        //sort the list first by the enddate and then by the priority
        val cursor = db.rawQuery("SELECT t.*, p.level AS priority  FROM tasks t INNER JOIN priorities p ON t.priority = p.id WHERE t.status = ? ORDER BY t.endDate, t.priority",
            arrayOf("1"))
        try {
            if (cursor.moveToFirst()) {
                do {
                    //when you found a task with a status 0 make a todoItem out of it with the cursor
                    val task = ToDoDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                    )
                    toDosClose.add(task)
                } while (cursor.moveToNext())
            }
        }
        //catch the exceptions
        catch (e: Exception) {
            Log.e("ToDoController", "Fetching todos failed", e)
        }
        //always close the db and the cursor
        finally {
            cursor.close()
            db.close()
        }
        return toDosClose
    }

    /**
     * function to get the id of a priority
     */
    @SuppressLint("Range")
    fun getPriorityId(priority: String): Int {
        val db = dbHelper.readableDatabase
        // get the id by the priority
        val cursor = db.rawQuery("SELECT id FROM priorities WHERE level = ?", arrayOf(priority))
        var priorityId = -1

        if (cursor.moveToFirst()) {
            // create an int with the cursor
            priorityId = cursor.getInt(cursor.getColumnIndex("id"))
        }
        //close the cursor
        cursor.close()
        Log.d("ToDoControllerId", priorityId.toString())
        return priorityId
    }
}
