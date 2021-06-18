/**
* @author  Stefano Fedeli
* @version 1.0
* @since   2021-06-17 
*/
package demo


import kotlin.collections.mutableListOf
import java.util.concurrent.*
import java.util.*


/**
 * ConcurrenQ class keep the status of a FIFO queue as a singleton. 
 * Note that this object sion is thread safe. 
 */
object ConcurrentQ {

    /**
    * A Thread-safe FIFO queue.
    */
    private val myQueue: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue<String>()
    /**
    * A Thread-safe map that keeps the size of the queue
    */
    private val queueSize: ConcurrentHashMap<Int,Int> = ConcurrentHashMap(mapOf(0 to 0))

    /**
    * Update the queue with a new element and increase the size
    *
    * @param element string to add to the queue.
    */
    fun put(element: String) {
        print("[SERVER LOG]")
        println(queueSize)
        myQueue.add(element)
        queueSize.computeIfPresent(0, {k:Int, v:Int -> v + 1})
    }
    
    /**
    * Returns lines element from the FIFO queue if the queue contains at least lines elements.
    *
    * @param lines number of element to retrive. 
    * @return MutableList which contains the lines element extracted.
    */
    fun extract(lines: Int): MutableList<String> {
        val extracted: MutableList<String> = arrayListOf()
        val size: Int = queueSize.getOrDefault(0,0)
        if (lines <= size) {
            for (i in 1..lines) {
                extracted.add(myQueue.poll())
                queueSize.computeIfPresent(0, {k:Int, v:Int -> v - 1})
            }
        }
        return extracted
    }

}