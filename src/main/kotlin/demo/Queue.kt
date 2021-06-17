package demo


import kotlin.collections.mutableListOf
import java.util.concurrent.*
import java.util.*


object ConcurrentQ {
    private val myQueue: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue<String>()
    private val queueSize: ConcurrentHashMap<Int,Int> = ConcurrentHashMap(mapOf(0 to 0))

    fun put(element: String) {
        print("[SERVER LOG]")
        println(queueSize)
        myQueue.add(element)
        queueSize.computeIfPresent(0, {k:Int, v:Int -> v + 1})
    }
    
    
    fun extract(lines: Int): MutableList<String> {
        val extracted: MutableList<String> = arrayListOf()
        val size: Int = queueSize.getOrDefault(0,0)
        if (lines < size) {
            for (i in 1..lines) {
                extracted.add(myQueue.poll())
                queueSize.computeIfPresent(0, {k:Int, v:Int -> v - 1})
            }
        }
        return extracted
    }

}