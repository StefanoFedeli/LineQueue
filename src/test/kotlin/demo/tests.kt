package demo 

import kotlin.test.Test
import kotlin.test.assertEquals

class TestQueue {
    
    @Test fun check_in_out() {
        val storage = ConcurrentQ
        storage.put("Blanco")
        assertEquals("Blanco",storage.extract(1)[0])
        assertEquals(0,storage.extract(1).size)
    }
    
}
