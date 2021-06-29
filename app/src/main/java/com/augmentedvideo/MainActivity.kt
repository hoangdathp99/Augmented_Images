package com.augmentedvideo

import android.app.ActivityManager
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val openGlVersion by lazy {
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (openGlVersion.toDouble() >= MIN_OPEN_GL_VERSION) {
            class TestAsync(private val context: Context) : AsyncTask<URL?, Int?, ArrayList<ByteArray>>() {
                var TAG = javaClass.simpleName

//            interface AsyncResponse {
//                fun processFinish(output: ByteArray?)
//            }
//
//            var delegate: AsyncResponse? = null
//
//            fun MyAsyncTask(delegate: AsyncResponse?) {
//                this.delegate = delegate
//            }

                override fun onPreExecute() {
                    super.onPreExecute()
                    Log.d("$TAG PreExceute", "On pre Exceute......")
                }

                override fun doInBackground(vararg params: URL?): ArrayList<ByteArray> {
                    Log.d("$TAG DoINBackGround", "On doInBackground...")
                    val url= URL("https://aradmin-web.s3-ap-southeast-1.amazonaws.com/arcoreimg/fundoo_images.imgdb")
//                    val data = url.readBytes();
                    val allByteArray = ArrayList<ByteArray>()
                    for (i in 0 until 1) {
                        // some byte array
                        val data = url.readBytes();

                        // add to list
                        allByteArray.add(data)
                    }
                    
                    return allByteArray
                }

                override fun onPostExecute(result: ArrayList<ByteArray>) {
                    super.onPostExecute(result)
                    val length = result?.size
//                delegate!!.processFinish(result)
                    Log.d("$TAG onPostExecute", length.toString())
                    Toast.makeText(context, "welcome", Toast.LENGTH_SHORT).show()
                    val bundle =  Bundle();
                    var i=0
                    for (test in result){
                        bundle.putByteArray("data"+i, test);
                        bundle.putString("test", "aloalo")
                        i+=1
                    }
                    val arfrg = ArVideoFragment()
                    arfrg.arguments = bundle;
                    supportFragmentManager.inTransaction { replace(R.id.fragmentContainer, arfrg) }
//            val bundle =  Bundle();
//            bundle.putString("String", "String text");
//            val arfrg = ArVideoFragment
//            arfrg.arguments = bundle;
//            Log.d("$TAG onPostExecute", "" + status)
                    Log.d("$TAG onPostExecute", "" + arfrg.toString())


                }
            }
            TestAsync(this).execute()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Device is not supported")
                .setMessage("OpenGL ES 3.0 or higher is required. The device is running OpenGL ES $openGlVersion.")
                .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
                .show()
        }
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    companion object {
        private const val MIN_OPEN_GL_VERSION = 3.0
    }
}